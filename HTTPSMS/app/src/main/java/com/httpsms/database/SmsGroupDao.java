package com.httpsms.database;

import androidx.room.*;
import java.util.List;

@Dao
public interface SmsGroupDao {
    @Insert
    long insert(SmsGroup group);

    @Query("SELECT * FROM SmsGroup")
    List<SmsGroup> getAllGroups();

    @Query("SELECT * FROM SmsGroup WHERE id = :id LIMIT 1")
    SmsGroup getGroupById(long id);

    @Delete
    void delete(SmsGroup group);
}
