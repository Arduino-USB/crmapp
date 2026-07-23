package com.crm;

import com.crm.repos.UserRepo;
import com.crm.repos.AppointmentRepo;
import com.crm.repos.ReminderRepo;
import com.crm.repos.ResponseRepo;
import com.crm.entites.Appointment;
import com.crm.entites.Reminder;
import com.crm.entites.Response;
import com.crm.entites.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.List;

@Component
public class AutomatedTextMgr {

    private final UserRepo userRepo;
    private final AppointmentRepo appointmentRepo;
    private final ReminderRepo reminderRepo;
    private final ResponseRepo responseRepo;
    private final TextMgr textMgr;
    private final ActionMgr actionMgr;

    public AutomatedTextMgr(
            UserRepo userRepo,
            AppointmentRepo appointmentRepo,
            ReminderRepo reminderRepo,
            ResponseRepo responseRepo,
            TextMgr textMgr,
            ActionMgr actionMgr
    ) {
        this.userRepo = userRepo;
        this.appointmentRepo = appointmentRepo;
        this.reminderRepo = reminderRepo;
        this.responseRepo = responseRepo;
        this.textMgr = textMgr;
        this.actionMgr = actionMgr;

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.println("[AutomatedTextMgr] Error sleeping");
                }

                List<Appointment> appointments = appointmentRepo.findAppointmentPhoneNotBlank();
                List<Reminder> reminders = reminderRepo.findAll();
                Instant currentTime = Instant.now();

                // === 1. Handle customer replies ===
                for (Appointment appointment : appointments) {
                    Optional<User> userOpt = userRepo.findById(appointment.getUserId());
                    if (userOpt.isPresent()) {
                        handleCustomerReply(appointment, userOpt.get());
                    }
                }

                // === 2. Send appointment reminders ===
                for (Appointment appointment : appointments) {
                    Duration remainingTime = Duration.between(currentTime, appointment.getTime());

                    // Appointment already happened
                    if (remainingTime.compareTo(Duration.ZERO) < 0) {
                        continue;
                    }

                    for (Reminder reminder : reminders) {
                        int milestone = reminder.getDayMileStone();
                        Duration milestoneDuration = Duration.ofDays(milestone);

                        if (remainingTime.compareTo(milestoneDuration) <= 0) {
                            if (hasSentReminder(appointment, milestone)) {
                                continue;
                            }

                            System.out.println("[AutomatedTextMgr] Sending " + milestone + " day reminder");

                            Optional<User> userOpt = userRepo.findById(appointment.getUserId());
                            if (userOpt.isEmpty()) {
                                System.out.println("[AutomatedTextMgr] Could not find user in database, skipping");
                                continue;
                            }

                            User user = userOpt.get();

                            Long textId = textMgr.sendText(user.getPhone(), reminder.getReminder());

                            appointment.setCurrentTextId(textId);
                            appointment.setCurrentReminderId(reminder.getId());
                            appointment.setWaitingForResponse(true);
                            markReminderSent(appointment, milestone);

                            break; // Only send one reminder per appointment at a time
                        }
                    }
                }
            }
        }).start();
    }

    private void handleCustomerReply(Appointment appointment, User user) {
        Long currentTextId = appointment.getCurrentTextId();
        if (currentTextId == null || !appointment.isWaitingForResponse()) {
            return;
        }

        List<TextMgr.TextMessage> incomingReplies = textMgr.getReplies(currentTextId);
        List<TextMgr.TextMessage> outgoingResponses = textMgr.getResponses(currentTextId);

        System.out.println("[AutomatedTextMgr] Text " + currentTextId
                + " incoming=" + incomingReplies.size()
                + " outgoing=" + outgoingResponses.size());

        // Customer replied but bot hasn't answered yet
        if (incomingReplies.isEmpty() || !outgoingResponses.isEmpty()) {
            return;
        }

        Long reminderId = appointment.getCurrentReminderId();
        if (reminderId == null) {
            System.out.println("[AutomatedTextMgr] No current reminder ID, skipping response");
            return;
        }

        Reminder currentReminder = reminderRepo.findById(reminderId).orElse(null);
        if (currentReminder == null) {
            System.out.println("[AutomatedTextMgr] Reminder not found: " + reminderId);
            return;
        }

        for (Long responseId : currentReminder.getResponseIds()) {
            Response response = responseRepo.findById(responseId).orElse(null);
            if (response == null) {
                continue;
            }

            System.out.println("[AutomatedTextMgr] Testing response: " + response.getText()
                    + " mode=" + response.getMode());

            if (!ResponseMatcher.matches(response, incomingReplies.get(0).getText())) {
                continue;
            }

            System.out.println("[AutomatedTextMgr] MATCHED RESPONSE");

            Long newTextId = textMgr.sendText(user.getPhone(), response.getResponse());

            appointment.setCurrentTextId(newTextId);
            appointment.setWaitingForResponse(false);     // ← Key fix
            // appointment.setCurrentReminderId(null);    // Optional: clean up

            appointmentRepo.save(appointment);

            actionMgr.doAction(response.getAction(), appointment);
            return; // Done with this reply
        }
    }

    private boolean hasSentReminder(Appointment appointment, int milestone) {
        List<Integer> milestones = appointment.getMileStoneList();
        if (milestones == null) {
            return false;
        }
        return milestones.contains(milestone);
    }

    private void markReminderSent(Appointment appointment, int milestone) {
        appointment.addMileStoneToList(milestone);
        appointment.setTextSentNow();
        appointmentRepo.save(appointment);
    }
}
