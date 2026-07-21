package com.httpsms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface JobDao {
    @Insert
    long insert(SmsJob job);

    @Update
    void update(SmsJob job);

    @Query("SELECT * FROM sms_jobs WHERE id = :id LIMIT 1")
    SmsJob getJobById(long id);

    @Query("SELECT * FROM sms_jobs WHERE status = 'pending' AND scheduledTime <= :currentTime ORDER BY scheduledTime ASC")
    List<SmsJob> getPendingJobs(long currentTime);

    @Query("SELECT * FROM sms_jobs WHERE status = 'failed' OR status = 'bad message'")
    List<SmsJob> getFailedJobs();

    @Query("DELETE FROM sms_jobs WHERE id = :id")
    void deleteById(long id);
}
