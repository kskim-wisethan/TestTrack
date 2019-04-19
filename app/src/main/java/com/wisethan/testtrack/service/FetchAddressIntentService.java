package com.wisethan.testtrack.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.wisethan.testtrack.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String EXTRA_RECEIVER = "com.wisethan.testtrack.address.RECEIVER";
    public static final String EXTRA_DATA_LOCATION = "com.wisethan.testtrack.address.LOCATION";
    public static final String RESULT_DATA_KEY = "com.wisethan.testtrack.address.KEY";

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("AddressService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String errorMessage = "";

        // database default: en
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Geocoder geocoder_en = new Geocoder(this, new Locale("en"));
        Location location = intent.getParcelableExtra(EXTRA_DATA_LOCATION);
        mReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);

        List<Address> addresses = null;
        List<Address> addresses_en = null;
        try {
            addresses = geocoder.getFromLocation( location.getLatitude(), location.getLongitude(), 1);
            addresses_en = geocoder_en.getFromLocation( location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() + ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        if (addresses_en == null || addresses_en.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            Address address_en = addresses_en.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            addressFragments.add(address.getPostalCode());
            Log.i(TAG, getString(R.string.address_found));

            Bundle bundle = new Bundle();
            bundle.putString(RESULT_DATA_KEY, TextUtils.join(System.getProperty("line.separator"), addressFragments));
            mReceiver.send(SUCCESS_RESULT, bundle);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}

