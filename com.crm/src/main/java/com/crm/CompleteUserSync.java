package com.crm;

import com.crm.repos.UserRepo;
import com.crm.entites.User;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CompleteUserSync {

    private final UserRepo userRepo;

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            return "";
        }

        // Strip all non-digit characters (e.g. spaces, parentheses, hyphens)
        String digits = phone.replaceAll("\\D", "");

        // If it's a standard 10-digit US number, prepend country code +1
        if (digits.length() == 10) {
            return "+1" + digits;
        }

        // If it already includes the 1 country code (11 digits starting with 1)
        if (digits.length() == 11 && digits.startsWith("1")) {
            return "+" + digits;
        }

        // Fallback: return formatted digits with + if non-standard length
        return "+" + digits;
    }

    public CompleteUserSync(UserRepo userRepo) {
        this.userRepo = userRepo;

        List<User> users = userRepo.findUsersPhoneBlank();

        System.out.println("[CompleteUserSync] Starting user data fill in process");

        for (int i = 0; i < users.size(); i++) {
            User currentUser = users.get(i);
            
            // Pass the user ID / Chart # to Dentrix action script
            String patientIdArg = String.valueOf(currentUser.getId());
            
            DentrixAction.ReturnData getPatientDataAction = DentrixAction.run("getPatientData", patientIdArg);

            if (getPatientDataAction.returnCode == -1) {
                System.out.println("[CompleteUserSync] getPatientData failed to run for reason: " + getPatientDataAction.stderr);
                continue;
            }

            if (getPatientDataAction.returnCode != 0) {
                System.out.println("[CompleteUserSync] getPatientData returned a non 0 status code");
                continue;
            }

            try {
                Map<String, String> patientData = new ObjectMapper().readValue(
                        getPatientDataAction.stdout,
                        new TypeReference<Map<String, String>>() {}
                );

                currentUser.setPhone(formatPhoneNumber(patientData.get("Mobile Phone")));
                userRepo.save(currentUser);
                System.out.println("[CompleteUserSync] User phone number saved for ID: " + currentUser.getId());

            } catch (Exception e) {
                System.out.println("[CompleteUserSync] patientData failed to parse: " + e);
            }
        }
    }
}
