package com.httpsms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.httpsms.worker.BootWorker;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || 
            "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {
            
            // Queue an immediate, high-priority background worker execution
            OneTimeWorkRequest bootWorkRequest = new OneTimeWorkRequest.Builder(BootWorker.class)
                    .build();
                    
            WorkManager.getInstance(context).enqueue(bootWorkRequest);
        }
    }
}
