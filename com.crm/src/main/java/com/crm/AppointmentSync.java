package com.crm;

import com.crm.repos.UserRepo;
import com.crm.entites.User;
import com.crm.repos.AppointmentRepo;
import com.crm.entites.Appointment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Locale;                        
import java.time.*;
import java.time.format.DateTimeFormatter;        


@Component
public class AppointmentSync {
    
    private UserRepo userRepo;
    private AppointmentRepo appointmentRepo;
    private int daysAhead = 30;    

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

    private void parseJsonToDb(String jsonData, LocalDate currentDate) {

    ObjectMapper mapper = new ObjectMapper();

    try {
        // Parse the new JSON structure: {"schedule": [ { ... }, { ... } ]}
        JsonNode rootNode = mapper.readTree(jsonData);
        JsonNode scheduleNode = rootNode.get("schedule");

        List<Map<String, String>> rows = mapper.readValue(
                scheduleNode.traverse(),
                new TypeReference<List<Map<String, String>>>() {}
        );

        for (Map<String, String> row : rows) {

            System.out.println("[AppointmentSync] Row data: " + row);

            String chart = row.get("Chart #");

            if (chart == null || chart.isBlank()) {
                System.out.println("[AppointmentSync] Skipping blank row");
                continue;
            }

            long userId = Long.parseLong(chart);

            Optional<User> user = userRepo.findById(userId);

            if (!user.isPresent()) {

                String fullName = row.get("Name");
                System.out.println("[AppointmentSync] Adding user '" + fullName + "' to database");

                User newUser = new User();
                newUser.setId(userId);

                // Parse "Last, First" into firstName and lastName safely
                if (fullName != null && fullName.contains(",")) {
                    String[] parts = fullName.split(",", 2);
                    newUser.setLastName(parts[0].trim());
                    newUser.setFirstName(parts[1].trim());
                } else if (fullName != null) {
                    newUser.setLastName(fullName.trim());
                    newUser.setFirstName("");
                }

                newUser.setPrefName(row.get("Pref Name"));

                // Convert (xxx) xxx-xxxx -> +1xxxxxxxxxx
                String rawPhone = row.get("Mobile Phone");
                newUser.setPhone(formatPhoneNumber(rawPhone));

                userRepo.save(newUser);
            }

            LocalTime localTime = LocalTime.parse(
                    row.get("Time"),
                    DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
            );

            Instant timeInstant = LocalDateTime
                    .of(currentDate, localTime)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            Optional<Appointment> existingAppointment =
                    appointmentRepo.findByUserIdAndTime(userId, timeInstant);

            if (!existingAppointment.isPresent()) {

                System.out.println("[AppointmentSync] Adding appointment"
                        + "\n\tfor user '" + row.get("Name") + "'"
                        + "\n\tat time '" + row.get("Time") + "'"
                        + "\n\tfor reason '" + row.get("Reason") + "'"
                );

                Appointment newAppointment = new Appointment();

                newAppointment.setUserId(userId);
                newAppointment.setTime(timeInstant);
                newAppointment.setReason(row.get("Reason"));
                newAppointment.setJsonStatus(true);

                appointmentRepo.save(newAppointment);
            }
        }

        // Remove appointments that no longer exist in the JSON

        List<Appointment> appointments = appointmentRepo.findByDay(currentDate);

        for (Appointment dbAppointment : appointments) {

            if (!dbAppointment.fromJson())
                continue;

            boolean found = false;

            for (Map<String, String> row : rows) {

                long userId = Long.parseLong(row.get("Chart #"));

                LocalTime localTime = LocalTime.parse(
                        row.get("Time"),
                        DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
                );

                Instant timeInstant = LocalDateTime
                        .of(currentDate, localTime)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

                if (dbAppointment.getUserId() == userId
                        && dbAppointment.getTime().equals(timeInstant)
                        && dbAppointment.getReason().equals(row.get("Reason"))) {

                    found = true;
                    break;
                }
            }

            if (!found) {

                System.out.println("[AppointmentSync] Removing cancelled appointment for user "
                        + dbAppointment.getUserId());

                appointmentRepo.delete(dbAppointment);

            }
        }

    } catch (Exception e) {

        System.out.println("[AppointmentSync] Error parsing schedule JSON");

        e.printStackTrace();

    }
}

    public AppointmentSync(UserRepo userRepo, AppointmentRepo appointmentRepo) {

        this.userRepo = userRepo;
        this.appointmentRepo = appointmentRepo;
            
        new Thread(() -> {
            System.out.println("[AppointmentSync] Starting Dentrix schedule sync");
            
            while (true) {
                try {Thread.sleep(5000);} catch (Exception e) {
                    System.out.println("[AppointmentSync] Sleep error, " + e);                    
                }
                
                for (int i = 0; i < daysAhead; i++) {
                    LocalDate currentDate = LocalDate.now().plusDays(i);
                    String currentDateFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    System.out.println("[AppointmentSync] Getting schedule for " + currentDateFormatted + ", which is " + i + " day(s) ahead of today");
                    try {Thread.sleep(1000);} catch (Exception e) {
                        System.out.println("[AppointmentSync] Sleep error, " + e);                    
                    }
                    
                    // Fixed: called getDayOfMonth() and getYear() on currentDate, and used String values to fit Map<String, String>
                    DentrixAction.ReturnData getScheduleAction = DentrixAction.run(
                        "getSchedule", 
                        Map.of(
                            "month", String.valueOf(currentDate.getMonthValue()), 
                            "day", String.valueOf(currentDate.getDayOfMonth()), 
                            "year", String.valueOf(currentDate.getYear())
                        )
                    );
                    int returnCode = getScheduleAction.returnCode;
                    

                    if (returnCode == -1) {
                        System.out.println("[AppointmentSync] DentrixAction ran into an issue, " + getScheduleAction.stderr);            

                    } else if (returnCode == 0) {
                        
                        if (!getScheduleAction.stdout.equals("")) {
                            System.out.println("[AppointmentSync] Parsing getSchedule stdout");
                            parseJsonToDb(getScheduleAction.stdout, currentDate);
                        } else {
                            System.out.println("[AppointmentSync].Error For some reason getSchedule helper didnt send stdout data even though it returned 0");
                        }
                    } else if (returnCode == 10) {
                        System.out.println("[AppointmentSync] It seems as the day, '" + currentDate.toString() + "' has no active schedule");
                    } else {
                        System.out.println("[AppointmentSync].Error getSchedule failed with code " + returnCode);                
                    }
                    
                }
                            
            }
        }).start();
        
    }

}
