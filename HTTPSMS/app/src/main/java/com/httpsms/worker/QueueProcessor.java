package com.httpsms.worker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import com.httpsms.database.AppDatabase;
import com.httpsms.database.MessageLog;
import com.httpsms.database.SmsJob;
import java.util.List;

public class QueueProcessor implements Runnable {
    private static final String TAG = "HTTPSMS";
    private final Context context;
    private final AppDatabase db;
    private boolean isRunning = true;

    public QueueProcessor(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
        Log.d(TAG, "QueueProcessor initialized.");
    }

    public void stop() {
        Log.d(TAG, "QueueProcessor stop requested.");
        this.isRunning = false;
    }

    @Override
    public void run() {
        Log.d(TAG, "QueueProcessor background thread started running.");
        SmsManager smsManager;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                smsManager = context.getSystemService(SmsManager.class);
            } else {
                smsManager = SmsManager.getDefault();
            }
            Log.d(TAG, "SmsManager initialized successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Critical failure initializing SmsManager: " + e.getMessage(), e);
            return;
        }

        while (isRunning) {
            try {
                long now = System.currentTimeMillis();
                Log.v(TAG, "Checking database for pending jobs. Current system time (epoch ms): " + now);
                
                List<SmsJob> pending = db.jobDao().getPendingJobs(now);
                if (pending != null && !pending.isEmpty()) {
                    Log.i(TAG, "Found " + pending.size() + " pending job(s) scheduled on or before now.");
                    
                    for (SmsJob job : pending) {
                        Log.d(TAG, "Processing Job ID: " + job.id + " | Type: " + job.msgType + " | Target: " + job.toNumber);
                        
                        if ("sms".equalsIgnoreCase(job.msgType)) {
                            try {
                                Log.d(TAG, "Attempting to send SMS text to " + job.toNumber);
                                String SENT_ACTION = "com.httpsms.SMS_SENT_" + job.id + "_" + System.currentTimeMillis();
                                
                                Intent sentIntent = new Intent(SENT_ACTION);
                                sentIntent.setPackage(context.getPackageName());
                                PendingIntent sentPendingIntent = PendingIntent.getBroadcast(
                                    context, 
                                    (int) job.id, 
                                    sentIntent, 
                                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                                );

                                // Register a runtime receiver for this specific broadcast to catch radio/network failures
                                context.registerReceiver(new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context ctx, Intent intent) {
                                        try {
                                            context.unregisterReceiver(this);
                                        } catch (Exception ignored) {}

                                        // Run database operations off the main thread to prevent IllegalStateException
                                        new Thread(() -> {
                                            SmsJob currentJob = db.jobDao().getJobById(job.id);
                                            if (currentJob == null) return;

                                            if (getResultCode() == Activity.RESULT_OK) {
                                                Log.i(TAG, "SMS successfully sent for Job ID: " + job.id);
                                                currentJob.status = "sent";
                                                db.jobDao().update(currentJob);
                                                Log.d(TAG, "Job ID: " + job.id + " status updated to 'sent' in database.");

                                                MessageLog log = new MessageLog();
                                                log.message = job.text;
                                                log.type = "sms";
                                                log.direction = "outgoing";
                                                log.time = System.currentTimeMillis();
                                                log.number = job.toNumber;
                                                db.logDao().insert(log);
                                                Log.d(TAG, "MessageLog record inserted for Job ID: " + job.id);
                                            } else {
                                                String errorReason = getSmsErrorReason(getResultCode());
                                                Log.e(TAG, "SMS failed for Job ID: " + job.id + " | Reason: " + errorReason);
                                                
                                                currentJob.status = "failed";
                                                db.jobDao().update(currentJob);
                                                Log.d(TAG, "Job ID: " + job.id + " status updated to 'failed' in database.");
                                            }
                                        }).start();
                                    }
                                }, new IntentFilter(SENT_ACTION));

                                smsManager.sendTextMessage(job.toNumber, null, job.text, sentPendingIntent, null);
                                Log.i(TAG, "SmsManager.sendTextMessage dispatched for Job ID: " + job.id);
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to dispatch SMS for Job ID: " + job.id + ". Error: " + e.getMessage(), e);
                                job.status = "failed";
                                db.jobDao().update(job);
                                Log.d(TAG, "Job ID: " + job.id + " status updated to 'failed' in database.");
                            }
                        } else {
                            Log.d(TAG, "Processing Media Job ID: " + job.id);
                            if (job.base64Data == null || job.base64Data.isEmpty()) {
                                Log.w(TAG, "Media Job ID: " + job.id + " rejected due to empty/null base64 string data payload.");
                                job.status = "bad message";
                            } else {
                                Log.i(TAG, "Media payload processed (simulated) for Job ID: " + job.id);
                                job.status = "sent";
                                
                                MessageLog log = new MessageLog();
                                log.message = ""; 
                                log.type = job.msgType;
                                log.direction = "outgoing";
                                log.time = System.currentTimeMillis();
                                log.number = job.toNumber;
                                log.base64Data = job.base64Data; 
                                db.logDao().insert(log);
                                Log.d(TAG, "Media MessageLog record inserted for Job ID: " + job.id);
                            }
                            db.jobDao().update(job);
                            Log.d(TAG, "Media Job ID: " + job.id + " database state updated to: " + job.status);
                        }
                    }
                } else {
                    Log.v(TAG, "No pending jobs found due for execution right now.");
                }
                
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.w(TAG, "QueueProcessor loop interrupted. Shutting down worker thread.");
                break;
            } catch (Exception e) {
                Log.e(TAG, "Unexpected exception caught in QueueProcessor loop: " + e.getMessage(), e);
            }
        }
        Log.d(TAG, "QueueProcessor worker thread exited fully.");
    }

    private String getSmsErrorReason(int resultCode) {
        switch (resultCode) {
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                return "Generic failure";
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                return "No service / No cellular connection";
            case SmsManager.RESULT_ERROR_NULL_PDU:
                return "Null PDU";
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                return "Radio explicitly powered off";
            default:
                return "Unknown error code: " + resultCode;
        }
    }
}
