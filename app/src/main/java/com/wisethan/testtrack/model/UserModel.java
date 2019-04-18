package com.wisethan.testtrack.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserModel implements Serializable {
    private String mUserId;
    private String mEmail;
    private String mName;
    private String mImageUrl;
    private String mDeviceId;
    private String mLocationId;
    private String mVehicleId;
    private String mTrackingId;

    public UserModel() {
        mUserId = "";
        mEmail = "";
        mName = "";
        mImageUrl = "";
        mDeviceId = "";
        mLocationId = "";
        mVehicleId = "";
        mTrackingId ="";
    }

    public UserModel(Map<String, Object> data) {
        if (data.containsKey("user_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("email")) {
            mEmail = (String) data.get("email");
        } else {
            mEmail = "";
        }

        if (data.containsKey("name")) {
            mName = (String) data.get("name");
        } else {
            mName = "";
        }

        if (data.containsKey("image_url")) {
            mImageUrl = (String) data.get("image_url");
        } else {
            mImageUrl = "";
        }

        if (data.containsKey("device_id")) {
            mDeviceId = (String) data.get("device_id");
        } else {
            mDeviceId = "";
        }

        if (data.containsKey("location_id")) {
            mLocationId = (String) data.get("location_id");
        } else {
            mLocationId = "";
        }

        if (data.containsKey("vehicle_id")) {
            mVehicleId = (String) data.get("vehicle_id");
        } else {
            mVehicleId = "";
        }

        if (data.containsKey("tracking_id")) {
            mTrackingId = (String) data.get("tracking_id");
        } else {
            mTrackingId = "";
        }
    }

    public void setUserId(String userid) {
        mUserId = userid;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setImageUrl(String url) {
        mImageUrl = url;
    }

    public void setDeviceId(String id) {
        mDeviceId = id;
    }

    public void setLocationId(String id) {
        mLocationId = id;
    }

    public void setVehicleId(String id) {
        mVehicleId = id;
    }

    public void setTrackingId(String id) {
        mTrackingId = id;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getDeviceId() {
        return  mDeviceId;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public String getVehicleId() {
        return mVehicleId;
    }

    public String getTrackingId() {
        return mTrackingId;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("user_id", mUserId);
        data.put("email", mEmail);
        data.put("name", mName);
        data.put("image_url", mImageUrl);
        data.put("device_id", mDeviceId);
        data.put("location_id", mLocationId);
        data.put("vehicle_id", mVehicleId);
        data.put("tracking_id", mTrackingId);

        return data;
    }
}
