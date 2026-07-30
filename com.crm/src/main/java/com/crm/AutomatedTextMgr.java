package com.crm;

import com.crm.entites.Appointment;
import com.crm.entites.Message;
import com.crm.entites.Response;
import com.crm.entites.User;
import com.crm.entites.Message.VarType;
import com.crm.entites.SmsLog;
import com.crm.enums.ResponseAction;
import com.crm.repos.AppointmentRepo;
import com.crm.repos.MessageRepo;
import com.crm.repos.ResponseRepo;
import com.crm.repos.UserRepo;
import com.crm.repos.SmsLogRepo;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Component
public class AutomatedTextMgr {

    private final UserRepo userRepo;
    private final AppointmentRepo appointmentRepo;
    private final MessageRepo messageRepo;
    private final ResponseRepo responseRepo;
    private final SmsLogRepo smsLogRepo;
    private final TextMgr textMgr;
    private final ActionMgr actionMgr;

    private final Map<Long, ResponseAction> lastExecutedActions = new HashMap<>();

    public AutomatedTextMgr(
            UserRepo userRepo,
            AppointmentRepo appointmentRepo,
            MessageRepo messageRepo,
            ResponseRepo responseRepo,
            SmsLogRepo smsLogRepo,
            TextMgr textMgr,
            ActionMgr actionMgr
    ) {
        this.userRepo = userRepo;
        this.appointmentRepo = appointmentRepo;
        this.messageRepo = messageRepo;
        this.responseRepo = responseRepo;
        this.smsLogRepo = smsLogRepo;
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


	private String parseText(User user, Appointment appointment, List<Object> textObjs) {
		
		String finalText = "";
		String varContents = null;
			
		for (int i = 0; i < textObjs.size(); i++) {
			Object currentTextObj = textObjs.get(0);
			if (currentTextObj instanceof Message.TextBlock) {
				Message.TextBlock textBlock = (Message.TextBlock) currentTextObj;
				
				System.out.println("[AutomatedTextMgr].Parser Appending " + textBlock.text + " to final text");
				finalText  = finalText + textBlock.text;
			} else if (currentTextObj instanceof Message.IfElseBlock) {
				Message.IfElseBlock ifElseBlock = (Message.IfElseBlock) currentTextObj;
				
				
				if (ifElseBlock.varType == VarType.USER_VAR) {
					varContents = user.getVar(ifElseBlock.varName);
	
				} else if (ifElseBlock.varType == VarType.APPOINTMENT_VAR) {
					varContents = appointment.getVar(ifElseBlock.varName);	
				}
	
				if (varContents != null) {
					System.out.println("[AutomatedTextMgr].Parser Varible '" + ifElseBlock.varName  + "' is defined in '" + ifElseBlock.varType + "'. Appending");
					List<Integer> insertedIndexes = new ArrayList<>();		
				
					for (int j = 0; j < ifElseBlock.ifText.size(); j++) {
						//here we are checking if we need to inject the varContent varible into current index.
						boolean insertVar = false;
						for (int k = 0; k < ifElseBlock.insertIndexes.length; k++) {
							if (j == ifElseBlock.insertIndexes[k] && !(insertedIndexes.contains(j))) {
								insertVar = true;
							}
						}
					
						if (insertVar) {
							System.out.println("[AutomatedTextMgr].Parser Injecting varContent into finalText");	
							finalText = finalText + varContents;
							// becuase we didnt actually insert that yet, and we still need to 
							insertedIndexes.add(j);
							j--;
							continue;
						} else {
							finalText = finalText + ifElseBlock.ifText.get(j);
						}		
					}		
			
				}							
	
					
			}
		}
		
		return finalText;
	}	


    private void runWorkerLoop() {
        List<Appointment> appointments = appointmentRepo.findAppointmentPhoneNotBlank();
        List<Message> messages = messageRepo.findAll();
        Instant currentTime = Instant.now();

        System.out.println("[AutomatedTextMgr] --- TICK START ---");
        System.out.println("[AutomatedTextMgr] Found " + appointments.size() 
                + " appointment(s) and " + messages.size() + " message rule(s).");

        // === 1. Handle customer replies ===
        for (Appointment appointment : appointments) {
            Optional<User> userOpt = userRepo.findById(appointment.getUserId());
            if (userOpt.isPresent()) {
                handleCustomerReply(appointment, userOpt.get());
            } else {
                System.out.println("[AutomatedTextMgr] [REPLY CHECK] No user found for Appt ID " + appointment.getId());
            }
        }

        // === 2. Send automated messages ===
        for (Appointment appointment : appointments) {
            if (appointment.isStopTexting()) {
                System.out.println("[AutomatedTextMgr] [OUTBOUND SKIPPED] Appt ID " + appointment.getId() + " has stopTexting=true.");
                continue;
            }

            Instant apptTime = appointment.getTime();
            if (apptTime == null) {
                System.out.println("[AutomatedTextMgr] [OUTBOUND SKIPPED] Appt ID " + appointment.getId() + " has NULL time.");
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
	
					// Parse the text message
	

					String textMessage = parseText(user, appointment, msg.getTextList());
	
                    Long textId = textMgr.sendText(user.getPhone(), textMessage);
                    System.out.println("[AutomatedTextMgr] [OUTBOUND SUCCESS] Sent text ID: " + textId + " to Appt ID: " + appointment.getId());

                    smsLogRepo.save(new SmsLog(
                        appointment.getId(),
                        user.getId(),
                        user.getPhone(),
                        "OUTBOUND",
                        textMessage,
                        textId
                    ));                

                    appointment.setCurrentTextId(textId);
                    appointment.setCurrentReminderId(msg.getId());
                    appointment.setWaitingForResponse(true);
                    markMessageSent(appointment, milestoneDays, msg.isBefore());

                    System.out.println("[AutomatedTextMgr] Updated Appt ID " + appointment.getId() 
                            + " with currentTextId=" + textId + ", currentReminderId=" + msg.getId());

                    break;
                }
            }
        }
        System.out.println("[AutomatedTextMgr] --- TICK END ---");
    }

    private void handleCustomerReply(Appointment appointment, User user) {
        Long currentTextId = appointment.getCurrentTextId();
        
        System.out.println("[AutomatedTextMgr] [REPLY CHECK] Appt ID: " + appointment.getId() 
                + " | currentTextId: " + currentTextId 
                + " | currentReminderId: " + appointment.getCurrentReminderId()
                + " | isWaitingForResponse: " + appointment.isWaitingForResponse());

        if (currentTextId == null) {
            System.out.println("[AutomatedTextMgr] [REPLY SKIPPED] Appt ID " + appointment.getId() + " has null currentTextId.");
            return;
        }

        List<TextMgr.TextMessage> incomingReplies = textMgr.getReplies(currentTextId);
        System.out.println("[AutomatedTextMgr] [REPLY CHECK] Fetched " + incomingReplies.size() + " reply(ies) for text ID " + currentTextId);

        if (incomingReplies.isEmpty()) {
            return;
        }

        Long messageId = appointment.getCurrentReminderId();
        if (messageId == null) {
            System.out.println("[AutomatedTextMgr] [REPLY ERROR] Found replies, but currentReminderId is null for Appt ID " + appointment.getId());
            return;
        }

        Message currentMessage = messageRepo.findById(messageId).orElse(null);
        if (currentMessage == null) {
            System.out.println("[AutomatedTextMgr] [REPLY ERROR] Message ID " + messageId + " not found in database.");
            return;
        }

        TextMgr.TextMessage latestReply = incomingReplies.get(incomingReplies.size() - 1);
        System.out.println("[AutomatedTextMgr] [REPLY RECEIVED] Appt ID " + appointment.getId() 
                + " latest reply text: \"" + latestReply.getText() + "\"");

        boolean alreadyLogged = smsLogRepo.findByAppointmentIdOrderByTimestampAsc(appointment.getId())
            .stream()
            .anyMatch(log -> "INBOUND".equals(log.getDirection()) && latestReply.getText().equals(log.getMessageContent()));

        if (!alreadyLogged) {
            System.out.println("[AutomatedTextMgr] Logging new INBOUND message to SmsLog Repo...");
            smsLogRepo.save(new SmsLog(
                appointment.getId(),
                user.getId(),
                user.getPhone(),
                "INBOUND",
                latestReply.getText(),
                currentTextId
            ));
        }

        List<Long> responseIds = currentMessage.getResponseIds();
        System.out.println("[AutomatedTextMgr] Checking reply against " + (responseIds != null ? responseIds.size() : 0) + " response rule(s)...");

        if (responseIds == null || responseIds.isEmpty()) {
            System.out.println("[AutomatedTextMgr] [REPLY WARNING] Message ID " + messageId + " has NO response IDs associated with it!");
            return;
        }

        for (Long responseId : responseIds) {
            Response response = responseRepo.findById(responseId).orElse(null);
            if (response == null) {
                System.out.println("[AutomatedTextMgr] Response ID " + responseId + " not found in DB.");
                continue;
            }

            boolean matches = ResponseMatcher.matches(response, latestReply.getText());
            System.out.println("[AutomatedTextMgr] Testing pattern/text against response ID " + responseId 
                    + " (" + response.getText() + "): " + (matches ? "MATCHED" : "NO MATCH"));

            if (!matches) {
                continue;
            }

            ResponseAction newAction = response.getAction();
            ResponseAction previousAction = lastExecutedActions.get(appointment.getId());

            // Update branch
            if (previousAction != null) {
                if (previousAction != newAction) {
                    System.out.println("[AutomatedTextMgr] [ACTION UPDATED] Appt ID " + appointment.getId() 
                            + ": " + previousAction + " -> " + newAction);

                    actionMgr.undoAction(previousAction, appointment);
                    actionMgr.doAction(newAction, appointment);
                    lastExecutedActions.put(appointment.getId(), newAction);

                    String changeText = "Response changed for " + previousAction + " to " + newAction;
                    Long newTextId = textMgr.sendText(user.getPhone(), changeText);

                    appointment.setCurrentTextId(newTextId);
                    appointmentRepo.save(appointment);
                } else {
                    System.out.println("[AutomatedTextMgr] Action " + newAction + " already executed previously for Appt ID " + appointment.getId());
                }
                return;
            }

            // Initial response branch
            System.out.println("[AutomatedTextMgr] [ACTION EXECUTING] Matched Response: " + response.getText() + " | Action: " + newAction);

            Long newTextId = textMgr.sendText(user.getPhone(), response.getResponse());

            appointment.setCurrentTextId(newTextId);
            appointment.setWaitingForResponse(false);

            actionMgr.doAction(newAction, appointment);
            lastExecutedActions.put(appointment.getId(), newAction);

            appointmentRepo.save(appointment);
            System.out.println("[AutomatedTextMgr] [SUCCESS] Handled reply, updated database for Appt ID " + appointment.getId());
            return;
        }

        System.out.println("[AutomatedTextMgr] Reply \"" + latestReply.getText() + "\" did not match any defined response rules.");
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
