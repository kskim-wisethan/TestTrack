package com.wisethan.testtrack.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;


public class FetchSensorDataIntentService extends IntentService {
    private static final String TAG = FetchSensorDataIntentService.class.getSimpleName();

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    private static final String EXTRA_SENSOR_RECEIVER = "com.wisethan.testtrack.service.extra.RECEIVER";
    private static final String EXTRA_SENSOR_RESULT = "com.wisethan.testtrack.service.extra.RESULT";

    public FetchSensorDataIntentService() {
        super("FetchSensorDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

    }

    private void sendResultToReceiver(int resultCode, String message) {
    }

}
