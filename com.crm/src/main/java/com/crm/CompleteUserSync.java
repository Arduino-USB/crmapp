package com.crm;

import com.crm.repos.UserRepo;
import com.crm.entites.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

//@Component
public class CompleteUserSync {

    private final UserRepo userRepo;

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            return "";
        }

        // Strip all non-digit characters (e.g. spaces, parentheses, hyphens, tags like [H])
        String digits = phone.replaceAll("\\D", "");

        if (digits.length() == 10) {
            return "+1" + digits;
        }

        if (digits.length() == 11 && digits.startsWith("1")) {
            return "+" + digits;
        }

        return digits.isEmpty() ? "" : "+" + digits;
    }

    private LocalDate parseBirthday(String birthdayStr) {
        if (birthdayStr == null || birthdayStr.isBlank() || birthdayStr.equals("null") || birthdayStr.equalsIgnoreCase("[redacted]")) {
            return null;
        }
        try {
            // Handles format like "2/26/1952" or "02/26/1952"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(birthdayStr, formatter);
        } catch (Exception e) {
            try {
                // Fallback for single digit or standard formats
                return LocalDate.parse(birthdayStr);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private boolean isInvalidInsuranceField(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String trimmed = value.trim();
        return trimmed.equals("Subscriber ID #:") || trimmed.equals("Carrier:") || trimmed.equals(":");
    }

    private static class ParsedName {
        String firstName;
        String lastName;
        ParsedName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private ParsedName parseAndCleanName(String rawFirstName, String rawLastName) {
        String fn = rawFirstName != null ? rawFirstName.trim() : "";
        String ln = rawLastName != null ? rawLastName.trim() : "";

        // If rawFirstName contains a comma or looks like "Last, First N"
        if (fn.contains(",")) {
            String[] parts = fn.split(",", 2);
            ln = parts[0].trim();
            fn = parts[1].trim();
        }

        // Clean middle initial/name from firstName (e.g. "Yolanda M" -> "Yolanda")
        if (!fn.isEmpty()) {
            String[] words = fn.split("\\s+");
            if (words.length > 1) {
                String lastWord = words[words.length - 1];
                if (lastWord.length() <= 2 && (lastWord.matches("[A-Z]\\.?") || words.length == 2)) {
                    fn = words[0];
                } else if (words.length == 2) {
                    fn = words[0];
                }
            }
        }

        if (ln.contains(",")) {
            ln = ln.split(",")[0].trim();
        }

        return new ParsedName(fn, ln);
    }

    public CompleteUserSync(UserRepo userRepo) {
        this.userRepo = userRepo;

        System.out.println("[CompleteUserSync] Starting user data fill in process");
        
        new Thread(() -> {
            while (true) {
                try { 
                    Thread.sleep(5000); 
                } catch (Exception e) {
                    System.out.println("[CompleteUserSync] Sleep error, " + e);
                }

                List<User> users = userRepo.findUsersIncompleteData();
                for (int i = 0; i < users.size(); i++) {
                    User currentUser = users.get(i);
                    
                    Map<String, String> argMap = Map.of(
                            "ptFirstName", currentUser.getFirstName(),
                            "ptLastName", currentUser.getLastName()
                    );
                    
                    DentrixAction.ReturnData getPatientDataAction = DentrixAction.run("getPatientData", argMap);

                    if (getPatientDataAction.returnCode == -1) {
                        System.out.println("[CompleteUserSync] getPatientData failed to run for reason: " + getPatientDataAction.stderr);
                        continue;
                    }

                    if (getPatientDataAction.returnCode != 0) {
                        System.out.println("[CompleteUserSync] getPatientData returned a non 0 status code");
                        continue;
                    }

                    System.out.println("[CompleteUserSync] getPatientData returned : " + getPatientDataAction.stdout);

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(getPatientDataAction.stdout);

                        // Extract user info from the userInfo array
                        JsonNode userInfoArray = rootNode.path("userInfo");
                        ParsedName cleanedUser = new ParsedName("", "");
                        LocalDate patientDob = null;

                        if (userInfoArray.isArray() && userInfoArray.size() > 0) {
                            JsonNode userInfo = userInfoArray.get(0);

                            String rawFn = userInfo.path("First Name").asText("");
                            String rawLn = userInfo.path("Last Name").asText("");
                            cleanedUser = parseAndCleanName(rawFn, rawLn);

                            if (!cleanedUser.firstName.isBlank()) {
                                currentUser.setFirstName(cleanedUser.firstName);
                            }
                            if (!cleanedUser.lastName.isBlank()) {
                                currentUser.setLastName(cleanedUser.lastName);
                            }

                            // Phone
                            String rawPhone = userInfo.path("Phone").asText("");
                            String formattedPhone = formatPhoneNumber(rawPhone);
                            if (!formattedPhone.isBlank()) {
                                currentUser.setPhone(formattedPhone);
                            }

                            // Preferred Name
                            String prefName = userInfo.path("Preferred Name").asText("");
                            if (prefName != null && !prefName.equalsIgnoreCase("(null)") && !prefName.isBlank()) {
                                currentUser.setPrefName(prefName);
                            }

                            // Patient Birthday
                            String birthdayStr = userInfo.path("Birthday").asText("");
                            patientDob = parseBirthday(birthdayStr);
                            if (patientDob != null) {
                                currentUser.setDob(patientDob);
                            }
                        }

                        // Extract root-level insurance details & flags
                        String primaryInsName = rootNode.path("primaryInsName").asText(null);
                        String primaryInsId = rootNode.path("primaryInsId").asText(null);
                        String secondaryInsName = rootNode.path("secondaryInsName").asText(null);
                        String secondaryInsId = rootNode.path("secondaryInsId").asText(null);

                        JsonNode primarySubNode = rootNode.path("primarySub");
                        String primarySubStr = (primarySubNode.isMissingNode() || primarySubNode.isNull()) ? null : primarySubNode.asText();

                        JsonNode secondarySubNode = rootNode.path("secondarySub");
                        String secondarySubStr = (secondarySubNode.isMissingNode() || secondarySubNode.isNull()) ? null : secondarySubNode.asText();

                        boolean isPtSubPrimary = rootNode.path("isPtSubPrimary").asBoolean(false);
                        boolean isPtSubSecondary = rootNode.path("isPtSubSecondary").asBoolean(false);

                        // --- Process Primary Insurance ---
                        boolean hasPrimary = isPtSubPrimary || 
                                             (!isInvalidInsuranceField(primaryInsName)) || 
                                             (!isInvalidInsuranceField(primaryInsId)) || 
                                             (primarySubStr != null && !primarySubStr.isBlank() && !primarySubStr.equalsIgnoreCase("null"));

                        if (hasPrimary) {
                            if (!isInvalidInsuranceField(primaryInsName)) {
                                currentUser.setPrimaryInsuranceName(primaryInsName.trim());
                            }
                            
                            String primaryInsIdStr = null;
                            if (!isInvalidInsuranceField(primaryInsId)) {
                                primaryInsIdStr = primaryInsId.trim();
                                currentUser.setPrimaryInsuranceId(primaryInsIdStr);
                            }

                            if (isPtSubPrimary) {
                                currentUser.setPrimarySubFirstName(cleanedUser.firstName);
                                currentUser.setPrimarySubLastName(cleanedUser.lastName);
                                currentUser.setPrimarySubDob(patientDob);
                                currentUser.setPrimarySubMemberId(primaryInsIdStr);
                            } else if (primarySubStr != null && !primarySubStr.isBlank() && !primarySubStr.equalsIgnoreCase("null")) {
                                String subNamePart = primarySubStr.split("/")[0].trim();
                                ParsedName subParsed = parseAndCleanName(subNamePart, "");

                                Map<String, String> subArgMap = Map.of(
                                        "ptFirstName", subParsed.firstName,
                                        "ptLastName", subParsed.lastName
                                );
                                DentrixAction.ReturnData subDataAction = DentrixAction.run("getPatientData", subArgMap);

                                if (subDataAction.returnCode == 0) {
                                    try {
                                        JsonNode subRoot = mapper.readTree(subDataAction.stdout);
                                        JsonNode subUserInfoArray = subRoot.path("userInfo");
                                        if (subUserInfoArray.isArray() && subUserInfoArray.size() > 0) {
                                            JsonNode subUserInfo = subUserInfoArray.get(0);
                                            ParsedName cleanSub = parseAndCleanName(
                                                    subUserInfo.path("First Name").asText(""),
                                                    subUserInfo.path("Last Name").asText("")
                                            );
                                            currentUser.setPrimarySubFirstName(cleanSub.firstName);
                                            currentUser.setPrimarySubLastName(cleanSub.lastName);
                                            LocalDate subDob = parseBirthday(subUserInfo.path("Birthday").asText(""));
                                            if (subDob != null) {
                                                currentUser.setPrimarySubDob(subDob);
                                            }
                                        } else {
                                            currentUser.setPrimarySubFirstName(subParsed.firstName);
                                            currentUser.setPrimarySubLastName(subParsed.lastName);
                                        }
                                    } catch (Exception e) {
                                        currentUser.setPrimarySubFirstName(subParsed.firstName);
                                        currentUser.setPrimarySubLastName(subParsed.lastName);
                                    }
                                } else {
                                    currentUser.setPrimarySubFirstName(subParsed.firstName);
                                    currentUser.setPrimarySubLastName(subParsed.lastName);
                                }
                                if (primaryInsIdStr != null) {
                                    currentUser.setPrimarySubMemberId(primaryInsIdStr);
                                }
                            }
                        }

                        // --- Process Secondary Insurance ---
                        boolean hasSecondary = isPtSubSecondary || 
                                               (!isInvalidInsuranceField(secondaryInsName)) || 
                                               (!isInvalidInsuranceField(secondaryInsId)) || 
                                               (secondarySubStr != null && !secondarySubStr.isBlank() && !secondarySubStr.equalsIgnoreCase("null"));

                        if (hasSecondary) {
                            if (!isInvalidInsuranceField(secondaryInsName)) {
                                currentUser.setSecondaryInsuranceName(secondaryInsName.trim());
                            }
                            
                            String secondaryInsIdStr = null;
                            if (!isInvalidInsuranceField(secondaryInsId)) {
                                secondaryInsIdStr = secondaryInsId.trim();
                                currentUser.setSecondaryInsuranceId(secondaryInsIdStr);
                            }

                            if (isPtSubSecondary) {
                                currentUser.setSecondarySubFirstName(cleanedUser.firstName);
                                currentUser.setSecondarySubLastName(cleanedUser.lastName);
                                currentUser.setSecondarySubDob(patientDob);
                                currentUser.setSecondarySubMemberId(secondaryInsIdStr);
                            } else if (secondarySubStr != null && !secondarySubStr.isBlank() && !secondarySubStr.equalsIgnoreCase("null")) {
                                String subNamePart = secondarySubStr.split("/")[0].trim();
                                ParsedName subParsed = parseAndCleanName(subNamePart, "");

                                Map<String, String> subArgMap = Map.of(
                                        "ptFirstName", subParsed.firstName,
                                        "ptLastName", subParsed.lastName
                                );
                                DentrixAction.ReturnData subDataAction = DentrixAction.run("getPatientData", subArgMap);

                                if (subDataAction.returnCode == 0) {
                                    try {
                                        JsonNode subRoot = mapper.readTree(subDataAction.stdout);
                                        JsonNode subUserInfoArray = subRoot.path("userInfo");
                                        if (subUserInfoArray.isArray() && subUserInfoArray.size() > 0) {
                                            JsonNode subUserInfo = subUserInfoArray.get(0);
                                            ParsedName cleanSub = parseAndCleanName(
                                                    subUserInfo.path("First Name").asText(""),
                                                    subUserInfo.path("Last Name").asText("")
                                            );
                                            currentUser.setSecondarySubFirstName(cleanSub.firstName);
                                            currentUser.setSecondarySubLastName(cleanSub.lastName);
                                            LocalDate subDob = parseBirthday(subUserInfo.path("Birthday").asText(""));
                                            if (subDob != null) {
                                                currentUser.setSecondarySubDob(subDob);
                                            }
                                        } else {
                                            currentUser.setSecondarySubFirstName(subParsed.firstName);
                                            currentUser.setSecondarySubLastName(subParsed.lastName);
                                        }
                                    } catch (Exception e) {
                                        currentUser.setSecondarySubFirstName(subParsed.firstName);
                                        currentUser.setSecondarySubLastName(subParsed.lastName);
                                    }
                                } else {
                                    currentUser.setSecondarySubFirstName(subParsed.firstName);
                                    currentUser.setSecondarySubLastName(subParsed.lastName);
                                }
                                if (secondaryInsIdStr != null) {
                                    currentUser.setSecondarySubMemberId(secondaryInsIdStr);
                                }
                            }
                        }

                        userRepo.save(currentUser);
                        System.out.println("[CompleteUserSync] User data updated successfully for ID: " + currentUser.getId());

                    } catch (Exception e) {
                        System.out.println("[CompleteUserSync] patientData failed to parse: " + e);
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
