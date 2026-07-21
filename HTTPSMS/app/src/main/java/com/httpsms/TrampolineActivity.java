package com.httpsms;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.httpsms.service.HttpGatewayService;

public class TrampolineActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We are now technically in the foreground, bypassing background restrictions!
        Intent serviceIntent = new Intent(this, HttpGatewayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Close instantly so the user never sees a window flash open
        finish();
    }
}
