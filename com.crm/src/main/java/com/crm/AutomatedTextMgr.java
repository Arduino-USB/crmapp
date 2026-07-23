package com.crm;

import com.crm.entites.Appointment;
import com.crm.entites.Message;
import com.crm.entites.Response;
import com.crm.entites.User;
import com.crm.enums.ResponseAction;
import com.crm.repos.AppointmentRepo;
import com.crm.repos.MessageRepo;
import com.crm.repos.ResponseRepo;
import com.crm.repos.UserRepo;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AutomatedTextMgr {

    private final UserRepo userRepo;
    private final AppointmentRepo appointmentRepo;
    private final MessageRepo messageRepo;
    private final ResponseRepo responseRepo;
    private final TextMgr textMgr;
    private final ActionMgr actionMgr;

    private final Map<Long, ResponseAction> lastExecutedActions = new HashMap<>();

    public AutomatedTextMgr(
            UserRepo userRepo,
            AppointmentRepo appointmentRepo,
            MessageRepo messageRepo,
            ResponseRepo responseRepo,
            TextMgr textMgr,
            ActionMgr actionMgr
    ) {
        this.userRepo = userRepo;
        this.appointmentRepo = appointmentRepo;
        this.messageRepo = messageRepo;
        this.responseRepo = responseRepo;
        this.textMgr = textMgr;
        this.actionMgr = actionMgr;

        System.out.println("[AutomatedTextMgr] Starting automated text sending thread...");

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    runWorkerLoop();
                } catch (Exception e) {
                    System.out.println("[AutomatedTextMgr] CRITICAL WORKER EXCEPTION: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runWorkerLoop() {
        List<Appointment> appointments = appointmentRepo.findAppointmentPhoneNotBlank();
        List<Message> messages = messageRepo.findAll();
        Instant currentTime = Instant.now();

        System.out.println("[AutomatedTextMgr] Tick: Found " + appointments.size() 
                + " appointment(s) and " + messages.size() + " message rule(s).");

        // === 1. Handle customer replies ===
        for (Appointment appointment : appointments) {
            Optional<User> userOpt = userRepo.findById(appointment.getUserId());
            if (userOpt.isPresent()) {
                handleCustomerReply(appointment, userOpt.get());
            } else {
                System.out.println("[AutomatedTextMgr] Reply Check: No user found for Appt ID " + appointment.getId());
            }
        }

        // === 2. Send automated messages ===
        for (Appointment appointment : appointments) {
            Instant apptTime = appointment.getTime();
            if (apptTime == null) {
                System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() + " has NULL time. Skipping.");
                continue;
            }

            for (Message msg : messages) {
                int milestoneDays = msg.getDayMileStone();
                Duration milestoneDuration = Duration.ofDays(milestoneDays);

                boolean shouldSend = false;

                if (msg.isBefore()) {
                    if (currentTime.isBefore(apptTime)) {
                        Duration timeUntilAppt = Duration.between(currentTime, apptTime);
                        System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() 
                                + " is BEFORE. Time until appt: " + timeUntilAppt.toHours() + "h " 
                                + (timeUntilAppt.toMinutes() % 60) + "m (Milestone: " + milestoneDays + " days)");

                        if (timeUntilAppt.compareTo(milestoneDuration) <= 0) {
                            shouldSend = true;
                        } else {
                            System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() 
                                    + " skipped: time until appt (" + timeUntilAppt.toHours() + "h) > milestone (" 
                                    + milestoneDuration.toHours() + "h)");
                        }
                    } else {
                        System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() 
                                + " skipped for BEFORE message because appointment time is in the past.");
                    }
                } else {
                    if (currentTime.isAfter(apptTime)) {
                        Duration timeSinceAppt = Duration.between(apptTime, currentTime);
                        System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() 
                                + " is AFTER. Time since appt: " + timeSinceAppt.toHours() + "h " 
                                + (timeSinceAppt.toMinutes() % 60) + "m (Milestone: " + milestoneDays + " days)");

                        if (timeSinceAppt.compareTo(milestoneDuration) >= 0) {
                            shouldSend = true;
                        } else {
                            System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() 
                                    + " skipped: time since appt (" + timeSinceAppt.toHours() + "h) < milestone (" 
                                    + milestoneDuration.toHours() + "h)");
                        }
                    } else {
                        System.out.println("[AutomatedTextMgr] Appt ID " + appointment.getId() 
                                + " skipped for AFTER message because appointment time is in the future.");
                    }
                }

                if (shouldSend) {
                    if (hasSentMessage(appointment, milestoneDays, msg.isBefore())) {
                        System.out.println("[AutomatedTextMgr] Message already sent previously for Appt ID " 
                                + appointment.getId() + " (Milestone: " + milestoneDays + ")");
                        continue;
                    }

                    System.out.println("[AutomatedTextMgr] Triggering " + milestoneDays + " day " 
                            + (msg.isBefore() ? "before" : "after") + " message for Appt ID: " + appointment.getId());

                    Optional<User> userOpt = userRepo.findById(appointment.getUserId());
                    if (userOpt.isEmpty()) {
                        System.out.println("[AutomatedTextMgr] Could not find user for ID: " + appointment.getUserId());
                        continue;
                    }

                    User user = userOpt.get();

                    Long textId = textMgr.sendText(user.getPhone(), msg.getText());

                    appointment.setCurrentTextId(textId);
                    appointment.setCurrentReminderId(msg.getId());
                    appointment.setWaitingForResponse(true);
                    markMessageSent(appointment, milestoneDays, msg.isBefore());

                    break;
                }
            }
        }
    }

	private void handleCustomerReply(Appointment appointment, User user) {
        Long currentTextId = appointment.getCurrentTextId();
        if (currentTextId == null) {
            return;
        }

        List<TextMgr.TextMessage> incomingReplies = textMgr.getReplies(currentTextId);

        if (incomingReplies.isEmpty()) {
            return;
        }

        Long messageId = appointment.getCurrentReminderId();
        if (messageId == null) {
            return;
        }

        Message currentMessage = messageRepo.findById(messageId).orElse(null);
        if (currentMessage == null) {
            return;
        }

        TextMgr.TextMessage latestReply = incomingReplies.get(incomingReplies.size() - 1);

        for (Long responseId : currentMessage.getResponseIds()) {
            Response response = responseRepo.findById(responseId).orElse(null);
            if (response == null) {
                continue;
            }

            if (!ResponseMatcher.matches(response, latestReply.getText())) {
                continue;
            }

            ResponseAction newAction = response.getAction();
            ResponseAction previousAction = lastExecutedActions.get(appointment.getId());

            // Check if we already executed an action for this appointment session
            if (previousAction != null) {
                if (previousAction != newAction) {
                    System.out.println("[AutomatedTextMgr] UPDATING REPLY FOR APPT " + appointment.getId() 
                            + ": " + previousAction + " -> " + newAction);

                    // 1. Revert the old action
                    actionMgr.undoAction(previousAction, appointment);

                    // 2. Perform the new action
                    actionMgr.doAction(newAction, appointment);

                    // 3. Update tracked state
                    lastExecutedActions.put(appointment.getId(), newAction);

                    // 4. Send updated response text
                    String changeText = "Response changed for " + previousAction + " to " + newAction;
                    Long newTextId = textMgr.sendText(user.getPhone(), changeText);

                    appointment.setCurrentTextId(newTextId);
                    appointmentRepo.save(appointment);
                }
                return; // Reply matched and processed as update (or same action), exit loop
            }

            // First time replying to this prompt
            System.out.println("[AutomatedTextMgr] MATCHED INITIAL RESPONSE: " + response.getText());

            Long newTextId = textMgr.sendText(user.getPhone(), response.getResponse());

            appointment.setCurrentTextId(newTextId);
            appointment.setWaitingForResponse(false);
            appointmentRepo.save(appointment);

            actionMgr.doAction(newAction, appointment);
            lastExecutedActions.put(appointment.getId(), newAction);
            return;
        }
    }

    private boolean hasSentMessage(Appointment appointment, int milestone, boolean isBefore) {
        List<Integer> milestones = appointment.getMileStoneList();
        if (milestones == null) {
            return false;
        }
        int key = isBefore ? milestone : -milestone;
        return milestones.contains(key);
    }

    private void markMessageSent(Appointment appointment, int milestone, boolean isBefore) {
        int key = isBefore ? milestone : -milestone;
        appointment.addMileStoneToList(key);
        appointment.setTextSentNow();
        appointmentRepo.save(appointment);
    }
}
