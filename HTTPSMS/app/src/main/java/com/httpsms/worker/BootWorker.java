package com.httpsms.worker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.httpsms.service.HttpGatewayService;

public class BootWorker extends Worker {
    public BootWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent serviceIntent = new Intent(getApplicationContext(), HttpGatewayService.class);
        
        // Android 14+ specific foreground initialization configuration bundle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Bundle fgsArgs = new Bundle();
            fgsArgs.putString("android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE", "Embedded HTTP API gateway for remote device automation");
            serviceIntent.putExtras(fgsArgs);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(serviceIntent);
            } else {
                getApplicationContext().startService(serviceIntent);
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
