package com.wisethan.testtrack.util;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.wisethan.testtrack.model.SensingModel;
import com.wisethan.testtrack.service.FetchAddressIntentService;

import java.util.ArrayList;
import java.util.List;

public class SensorDataManager {
    private static final String TAG = SensorDataManager.class.getSimpleName();

    public static final int TYPE_LOCATION = 51;
    public static final int TYPE_ADDRESS = 52;
    public static final int TYPE_MOTION = 53;

    long mStartTime = 0;
    long mEndTime = 0;
    long mStepCount = 0;
    private float[] mOrientationAngles = new float[3];
    private Location mLastLocation;
    private SensingModel mSensingModel = new SensingModel();
    private SensingCallback mSensingCallback;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mLinearAccelerometerSensor;
    private Sensor mGravitySensor;
    private Sensor mGyroscopeSensor;
    private Sensor mRotationVectorSensor;
    private Sensor mSignificantMotionSensor;
    private Sensor mStepDetectorSensor;
    private Sensor mMagneticFieldSensor;
    private Sensor mProximitySensor;
    private Sensor mTemperatureSensor;
    private Sensor mHumiditySensor;
    private Sensor mPressureSensor;
    private Sensor mLightSensor;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager mLocationManager;
    private GeofencingClient mGeofencingClient;

    private static Context mContext;
    private static SensorDataManager mInstance;
    public static SensorDataManager getInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new SensorDataManager(context);
        }
        return mInstance;
    }

    public SensorDataManager(Context context) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSignificantMotionSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mTemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mHumiditySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    public interface SensingCallback {
        public void onResponse(int type);
    }

    public SensingModel getSensingModel() {
        return mSensingModel;
    }

    public List<Sensor> getSupportedSensorList() {
        return mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public void setUserId(String userid) {
        mSensingModel.setUserId(userid);
    }

    public void startSensing(final SensingCallback callback) {
        mSensingCallback = callback;
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mLinearAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mGravitySensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mGyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mRotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mStepDetectorSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mProximitySensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mTemperatureSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mHumiditySensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mPressureSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener, mLightSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.requestTriggerSensor(mTriggerEventListener, mSignificantMotionSensor);
        mStartTime = System.nanoTime();
    }

    public void stopSensing() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensingCallback = null;
        mStartTime = 0;
        mEndTime = 0;
        mStepCount = 0;
    }

    private final TriggerEventListener mTriggerEventListener = new TriggerEventListener() {
        @Override
        public void onTrigger(TriggerEvent event) {
            float value[] = event.values;
            //  for example walking, biking, or sitting in a moving car
            mSensingModel.setSignificantMotion(value[0]);
            mSensingCallback.onResponse(TYPE_MOTION);
        }
    };

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                mEndTime = System.nanoTime();
                long elapsed_time = mEndTime - mStartTime;
                mSensingModel.setElapsedTime(elapsed_time);
                mStartTime = System.nanoTime();

                float value[] = event.values;
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setAcceleration(data);
                mSensingModel.setSensingTime(Timestamp.now());

                getLastLocation();
            } else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
                float value[] = event.values;
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setLinearAcceleration(data);
            } else if (type == Sensor.TYPE_GRAVITY) {
                float value[] = event.values;
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setGravity(data);
            } else if (type == Sensor.TYPE_GYROSCOPE) {
                float value[] = event.values;
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setGyroscope(data);
            } else if (type == Sensor.TYPE_ROTATION_VECTOR) {
                float value[] = event.values;
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                data.add(value[3]);
                mSensingModel.setRotationVector(data);
            } else if (type == Sensor.TYPE_STEP_DETECTOR) {
                float value[] = event.values;
                ++mStepCount;
                mSensingModel.setStepCount(mStepCount);
            } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                float value[] = event.values;
                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setMagneticField(data);
                // orientation (acceleration + magnetic field)
                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrix(rotationMatrix, null, mSensingModel.getAccelerationArray(), mSensingModel.getMagneticFieldArray());
                SensorManager.getOrientation(rotationMatrix, mOrientationAngles);
                mSensingModel.setOrientation(mOrientationAngles);
            } else if (type == Sensor.TYPE_PROXIMITY) {
                float value = event.values[0];
                mSensingModel.setProximity(value);
            } else if (type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                float value = event.values[0];
                mSensingModel.setTemperature(value);
            } else if (type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                float value = event.values[0];
                mSensingModel.setHumidity(value);
            } else if (type == Sensor.TYPE_PRESSURE) {
                float value = event.values[0];
                mSensingModel.setPressure(value);
            } else if (type == Sensor.TYPE_LIGHT) {
                float value = event.values[0];
                mSensingModel.setLight(value);
            }

            mSensingCallback.onResponse(type);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // A sensor's accuracy changes
            int type = sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {

            }
        }
    };

    private void getLastLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                float speed = location.getSpeed();

                                if (mLastLocation == null || (latitude != mLastLocation.getLatitude() && longitude != mLastLocation.getLongitude())) {
                                    mLastLocation = location;
                                    mSensingModel.setSensingLocation(new GeoPoint(latitude, longitude));
                                    mSensingModel.setSpeed(speed);
                                    startFetchAddressIntentService();
                                    mSensingCallback.onResponse(TYPE_LOCATION);
                                }
                            }
                        }
                    });

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            String result = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            mSensingModel.setAddress(result);
            stopFetchAddressIntentService();
            mSensingCallback.onResponse(TYPE_ADDRESS);
        }
    }

    private void startFetchAddressIntentService() {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.EXTRA_RECEIVER, new AddressResultReceiver(new Handler()));
        intent.putExtra(FetchAddressIntentService.EXTRA_DATA_LOCATION, mLastLocation);
        mContext.startService(intent);
        Log.d(TAG, "start fetch address service.");
    }

    private void stopFetchAddressIntentService() {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        mContext.stopService(intent);
        Log.d(TAG, "stop fetch address service.");
    }
}
