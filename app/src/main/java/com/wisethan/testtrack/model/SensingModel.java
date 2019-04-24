package com.wisethan.testtrack.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SensingModel {
    private String mSensingId;
    private String mUserId;
    private Timestamp mSensingTime;
    private GeoPoint mSensingLocation;
    private ArrayList<Float> mAcceleration;
    private ArrayList<Float> mGravity;
    private ArrayList<Float> mGyroscope;
    private ArrayList<Float> mRotationVector;
    private ArrayList<Float> mMagneticField;
    private ArrayList<Float> mOrientation;
    private Number mProximity;
    private Number mStepCount;
    private ArrayList<Float> mEnvironment;

    public SensingModel() {
        mSensingId = "";
        mUserId = "";
        mSensingTime = Timestamp.now();
        mSensingLocation = new GeoPoint(0, 0);
        mAcceleration = new ArrayList<Float>();
        mGravity = new ArrayList<Float>();
        mGyroscope = new ArrayList<Float>();
        mRotationVector = new ArrayList<Float>();
        mMagneticField = new ArrayList<Float>();
        mOrientation = new ArrayList<Float>();
        mProximity = 0;
        mStepCount = 0;
        mEnvironment = new ArrayList<Float>();
    }

    public SensingModel(Map<String, Object> data) {
        if (data.containsKey("sensing_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("user_id")) {
            mUserId = (String) data.get("user_id");
        } else {
            mUserId = "";
        }

        if (data.containsKey("sensing_time")) {
            mSensingTime = (Timestamp) data.get("sensing_time");
        } else {
            mSensingTime = Timestamp.now();
        }

        if (data.containsKey("sensing_location")) {
            mSensingLocation = (GeoPoint) data.get("sensing_location");
        } else {
            mSensingLocation = new GeoPoint(0, 0);
        }

        if (data.containsKey("acceleration")) {
            mAcceleration = (ArrayList<Float>) data.get("acceleration");
        } else {
            mAcceleration = new ArrayList<Float>();
        }

        if (data.containsKey("gravity")) {
            mGravity = (ArrayList<Float>) data.get("gravity");
        } else {
            mGravity = new ArrayList<Float>();
        }

        if (data.containsKey("gyroscope")) {
            mGyroscope = (ArrayList<Float>) data.get("gyroscope");
        } else {
            mGyroscope = new ArrayList<Float>();
        }

        if (data.containsKey("rotation_vector")) {
            mRotationVector = (ArrayList<Float>) data.get("rotation_vector");
        } else {
            mRotationVector = new ArrayList<Float>();
        }

        if (data.containsKey("magnetic_field")) {
            mMagneticField = (ArrayList<Float>) data.get("magnetic_field");
        } else {
            mMagneticField = new ArrayList<Float>();
        }

        if (data.containsKey("orientation")) {
            mOrientation = (ArrayList<Float>) data.get("orientationn");
        } else {
            mOrientation = new ArrayList<Float>();
        }

        if (data.containsKey("proximity")) {
            mProximity = (Number) data.get("proximity");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("step_count")) {
            mStepCount = (Number) data.get("step_count");
        } else {
            mStepCount = 0;
        }

        if (data.containsKey("environment")) {
            mEnvironment = (ArrayList<Float>) data.get("environment");
        } else {
            mEnvironment = new ArrayList<Float>();
        }
    }

    public void setSensingId(String sensingid) {
        mSensingId = sensingid;
    }

    public void setUserId(String userid) {
        mUserId = userid;
    }

    public void setSensingTime(Timestamp time) {
        mSensingTime = time;
    }

    public void setSensingLocation(GeoPoint location) {
        mSensingLocation = location;
    }

    public void setAcceleration(ArrayList<Float> acceleration) {
        mAcceleration = acceleration;
    }

    public void setGravity(ArrayList<Float> gravity) {
        mGravity = gravity;
    }

    public void setGyroscope(ArrayList<Float> gyroscope) {
        mGyroscope = gyroscope;
    }

    public void setRotationVector(ArrayList<Float> rotation) {
        mRotationVector = rotation;
    }

    public void setMagneticField(ArrayList<Float> magnetic) {
        mMagneticField = magnetic;
    }

    public void setOrientation(ArrayList<Float> orientation) {
        mOrientation = orientation;
    }

    public void setOrientation(float[] values) {
        if (values.length != 3) {
            return;
        }

        if (mOrientation.size() == 3) {
            mOrientation.set(0, values[0]);
            mOrientation.set(1, values[1]);
            mOrientation.set(2, values[2]);
        } else {
            mOrientation.add(values[0]);
            mOrientation.add(values[1]);
            mOrientation.add(values[2]);
        }
    }

    public void setProximity(Number proximity) {
        mProximity = proximity;
    }

    public void setStepCount(Number count) {
        mStepCount = count;
    }

    public void setEnvironment(ArrayList<Float> environment) {
        mEnvironment = environment;
    }

    public String getSensingId() {
        return mSensingId;
    }

    public String getUserId() {
        return mUserId;
    }

    public Timestamp getSensingTime() {
        return mSensingTime;
    }

    public GeoPoint getSensingLocation() {
        return mSensingLocation;
    }

    public ArrayList<Float> getAcceleration() {
        return mAcceleration;
    }

    public float[] getAccelerationArray() {
        float values[] = new float[3];
        if (mAcceleration.size() == 3) {
            values[0] = mAcceleration.get(0);
            values[1] = mAcceleration.get(1);
            values[2] = mAcceleration.get(2);
            return values;
        } else {
            values[0] = 0;
            values[1] = 0;
            values[2] = 0;
            return values;
        }
    }

    public ArrayList<Float> getGravity() {
        return  mGravity;
    }

    public ArrayList<Float> getGyroscope() {
        return mGyroscope;
    }

    public ArrayList<Float> getRotationVector() {
        return mRotationVector;
    }

    public ArrayList<Float> getMagneticField() {
        return mMagneticField;
    }

    public float[] getMagneticFieldArray() {
        float values[] = new float[3];
        if (mMagneticField.size() == 3) {
            values[0] = mMagneticField.get(0);
            values[1] = mMagneticField.get(1);
            values[2] = mMagneticField.get(2);
            return values;
        } else {
            values[0] = 0;
            values[1] = 0;
            values[2] = 0;
            return values;
        }
    }

    public ArrayList<Float> getOrientation() {
        return mOrientation;
    }

    public Number getProximity() {
        return mProximity;
    }

    public Number getStepCount() {
        return mStepCount;
    }

    public ArrayList<Float> getEnvironment() {
        return mEnvironment;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("sensing_id", mSensingId);
        data.put("user_id", mUserId);
        data.put("sensing_time", mSensingTime);
        data.put("sensing_location", mSensingLocation);
        data.put("acceleration", mAcceleration);
        data.put("gravity", mGravity);
        data.put("gyroscope", mGyroscope);
        data.put("rotation_vector", mRotationVector);
        data.put("magnetic_field", mMagneticField);
        data.put("orientation", mOrientation);
        data.put("proximity", mProximity);
        data.put("step_count", mStepCount);
        data.put("environment", mEnvironment);

        return data;
    }
}
