package com.httpsms.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "message_logs")
public class MessageLog {
    @PrimaryKey(autoGenerate = true)
    public long id;
	public long groupId = 0;
	  
  
    public String message;
    public String type;        // "sms", "image", "video"
    public String direction;   // "incoming", "outgoing"
    public long time;
    public String number;
    public String base64Data;  // Storage for raw media data strings
}
