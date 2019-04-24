package com.wisethan.testtrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.wisethan.testtrack.activity.LoginActivity;
import com.wisethan.testtrack.activity.ProfileActivity;
import com.wisethan.testtrack.model.SensingModel;
import com.wisethan.testtrack.model.StorageFileModel;
import com.wisethan.testtrack.model.UserModel;
import com.wisethan.testtrack.service.FetchAddressIntentService;
import com.wisethan.testtrack.util.PermissionManager;
import com.wisethan.testtrack.util.RequestManager;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    long mStartTime = 0;
    long mEndTime = 0;
    long mStepCount = 0;
    float mTemperature = 0;
    float mHumidity = 0;
    float mPressure = 0;
    float mLight = 0;
    private float[] mOrientationAngles = new float[3];
    Location mLastLocation;
    SensingModel mSensingModel = new SensingModel();

    private ImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private TextView mAccelerationLog;
    private TextView mLinearAccelerationLog;
    private TextView mGravityLog;
    private TextView mGyroscopeLog;
    private TextView mRotationVectorLog;
    private TextView mSignificantMotionLog;
    private TextView mStepDetectLog;
    private TextView mSubLog;
    private TextView mLocationLog;
    private TextView mAddressLog;
    private TextView mMagneticFieldLog;
    private TextView mProximityLog;
    private TextView mEnvironmentLog;
    private Switch mTrackingSwitch;

    private RequestManager mRequestManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.permissionCheck();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mRequestManager = RequestManager.getInstance();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long nt = System.nanoTime();
                long mt = System.currentTimeMillis();
                String output = String.format("%d ns, %d ms", nt, mt);
                Log.d(TAG, output);

                Date dt = Calendar.getInstance().getTime();
                Timestamp tm = Timestamp.now();

                String output2 = tm.toString();
                String output3 = String.format("%d-%02d-%02d", dt.getYear() + 1900, dt.getMonth() + 1, dt.getDate());
                Log.d(TAG, output2);
                Log.d(TAG, output3);

                uploadSensingData();
                //Snackbar.make(view, output, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mProfileImage = (ImageView) header.findViewById(R.id.nav_profile_image);
        mProfileImage.setOnClickListener(new ProfileImageClick());
        mProfileName = (TextView) header.findViewById(R.id.nav_profile_name);
        mProfileEmail = (TextView) header.findViewById(R.id.nav_profile_email);
        mAccelerationLog = findViewById(R.id.current_acceleration);
        mLinearAccelerationLog = findViewById(R.id.current_linear_acceleration);
        mGravityLog = findViewById(R.id.current_gravity);
        mGyroscopeLog = findViewById(R.id.current_gyroscope);
        mRotationVectorLog = findViewById(R.id.current_rotational_vector);
        mMagneticFieldLog = findViewById(R.id.current_magnetic_field);
        mProximityLog = findViewById(R.id.current_proximity);
        mSignificantMotionLog = findViewById(R.id.current_significant_motion);
        mStepDetectLog = findViewById(R.id.current_step_detect);
        mSubLog = findViewById(R.id.sub_log);
        mLocationLog = findViewById(R.id.current_location);
        mAddressLog = findViewById(R.id.current_address);
        mEnvironmentLog = findViewById(R.id.current_environment);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
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

        mTrackingSwitch = (Switch) findViewById(R.id.tracking_switch);
        mTrackingSwitch.setOnCheckedChangeListener(new TrackingSwitchClick());

        updateProfile();

        LocalBroadcastManager.getInstance(this).registerReceiver(new MyStateReceiver(), makeBroadcastIntentFilter());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            goIdentifyingSensors();

        } else if (id == R.id.nav_gallery) {
            getLastLocation();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goIdentifyingSensors() {
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        String output = "";
        for (int i = 0; i < deviceSensors.size(); i++) {
            Sensor sensor = deviceSensors.get(i);

            Log.d(TAG, deviceSensors.get(i).getName());
            output += deviceSensors.get(i).getName() + "\n";
        }

        mAccelerationLog.setText(output);
    }

    class ProfileImageClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            profileImageClicked(v);
        }
    }

    class TrackingSwitchClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                startTracking();
            } else {
                stopTracking();
            }
        }
    }

    private void startTracking() {
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
        //mStartTime = System.nanoTime();
    }

    private void stopTracking() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mStartTime = 0;
        mEndTime = 0;
        mStepCount = 0;
        mTemperature = 0;
        mHumidity = 0;
        mPressure = 0;
        mLight = 0;
    }

    private final TriggerEventListener mTriggerEventListener = new TriggerEventListener() {
        @Override
        public void onTrigger(TriggerEvent event) {
            float value[] = event.values;
            String log = "Current Motion\n";
            log += String.format("[%.0f]\n", value[0]);
            mSignificantMotionLog.setText(log);
        }
    };

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                mEndTime = System.nanoTime();
                long elapsed_time = mEndTime - mStartTime;
                String log2 = String.format("elapsed time: %.0f ms", (double)(elapsed_time / 1000000));
                mSubLog.setText(log2);
                mStartTime = System.nanoTime();

                float value[] = event.values;
                String log = "Acceleration force (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f, %f, %f]\n", value[0], value[1], value[2]);
                mAccelerationLog.setText(log);

                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setAcceleration(data);
                mSensingModel.setSensingTime(Timestamp.now());

                getLastLocation();
            } else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
                float value[] = event.values;
                String log = "Linear Acceleration (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f, %f, %f]\n", value[0], value[1], value[2]);
                mLinearAccelerationLog.setText(log);
            } else if (type == Sensor.TYPE_GRAVITY) {
                float value[] = event.values;
                String log = "Gravity (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f, %f, %f]\n", value[0], value[1], value[2]);
                mGravityLog.setText(log);

                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setGravity(data);
            } else if (type == Sensor.TYPE_GYROSCOPE) {
                float value[] = event.values;
                String log = "Gyroscope (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f, %f, %f]\n", value[0], value[1], value[2]);
                mGyroscopeLog.setText(log);

                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                mSensingModel.setGyroscope(data);

            } else if (type == Sensor.TYPE_ROTATION_VECTOR) {
                float value[] = event.values;
                String log = "Rotation Vector (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f, %f, %f, %f]\n", value[0], value[1], value[2], value[3]);
                mRotationVectorLog.setText(log);

                ArrayList<Float> data = new ArrayList<Float>();
                data.add(value[0]);
                data.add(value[1]);
                data.add(value[2]);
                data.add(value[3]);
                mSensingModel.setRotationVector(data);

            } else if (type == Sensor.TYPE_STEP_DETECTOR) {
                float value[] = event.values;
                ++mStepCount;
                String log = "Step Detector (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%.0f][%d]\n", value[0], mStepCount);
                mStepDetectLog.setText(log);

                mSensingModel.setStepCount(value[0]);
            } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                float value[] = event.values;
                String log = "Magnetic Field (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f, %f, %f]\n", value[0], value[1], value[2]);
                mMagneticFieldLog.setText(log);

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
                String log = "Proximity (" + System.currentTimeMillis() + ")\n";
                log += String.format("[%f cm]\n", value);
                mProximityLog.setText(log);

                mSensingModel.setProximity(value);
            } else if (type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                mTemperature = event.values[0];
                String log = "Environment (" + System.currentTimeMillis() + ")\n";
                log += String.format("[Temperature: %.0f, Humidity: %.0f, Pressure: %.0f, Light: %.0f]\n", mTemperature, mHumidity, mPressure, mLight);
                mEnvironmentLog.setText(log);

            } else if (type == Sensor.TYPE_RELATIVE_HUMIDITY) {
                mHumidity = event.values[0];
                String log = "Environment (" + System.currentTimeMillis() + ")\n";
                log += String.format("[Temperature: %.0f, Humidity: %.0f, Pressure: %.0f, Light: %.0f]\n", mTemperature, mHumidity, mPressure, mLight);
                mEnvironmentLog.setText(log);

            } else if (type == Sensor.TYPE_PRESSURE) {
                mPressure = event.values[0];
                String log = "Environment (" + System.currentTimeMillis() + ")\n";
                log += String.format("[Temperature: %.0f, Humidity: %.0f, Pressure: %.0f, Light: %.0f]\n", mTemperature, mHumidity, mPressure, mLight);
                mEnvironmentLog.setText(log);

                ArrayList<Float> data = new ArrayList<Float>();
                data.add(mTemperature);
                data.add(mHumidity);
                data.add(mPressure);
                data.add(mLight);
                mSensingModel.setEnvironment(data);

            } else if (type == Sensor.TYPE_LIGHT) {
                mLight = event.values[0];
                String log = "Environment (" + System.currentTimeMillis() + ")\n";
                log += String.format("[Temperature: %.0f, Humidity: %.0f, Pressure: %.0f, Light: %.0f]\n", mTemperature, mHumidity, mPressure, mLight);
                mEnvironmentLog.setText(log);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // A sensor's accuracy changes
            int type = sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {

            }
        }
    };


    private void profileImageClicked(View v) {
        if (checkLogin()) {
            goProfile();
            closeDrawer();
        } else {
            goLogin();
            closeDrawer();
        }
    }

    private void goProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", MainActivity.class.getName());
            startActivity(intent);
        }
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", MainActivity.class.getName());
            startActivity(intent);
        }
    }

    private boolean checkLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            return false;
        }
        return true;
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private class MyStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra(LoginActivity.EXTENDED_DATA_STATUS, false);
            Log.d(TAG, "Received status: " + status);

            if (status) {
                updateProfile();
            }
        }
    }

    private void updateProfile() {
        if (checkLogin()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();

            mProfileName.setText((name == null || name.isEmpty()) ? uid : name);
            mProfileEmail.setText(email);
            getUserProfileInfo(uid);

            mSensingModel.setUserId(uid);
        } else {
            mProfileImage.setImageResource(R.mipmap.ic_launcher_round);
            mProfileName.setText(getString(R.string.nav_header_title));
            mProfileEmail.setText(getString(R.string.nav_header_subtitle));

            mSensingModel.setUserId("");
        }
    }

    private static IntentFilter makeBroadcastIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.BROADCAST_ACTION);
        return intentFilter;
    }

    private void getUserProfileInfo(String userid) {
        mRequestManager.requestGetUserInfo(userid, new RequestManager.UserCallback() {
            @Override
            public void onResponse(UserModel response) {
                Log.d(TAG, "onResponse: UserInfo (" + response.getUserId() + ", " + response.getName() + ", " + response.getImageUrl() + ")");

                String username = response.getName();
                mProfileName.setText(username);

                String imagename = "profile";
                String imageurl = response.getImageUrl();
                if (imageurl != null && !imageurl.isEmpty()) {
                    String imagesuffix = imageurl.substring(imageurl.indexOf('.'), imageurl.length());
                    mRequestManager.requestDownloadFileFromStorage(imagename, imageurl, imagesuffix, new RequestManager.StorageFileCallback() {
                        @Override
                        public void onResponse(StorageFileModel download) {
                            String imgurl = download.getPath();
                            File imgFile = new File(imgurl);
                            if (imgFile.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                if (myBitmap != null) {
                                    mProfileImage.setImageBitmap(myBitmap);
                                }
                            }
                        }
                    });
                } else {
                    try {
                        AssetManager am = getResources().getAssets();
                        InputStream is = null;
                        is = am.open("lake.png");
                        if (is != null) {
                            Bitmap bm = BitmapFactory.decodeStream(is);
                            mProfileImage.setImageBitmap(bm);
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getLastLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;

                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                float speed = location.getSpeed();

                                String log = "Last Location\n[" + latitude + ", " + longitude + ", " + speed + " m/s" + "\n";
                                mLocationLog.setText(log);
                                mSensingModel.setSensingLocation(new GeoPoint(latitude, longitude));

                                startFetchAddressIntentService();
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
            mAddressLog.setText(result);
        }
    }

    private void startFetchAddressIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.EXTRA_RECEIVER, new AddressResultReceiver(new Handler()));
        intent.putExtra(FetchAddressIntentService.EXTRA_DATA_LOCATION, mLastLocation);
        startService(intent);
    }

    private void uploadSensingData() {
        mRequestManager.requestSetSensingInfo(mSensingModel, new RequestManager.SuccessCallback() {
            @Override
            public void onResponse(boolean success) {
                Log.d(TAG, "sensing data upload success.");
            }
        });
    }
}
