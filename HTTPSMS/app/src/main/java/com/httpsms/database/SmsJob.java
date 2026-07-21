package com.httpsms.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sms_jobs")
public class SmsJob {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String toNumber;
    public String text;
    public String msgType;     // "sms", "image", "video"
    public String base64Data;  // For MMS data
    public long scheduledTime;
    public String status;      // "pending", "sent", "failed", "bad message"
}
