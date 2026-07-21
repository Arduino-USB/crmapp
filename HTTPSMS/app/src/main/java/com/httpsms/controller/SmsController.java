package com.httpsms.controller;

import com.httpsms.App;
import com.httpsms.database.AppDatabase;
import com.httpsms.database.MessageLog;
import com.httpsms.database.SmsJob;
import com.yanzhenjie.andserver.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SmsController {

    private final AppDatabase db;
    private static final int PAGE_SIZE = 50; // Returns 50 items maximum per page sequence

    public SmsController() {
        this.db = AppDatabase.getInstance(App.getContext());
    }

	@GetMapping("/alive")
	public Map<String, String> alive_check() {
		return Map.of("status", "ok");
	}

    @PostMapping("/send_sms")
    public Map<String, Object> sendSms(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            SmsJob job = new SmsJob();
            job.toNumber = (String) body.get("to");
            job.text = (String) body.get("text");
            job.msgType = "sms";
            job.scheduledTime = ((Number) body.get("time")).longValue();
            job.status = "pending";

            long jobId = db.jobDao().insert(job);

            response.put("job", jobId);
            response.put("status", "pending");
        } catch (Exception e) {
            response.put("job", -1);
            response.put("status", "failed");
        }
        return response;
    }

    @PostMapping("/send_media")
    public Map<String, Object> sendMedia(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String type = (String) body.get("type");
            if (!"image".equalsIgnoreCase(type) && !"video".equalsIgnoreCase(type)) {
                response.put("job", -1);
                response.put("status", "bad message");
                return response;
            }

            SmsJob job = new SmsJob();
            job.toNumber = (String) body.get("to");
            job.msgType = type.toLowerCase();
            job.base64Data = (String) body.get("base64");
            job.scheduledTime = ((Number) body.get("time")).longValue();
            job.status = "pending";

            long jobId = db.jobDao().insert(job);

            response.put("job", jobId);
            response.put("status", "pending");
        } catch (Exception e) {
            response.put("job", -1);
            response.put("status", "failed");
        }
        return response;
    }

    @GetMapping("/message_logs")
    public List<Map<String, Object>> getMessageLogs(
            @RequestParam(name = "number", required = false) String number,
            @RequestParam(name = "page", required = false) String pageStr) {
        
        int page = 0;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 0) page = 0;
            } catch (NumberFormatException ignored) {}
        }

        int offset = page * PAGE_SIZE;
        List<MessageLog> logs;

        if (number != null && !number.trim().isEmpty()) {
            logs = db.logDao().getLogsByNumberPaged(number, PAGE_SIZE, offset);
        } else {
            logs = db.logDao().getAllLogsPaged(PAGE_SIZE, offset);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (MessageLog log : logs) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", log.message);
            map.put("type", log.type);
            map.put("direction", log.direction);
            map.put("time", log.time);
            
            // Map the raw base64 data string out if the entry is image/video
            if ("image".equalsIgnoreCase(log.type) || "video".equalsIgnoreCase(log.type)) {
                map.put("base64", log.base64Data != null ? log.base64Data : "");
            }
            result.add(map);
        }
        return result;
    }

    @PostMapping("/clear_chat")
    public Map<String, Object> clearChat(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String targetNumber = (String) body.get("number");
            if (targetNumber == null || targetNumber.trim().isEmpty()) {
                response.put("status", "failed");
                response.put("reason", "Missing target target phone connection descriptor string");
                return response;
            }
            
            db.logDao().deleteLogsByNumber(targetNumber);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "failed");
        }
        return response;
    }

	@GetMapping("/pending_jobs")
    public List<Map<String, Object>> getPendingJobs() {
        // Pass the current system timestamp to satisfy the required long argument
        List<SmsJob> pendingList = db.jobDao().getPendingJobs(System.currentTimeMillis());
        List<Map<String, Object>> response = new ArrayList<>();
        
        if (pendingList != null) {
            for (SmsJob job : pendingList) {
                Map<String, Object> item = new HashMap<>();
                item.put("job", job.id);
                item.put("status", "pending");
                response.add(item);
            }
        }
        
        return response;
    }
    @GetMapping("/failed_jobs")
    public List<Map<String, Object>> getFailedJobs() {
        List<SmsJob> failedJobs = db.jobDao().getFailedJobs();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SmsJob job : failedJobs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", job.id);
            map.put("status", job.status);
            result.add(map);
        }
        return result;
    }
	
   @PostMapping("/retry_job")
    public Map<String, Object> retryJob(@RequestBody Map<String, Object> body) {
        long jobId = ((Number) body.get("id")).longValue();
        long newTime = ((Number) body.get("time")).longValue();
        
        SmsJob existingJob = db.jobDao().getJobById(jobId);
        Map<String, Object> response = new HashMap<>();
        
        if (existingJob != null) {
            // Instead of .delete() which doesn't exist, change status to 'failed' 
            // so the QueueProcessor drops/ignores it, then update it.
            existingJob.status = "failed";
            db.jobDao().update(existingJob); // Using your existing Room update rule
            
            // Re-create the cloned job fresh into the queue execution line
            SmsJob newJob = new SmsJob();
            newJob.toNumber = existingJob.toNumber;
            newJob.text = existingJob.text;
            newJob.msgType = existingJob.msgType;
            newJob.base64Data = existingJob.base64Data;
            newJob.scheduledTime = newTime;
            newJob.status = "pending";
            
            long newJobId = db.jobDao().insert(newJob);
            
            response.put("job", newJobId);
            response.put("status", "pending");
        } else {
            response.put("job", -1);
            response.put("status", "failed");
        }
        
        return response;
    }

	@PostMapping("/create_group")
    public Map<String, Object> createGroup(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String name = (String) body.get("name");
            List<String> numbersList = (List<String>) body.get("numbers");

            if (name == null || numbersList == null || numbersList.isEmpty()) {
                response.put("status", "failed");
                response.put("group_id", -1);
                return response;
            }

            // Join the list into a clean comma-separated string for DB storage
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numbersList.size(); i++) {
                sb.append(numbersList.get(i));
                if (i < numbersList.size() - 1) {
                    sb.append(",");
                }
            }

            com.httpsms.database.SmsGroup group = new com.httpsms.database.SmsGroup();
            group.name = name;
            group.numbers = sb.toString();

            long groupId = db.groupDao().insert(group);

            response.put("status", "success");
            response.put("group_id", groupId);
        } catch (Exception e) {
            response.put("status", "failed");
            response.put("group_id", -1);
        }
        return response;
    }

    @GetMapping("/list_groups")
    public List<Map<String, Object>> listGroups() {
        List<com.httpsms.database.SmsGroup> groups = db.groupDao().getAllGroups();
        List<Map<String, Object>> response = new ArrayList<>();

        if (groups != null) {
            for (com.httpsms.database.SmsGroup group : groups) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", group.name);
                map.put("id", group.id);
                response.add(map);
            }
        }
        return response;
    }

    @PostMapping("/delete_group")
    public Map<String, Object> deleteGroup(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            long id = ((Number) body.get("id")).longValue();
            com.httpsms.database.SmsGroup group = db.groupDao().getGroupById(id);

            if (group != null) {
                db.groupDao().delete(group);
                response.put("status", "success");
            } else {
                response.put("status", "failed");
            }
        } catch (Exception e) {
            response.put("status", "failed");
        }
        return response;
    }

	@PostMapping("/group_send_sms")
    public Map<String, Object> groupSendSms(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            long groupId = ((Number) body.get("id")).longValue();
            String text = (String) body.get("text");
            String type = (String) body.get("type"); // Will be null for plaintext, or "image/video"
            String base64 = (String) body.get("base64");

            com.httpsms.database.SmsGroup group = db.groupDao().getGroupById(groupId);
            if (group == null || group.numbers == null || group.numbers.isEmpty()) {
                response.put("status", "failed");
                response.put("reason", "Group not found or has no numbers assigned.");
                return response;
            }

            // Split our comma-separated numbers out into individual tasks
            String[] numbers = group.numbers.split(",");
            List<Long> generatedJobIds = new ArrayList<>();

            for (String number : numbers) {
                if (number.trim().isEmpty()) continue;

                SmsJob job = new SmsJob();
                job.toNumber = number.trim();
                job.scheduledTime = System.currentTimeMillis();
                job.status = "pending";

                if (type != null && ("image".equalsIgnoreCase(type) || "video".equalsIgnoreCase(type))) {
                    job.msgType = type.toLowerCase();
                    job.base64Data = base64;
                } else {
                    job.msgType = "sms";
                    job.text = text;
                }

                long jobId = db.jobDao().insert(job);
                generatedJobIds.add(jobId);
                
                // Drop an immediate placeholder history item linked to this group tracking view context
                MessageLog log = new MessageLog();
                log.message = job.text != null ? job.text : "";
                log.type = job.msgType;
                log.direction = "outgoing";
                log.time = System.currentTimeMillis();
                log.number = job.toNumber;
                log.base64Data = job.base64Data;
                log.groupId = groupId; // Attached to group filter target
                db.logDao().insert(log);
            }

            response.put("status", "success");
            response.put("jobs_created", generatedJobIds);
        } catch (Exception e) {
            response.put("status", "failed");
        }
        return response;
    }

    @GetMapping("/message_group_log")
    public List<Map<String, Object>> getMessageGroupLog(
            @RequestParam(name = "group_id") String groupIdStr,
            @RequestParam(name = "page", required = false) String pageStr) {
        
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            long groupId = Long.parseLong(groupIdStr);
            int page = 0;
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                page = Integer.parseInt(pageStr);
                if (page < 0) page = 0;
            }

            int offset = page * PAGE_SIZE;
            List<MessageLog> logs = db.logDao().getLogsByGroupIdPaged(groupId, PAGE_SIZE, offset);

            for (MessageLog log : logs) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", log.message);
                map.put("type", log.type);
                map.put("direction", log.direction);
                map.put("time", log.time);
                map.put("number", log.number);
                if (log.base64Data != null) {
                    map.put("base64", log.base64Data);
                }
                result.add(map);
            }
        } catch (Exception ignored) {}
        
        return result;
    }

}
