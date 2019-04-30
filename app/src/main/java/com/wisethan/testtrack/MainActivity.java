package com.wisethan.testtrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wisethan.testtrack.activity.LoginActivity;
import com.wisethan.testtrack.activity.ProfileActivity;
import com.wisethan.testtrack.model.SensingModel;
import com.wisethan.testtrack.model.StorageFileModel;
import com.wisethan.testtrack.model.UserModel;
import com.wisethan.testtrack.util.PermissionManager;
import com.wisethan.testtrack.util.RequestManager;
import com.wisethan.testtrack.util.SensorDataManager;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

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
    private TextView mOrientationLog;
    private TextView mProximityLog;
    private TextView mEnvironmentLog;
    private Switch mTrackingSwitch;

    private SensorDataManager mSensorDataManager;
    private RequestManager mRequestManager;

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

        mSensorDataManager = SensorDataManager.getInstance(getApplicationContext());
        mRequestManager = RequestManager.getInstance();

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
        mOrientationLog = findViewById(R.id.current_orientation);
        mProximityLog = findViewById(R.id.current_proximity);
        mSignificantMotionLog = findViewById(R.id.current_significant_motion);
        mStepDetectLog = findViewById(R.id.current_step_detect);
        mSubLog = findViewById(R.id.sub_log);
        mLocationLog = findViewById(R.id.current_location);
        mAddressLog = findViewById(R.id.current_address);
        mEnvironmentLog = findViewById(R.id.current_environment);

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
            //getLastLocation();

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
        List<Sensor> deviceSensors = mSensorDataManager.getSupportedSensorList();
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
                startSensingData();
            } else {
                stopSensingData();
            }
        }
    }

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

            mSensorDataManager.setUserId(uid);
        } else {
            mProfileImage.setImageResource(R.mipmap.ic_launcher_round);
            mProfileName.setText(getString(R.string.nav_header_title));
            mProfileEmail.setText(getString(R.string.nav_header_subtitle));

            mSensorDataManager.setUserId("");
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

    private void uploadSensingData() {
        mRequestManager.requestSetSensingInfo(mSensorDataManager.getSensingModel(), new RequestManager.SuccessCallback() {
            @Override
            public void onResponse(boolean success) {
                Log.d(TAG, "sensing data upload success.");
            }
        });
    }

    private void startSensingData() {
        mSensorDataManager.startSensing(new SensorDataManager.SensingCallback() {
            @Override
            public void onResponse(int type) {
                if (type == Sensor.TYPE_ACCELEROMETER) {
                    mAccelerationLog.setText(mSensorDataManager.getSensingModel().getAccelerationString());
                    mSubLog.setText(mSensorDataManager.getSensingModel().getElapsedTimeString());
                } else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
                    mLinearAccelerationLog.setText(mSensorDataManager.getSensingModel().getLinearAccelerationString());
                } else if (type == Sensor.TYPE_GRAVITY) {
                    mGravityLog.setText(mSensorDataManager.getSensingModel().getGravityString());
                } else if (type == Sensor.TYPE_GYROSCOPE) {
                    mGyroscopeLog.setText(mSensorDataManager.getSensingModel().getGyroscopeString());
                } else if (type == Sensor.TYPE_ROTATION_VECTOR) {
                    mRotationVectorLog.setText(mSensorDataManager.getSensingModel().getRotationVectorString());
                } else if (type == Sensor.TYPE_STEP_DETECTOR) {
                    mStepDetectLog.setText(mSensorDataManager.getSensingModel().getStepCountString());
                } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                    mMagneticFieldLog.setText(mSensorDataManager.getSensingModel().getMagneticFieldString());
                    mOrientationLog.setText(mSensorDataManager.getSensingModel().getOrientationString());
                } else if (type == Sensor.TYPE_PROXIMITY) {
                    mProximityLog.setText(mSensorDataManager.getSensingModel().getProximityString());
                } else if (type == Sensor.TYPE_PRESSURE || type == Sensor.TYPE_LIGHT) {
                    mEnvironmentLog.setText(mSensorDataManager.getSensingModel().getEnvironmentString());
                } else if (type == SensorDataManager.TYPE_LOCATION) {
                    mLocationLog.setText(mSensorDataManager.getSensingModel().getLocationString());
                } else if (type == SensorDataManager.TYPE_ADDRESS) {
                    mAddressLog.setText(mSensorDataManager.getSensingModel().getAddress());
                } else if (type == SensorDataManager.TYPE_MOTION) {
                    mSignificantMotionLog.setText(mSensorDataManager.getSensingModel().getSignificantMotionString());
                }
            }
        });
    }

    private void stopSensingData() {
        mSensorDataManager.stopSensing();
    }
}
