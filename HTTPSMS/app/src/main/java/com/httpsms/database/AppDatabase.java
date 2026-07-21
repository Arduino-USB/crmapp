package com.httpsms.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SmsJob.class, MessageLog.class, SmsGroup.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract JobDao jobDao();
    public abstract LogDao logDao();
	public abstract SmsGroupDao groupDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "httpsms_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
