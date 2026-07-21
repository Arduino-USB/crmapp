package com.httpsms.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

public class BootJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        // The JobService environment bypasses background restrictions cleanly!
        Intent serviceIntent = new Intent(this, HttpGatewayService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Return false to signify the setup work is finished immediately
        return false; 
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
