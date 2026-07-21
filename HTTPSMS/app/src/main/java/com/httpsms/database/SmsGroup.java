package com.httpsms.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SmsGroup {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String name;
    
    // Storing numbers as a simple comma-separated string to keep it clean
    public String numbers; 
}
