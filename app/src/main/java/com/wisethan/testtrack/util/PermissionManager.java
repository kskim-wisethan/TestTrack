package com.wisethan.testtrack.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.wisethan.testtrack.MainActivity;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionManager {
    private Context context;
    private ArrayList<String> permission_check_list = new ArrayList<>();

    public PermissionManager(Context context) {
        this.context = context;

        this.permission_check_list.add(Manifest.permission.GET_ACCOUNTS);
        this.permission_check_list.add(Manifest.permission.READ_CONTACTS);
        this.permission_check_list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        this.permission_check_list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        this.permission_check_list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        this.permission_check_list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        this.permission_check_list.add(Manifest.permission.READ_PHONE_STATE);
        this.permission_check_list.add(Manifest.permission.INTERNET);
        this.permission_check_list.add(Manifest.permission.ACCESS_NETWORK_STATE);
        this.permission_check_list.add(Manifest.permission.WAKE_LOCK);
        this.permission_check_list.add(Manifest.permission.VIBRATE);

        // 위치
        // 파일
        // 전화
    }

    public void permissionCheck() {
        ArrayList<String> deninedPermission = new ArrayList<>();

        for(int i = 0; i < permission_check_list.size(); i++) {
            if (ContextCompat.checkSelfPermission(context, permission_check_list.get(i)) == PackageManager.PERMISSION_DENIED){
                Log.d("debug",permission_check_list.get(i));
                deninedPermission.add(permission_check_list.get(i));
            }
        }

        if (!deninedPermission.isEmpty()) {
            String[] permissions = new String [deninedPermission.size()];
            deninedPermission.toArray(permissions);
            ActivityCompat.requestPermissions((MainActivity)context, permissions, 0);
        }
    }
}