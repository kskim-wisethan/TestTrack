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
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wisethan.testtrack.activity.LoginActivity;
import com.wisethan.testtrack.activity.ProfileActivity;
import com.wisethan.testtrack.model.StorageFileModel;
import com.wisethan.testtrack.model.UserModel;
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
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    long mStartTime = 0;
    long mEndTime = 0;

    private ImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private TextView mMainLog;
    private TextView mSubLog;
    private Switch mTrackingSwitch;

    private RequestManager mRequestManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        mMainLog = findViewById(R.id.current_location);
        mSubLog = findViewById(R.id.current_address);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mTrackingSwitch = (Switch) findViewById(R.id.tracking_switch);
        mTrackingSwitch.setOnCheckedChangeListener(new TrackingSwitchClick());

        updateProfile();

        MyStateReceiver stateReceiver = new MyStateReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(stateReceiver, makeBroadcastIntentFilter());
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

        mMainLog.setText(output);
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
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        mStartTime = System.nanoTime();
    }

    private void stopTracking() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mStartTime = 0;
        mEndTime = 0;
    }

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // A sensor reports a new value
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                mEndTime = System.nanoTime();
                long elapsed_time = mEndTime - mStartTime;
                float value[] = event.values;
                String log  = "Acceleration\n";
                log += String.format("[%f, %f, %f]\n", value[0], value[1], value[2]);
                mMainLog.setText(log);

                String log2 = String.format("elapsed time: %.0f ms", (double)(elapsed_time / 1000000));
                mSubLog.setText(log2);
                mStartTime = System.nanoTime();
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
        } else {
            mProfileImage.setImageResource(R.mipmap.ic_launcher_round);
            mProfileName.setText(getString(R.string.nav_header_title));
            mProfileEmail.setText(getString(R.string.nav_header_subtitle));
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

}
