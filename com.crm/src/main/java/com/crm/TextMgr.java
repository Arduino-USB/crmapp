package com.crm;

import com.crm.entites.SmsJob;
import com.crm.repos.SmsJobRepo;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;


@Component
public class TextMgr {

    private final SmsJobRepo smsJobRepo;
    private final HTTPWrapper requests = new HTTPWrapper();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TextMgr(SmsJobRepo smsJobRepo) {
        this.smsJobRepo = smsJobRepo;
        System.out.println("[TextMgr] Starting text message phone wrapper");

        // Schedule worker tasks safely instead of an unthrottled thread loop
        this.scheduler.scheduleWithFixedDelay(this::processJobs, 0, 2, TimeUnit.SECONDS);
    }

    private void processJobs() {
        try {
            if (!ConnectionLoop.connected()) {
                return;
            }

            processOutboundSms();
            processFailedJobs();
        } catch (Exception e) {
            System.out.println("[TextMgr] Worker loop execution error: " + e.getMessage());
        }
    }

    private void processOutboundSms() {
        List<SmsJob> pendingJobs = smsJobRepo.findByStatusOrderByCreatedAtAsc(SmsJob.Status.PENDING);

        for (SmsJob currentJob : pendingJobs) {
            if (!ConnectionLoop.connected()) {
                break;
            }

            currentJob.setStatus(SmsJob.Status.PROCESSING);
            smsJobRepo.save(currentJob);

            // Construct payload matching SmsController /send_sms schema
            Map<String, Object> payload = Map.of(
                "to", currentJob.getRecipient(),
                "text", currentJob.getText(),
                "time", System.currentTimeMillis()
            );

            String url = "http://" + ConnectionLoop.get_ip() + ":" + ConnectionLoop.get_port() + "/send_sms";
            Map<String, Object> response = requests.post(url, payload, Map.class);

            if (response != null && !"failed".equalsIgnoreCase((String) response.get("status"))) {
                System.out.println("[TextMgr] Text successfully sent");
                currentJob.setStatus(SmsJob.Status.SENT);
                currentJob.setSentAt(System.currentTimeMillis());
            } else {
                System.out.println("[TextMgr] Phone failed to send text");
                currentJob.setStatus(SmsJob.Status.FAILED);
            }

            smsJobRepo.save(currentJob);
        }
    }

    private void processFailedJobs() {
        if (!ConnectionLoop.connected()) {
            return;
        }

        String baseUrl = "http://" + ConnectionLoop.get_ip() + ":" + ConnectionLoop.get_port();
        List<Map<String, Object>> response = requests.get(baseUrl + "/failed_jobs", List.class);

        if (response == null || response.isEmpty()) {
            return;
        }

        for (Map<String, Object> failedJobMap : response) {
            if (!ConnectionLoop.connected()) {
                break;
            }

            // Fix key name ('id' instead of 'job') and parse safely
            Number jobIdNum = (Number) failedJobMap.get("id");
            if (jobIdNum == null) {
                continue;
            }

            long jobId = jobIdNum.longValue();
            System.out.println("[TextMgr] Requesting phone to retry job: " + jobId);

            Map<String, Object> retryPayload = Map.of(
                "id", jobId,
                "time", System.currentTimeMillis() // Fix: Unix epoch in milliseconds
            );

            Map<String, Object> retryResponse = requests.post(baseUrl + "/retry_job", retryPayload, Map.class);

            if (retryResponse != null && !"failed".equalsIgnoreCase((String) retryResponse.get("status"))) {
                System.out.println("[TextMgr] Phone successfully queued retry for job: " + jobId);
            } else {
                System.out.println("[TextMgr] Phone retry job request failed for job: " + jobId);
            }
        }
    }

    public Long sendText(String recipient, String text) {
        SmsJob job = new SmsJob(recipient, text);
        job = smsJobRepo.save(job); 
        return job.getId();
    }

    public SmsJob.Status getStatus(Long jobId) {
        return smsJobRepo.findById(jobId)
                .map(SmsJob::getStatus)
                .orElse(null);
    }


	public class TextMessage {

		private final String text;
		private final Long time;

		public TextMessage(String text, Long time) {
		    this.text = text;
		    this.time = time;
		}

		public String getText() {
		    return text;
		}

		public Long getTime() {
		    return time;
		}
	}

	public List<TextMessage> getResponses(Long jobId) {
		Optional<SmsJob> targetJobOpt = smsJobRepo.findById(jobId);

		if (targetJobOpt.isEmpty() || targetJobOpt.get().getSentAt() == null) {
			return Collections.emptyList();
		}

		SmsJob targetJob = targetJobOpt.get();

		List<SmsJob> responses = smsJobRepo.findByRecipientAndStatusAndSentAtGreaterThanOrderBySentAtAsc(
			targetJob.getRecipient(),
			SmsJob.Status.SENT,
			targetJob.getSentAt()
		);

		List<TextMessage> messages = new ArrayList<>();

		for (SmsJob response : responses) {
			messages.add(new TextMessage(
				response.getText(),
				response.getSentAt()
			));
		}

		return messages;
	}


	
	/**
	 * Returns all incoming SMS replies received after this job was sent,
	 * up until the next outbound SMS sent to the same number (if any).
	 */
	
	public List<TextMessage> getReplies(Long jobId) {
		Optional<SmsJob> targetJobOpt = smsJobRepo.findById(jobId);
		if (targetJobOpt.isEmpty() || targetJobOpt.get().getSentAt() == null) {
		    return Collections.emptyList();
		}

		SmsJob targetJob = targetJobOpt.get();
		long startTime = targetJob.getSentAt();
		String number = targetJob.getRecipient();

		Optional<SmsJob> nextJob =
		    smsJobRepo.findFirstByRecipientAndStatusAndSentAtGreaterThanOrderBySentAtAsc(
		        number,
		        SmsJob.Status.SENT,
		        startTime
		    );

		long endTime = nextJob.map(SmsJob::getSentAt).orElse(Long.MAX_VALUE);

		String url = "http://" + ConnectionLoop.get_ip() + ":"
		           + ConnectionLoop.get_port()
		           + "/message_logs?number=" + URLEncoder.encode(number, StandardCharsets.UTF_8);

		List<Map<String, Object>> logs = requests.get(url, List.class);
		if (logs == null) {
		    return Collections.emptyList();
		}

		List<TextMessage> replies = new ArrayList<>();

		for (Map<String, Object> log : logs) {
		    String type = (String) log.get("type");
		    String direction = (String) log.get("direction");

		    Number rawTime = (Number) log.get("time");
		    long msgTime = rawTime != null ? rawTime.longValue() : 0L;

		    if ("sms".equalsIgnoreCase(type)
		            && "incoming".equalsIgnoreCase(direction)
		            && msgTime >= startTime
		            && msgTime < endTime) {

		        replies.add(new TextMessage(
		            String.valueOf(log.get("message")),
		            msgTime
		        ));
		    }
		}

		replies.sort(Comparator.comparingLong(TextMessage::getTime));

		return replies;
	}
}
