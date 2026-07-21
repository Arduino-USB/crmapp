package com.httpsms.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.httpsms.worker.QueueProcessor;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import java.util.concurrent.TimeUnit;

public class HttpGatewayService extends Service {

    private static final String CHANNEL_ID = "httpsms_gateway_channel";
    private static final String TAG = "HTTPSMS";
	private static final String ACTION_REPOST_NOTIFICATION = "com.httpsms.ACTION_REPOST_NOTIFICATION";
    private Server server;
    private QueueProcessor queueProcessor;
    private Thread processorThread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "HttpGatewayService onCreate fired.");
        
        createNotificationChannel();

        server = AndServer.webServer(this)
                .port(6741)
                .timeout(10, TimeUnit.SECONDS)
                .build();

        server.startup();
        Log.i(TAG, "AndServer web gateway started on port 6741.");

        // Instantiate the QueueProcessor and kick off the background thread loop
        queueProcessor = new QueueProcessor(this);
        processorThread = new Thread(queueProcessor);
        processorThread.start();
        Log.i(TAG, "QueueProcessor background thread spawned successfully from Service.");
    }


	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 1. If the intent came from a swipe-dismiss event, log it and clear the swipe
        if (intent != null && ACTION_REPOST_NOTIFICATION.equals(intent.getAction())) {
            Log.d(TAG, "User attempted to swipe away gateway notification. Force re-posting.");
        }

        // 2. Create a PendingIntent targeted back at this exact service
        Intent dismissIntent = new Intent(this, HttpGatewayService.class);
        dismissIntent.setAction(ACTION_REPOST_NOTIFICATION);
        
        // Use FLAG_MUTABLE or FLAG_IMMUTABLE depending on your target requirements
        android.app.PendingIntent deletePendingIntent = android.app.PendingIntent.getService(
                this, 
                0, 
                dismissIntent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Build the notification and attach the setDeleteIntent hook
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Gateway Server Active")
                .setContentText("Listening on http://localhost:6741")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setNotificationSilent()
                .setDeleteIntent(deletePendingIntent) // <--- The trap is set here
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "HttpGatewayService onDestroy fired. Cleaning up resources.");
        
        if (queueProcessor != null) {
            queueProcessor.stop();
        }
        if (processorThread != null) {
            processorThread.interrupt();
        }
        if (server != null && server.isRunning()) {
            server.shutdown();
        }
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "HTTPSMS Gateway Server Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Shows active background runtime notification for port 6741");
            
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
