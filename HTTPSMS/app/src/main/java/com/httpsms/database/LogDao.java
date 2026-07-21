package com.httpsms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LogDao {
    @Insert
    void insert(MessageLog log);

    // Dynamic pagination query filtering by a target phone number
    @Query("SELECT * FROM message_logs WHERE number = :number ORDER BY time DESC LIMIT :limit OFFSET :offset")
    List<MessageLog> getLogsByNumberPaged(String number, int limit, int offset);
    
    // Dynamic global pagination query 
    @Query("SELECT * FROM message_logs ORDER BY time DESC LIMIT :limit OFFSET :offset")
    List<MessageLog> getAllLogsPaged(int limit, int offset);

    // Delete logs matching a target destination string
    @Query("DELETE FROM message_logs WHERE number = :number")
    void deleteLogsByNumber(String number);

    // Fixed: Corrected table name case to message_logs and explicitly added the @Query annotation
    @Query("SELECT * FROM message_logs WHERE groupId = :groupId ORDER BY time DESC LIMIT :limit OFFSET :offset")
    List<MessageLog> getLogsByGroupIdPaged(long groupId, int limit, int offset);
}
