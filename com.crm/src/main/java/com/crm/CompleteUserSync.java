package com.crm;

import com.crm.repos.UserRepo;
import com.crm.entites.User;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
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
        if (birthdayStr == null || birthdayStr.isBlank() || birthdayStr.equals("null")) {
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
        // Check for empty placeholders specified in requirements
        return trimmed.equals("Subscriber ID #:") || trimmed.equals("Carrier:") || trimmed.equals(":");
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
                    
                    // Pass firstName and lastName using the new Map parameter format for DentrixAction
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

                        // Extract root-level insurance details
                        String primaryInsName = rootNode.path("primaryInsName").asText(null);
                        String primaryInsId = rootNode.path("primaryInsId").asText(null);
                        String secondaryInsName = rootNode.path("secondaryInsName").asText(null);
                        String secondaryInsId = rootNode.path("secondaryInsId").asText(null);

                        // Set Primary Insurance if valid
                        if (!isInvalidInsuranceField(primaryInsName)) {
                            currentUser.setPrimaryInsuranceName(primaryInsName.trim());
                        }
                        if (!isInvalidInsuranceField(primaryInsId)) {
                            try {
                                currentUser.setPrimaryInsuranceId(Long.parseLong(primaryInsId.replaceAll("\\D", "")));
                            } catch (NumberFormatException ignored) {}
                        }

                        // Set Secondary Insurance if valid
                        if (!isInvalidInsuranceField(secondaryInsName)) {
                            currentUser.setSecondaryInsuranceName(secondaryInsName.trim());
                        }
                        if (!isInvalidInsuranceField(secondaryInsId)) {
                            try {
                                currentUser.setSecondaryInsuranceId(Long.parseLong(secondaryInsId.replaceAll("\\D", "")));
                            } catch (NumberFormatException ignored) {}
                        }

                        // Extract user info from the userInfo array
                        JsonNode userInfoArray = rootNode.path("userInfo");
                        if (userInfoArray.isArray() && userInfoArray.size() > 0) {
                            JsonNode userInfo = userInfoArray.get(0);

                            // Phone
                            String rawPhone = userInfo.path("Phone").asText("");
                            String formattedPhone = formatPhoneNumber(rawPhone);
                            if (!formattedPhone.isBlank()) {
                                currentUser.setPhone(formattedPhone);
                            }

                            // Preferred Name (handle "(null)" string safely)
                            String prefName = userInfo.path("Preferred Name").asText("");
                            if (prefName != null && !prefName.equalsIgnoreCase("(null)") && !prefName.isBlank()) {
                                currentUser.setPrefName(prefName);
                            }

                            // Birthday
                            String birthdayStr = userInfo.path("Birthday").asText("");
                            LocalDate dob = parseBirthday(birthdayStr);
                            if (dob != null) {
                                currentUser.setDob(dob); // Ensure setDob exists in your User entity
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
