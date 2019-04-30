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
    private String mAddress;
    private Timestamp mSensingTime;
    private GeoPoint mSensingLocation;
    private ArrayList<Float> mAcceleration;
    private ArrayList<Float> mLinearAcceleration;
    private ArrayList<Float> mGravity;
    private ArrayList<Float> mGyroscope;
    private ArrayList<Float> mRotationVector;
    private ArrayList<Float> mMagneticField;
    private ArrayList<Float> mOrientation;
    private Number mTemperature;
    private Number mHumidity;
    private Number mPressure;
    private Number mLight;
    private Number mProximity;
    private Number mSignificantMotion;
    private Number mStepCount;
    private Number mSpeed;
    private Number mElapsedTime;

    public SensingModel() {
        mSensingId = "";
        mUserId = "";
        mAddress = "";
        mSensingTime = Timestamp.now();
        mSensingLocation = new GeoPoint(0, 0);
        mAcceleration = new ArrayList<Float>();
        mGravity = new ArrayList<Float>();
        mGyroscope = new ArrayList<Float>();
        mRotationVector = new ArrayList<Float>();
        mMagneticField = new ArrayList<Float>();
        mOrientation = new ArrayList<Float>();
        mTemperature = 0;
        mHumidity = 0;
        mPressure = 0;
        mLight = 0;
        mProximity = 0;
        mSignificantMotion = 0;
        mStepCount = 0;
        mSpeed = 0;
        mElapsedTime = 0;
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

        if (data.containsKey("address")) {
            mUserId = (String) data.get("address");
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

        if (data.containsKey("linear_acceleration")) {
            mLinearAcceleration = (ArrayList<Float>) data.get("linear_acceleration");
        } else {
            mLinearAcceleration = new ArrayList<Float>();
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

        if (data.containsKey("temperature")) {
            mProximity = (Number) data.get("temperature");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("humidity")) {
            mProximity = (Number) data.get("humidity");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("pressure")) {
            mProximity = (Number) data.get("pressure");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("light")) {
            mProximity = (Number) data.get("light");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("proximity")) {
            mProximity = (Number) data.get("proximity");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("significant_motion")) {
            mProximity = (Number) data.get("significant_motion");
        } else {
            mProximity = 0;
        }

        if (data.containsKey("step_count")) {
            mStepCount = (Number) data.get("step_count");
        } else {
            mStepCount = 0;
        }

        if (data.containsKey("speed")) {
            mSpeed = (Number) data.get("speed");
        } else {
            mSpeed = 0;
        }

        if (data.containsKey("elapsed_time")) {
            mStepCount = (Number) data.get("elapsed_time");
        } else {
            mStepCount = 0;
        }
    }

    public void setSensingId(String sensingid) {
        mSensingId = sensingid;
    }

    public void setUserId(String userid) {
        mUserId = userid;
    }

    public void setAddress(String address) {
        mAddress = address;
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

    public void setLinearAcceleration(ArrayList<Float> linear_acceleration) {
        mLinearAcceleration = linear_acceleration;
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

    public void setTemperature(Number oc) {
        mTemperature = oc;
    }

    public void setHumidity(Number percent) {
        mHumidity = percent;
    }

    public void setPressure(Number mbar) {
        mPressure = mbar;
    }

    public void setLight(Number lx) {
        mLight = lx;
    }

    public void setProximity(Number proximity) {
        mProximity = proximity;
    }

    public void setSignificantMotion(Number motion) {
        mSignificantMotion = motion;
    }

    public void setStepCount(Number count) {
        mStepCount = count;
    }

    public void setSpeed(Number ms) {
        mSpeed = ms;
    }

    public void setElapsedTime(Number ms) {
        mElapsedTime = ms;
    }

    public String getSensingId() {
        return mSensingId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getAddress() {
        return mAddress;
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

    public Number getTemperature() {
        return mTemperature;
    }

    public Number getHumidity() {
        return mHumidity;
    }

    public Number getPressure() {
        return mPressure;
    }

    public Number getLight() {
        return mLight;
    }

    public Number getProximity() {
        return mProximity;
    }

    public Number getSignificantMotion() {
        return mSignificantMotion;
    }

    public Number getStepCount() {
        return mStepCount;
    }

    public Number getSpeed() {
        return mSpeed;
    }

    public Number getElapsedTime() {
        return mElapsedTime;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("sensing_id", mSensingId);
        data.put("user_id", mUserId);
        data.put("address", mAddress);
        data.put("sensing_time", mSensingTime);
        data.put("sensing_location", mSensingLocation);
        data.put("acceleration", mAcceleration);
        data.put("linear_acceleration", mLinearAcceleration);
        data.put("gravity", mGravity);
        data.put("gyroscope", mGyroscope);
        data.put("rotation_vector", mRotationVector);
        data.put("magnetic_field", mMagneticField);
        data.put("orientation", mOrientation);
        data.put("temperature", mTemperature);
        data.put("humidity", mHumidity);
        data.put("pressure", mPressure);
        data.put("light", mLight);
        data.put("proximity", mProximity);
        data.put("significant_motion", mSignificantMotion);
        data.put("step_count", mStepCount);
        data.put("speed", mSpeed);
        data.put("elapsed_time", mElapsedTime);

        return data;
    }

    public String getAccelerationString() {
        String str = "Acceleration force (" + System.currentTimeMillis() + ")\n";
        if (mAcceleration.size() == 3) {
            str += String.format("[%f, %f, %f]\n", mAcceleration.get(0), mAcceleration.get(1), mAcceleration.get(2));
        } else {
            str += String.format("[0, 0, 0]\n");
        }
        return str;
    }

    public String getLinearAccelerationString() {
        String str = "Linear Acceleration (" + System.currentTimeMillis() + ")\n";
        if (mLinearAcceleration.size() == 3) {
            str += String.format("[%f, %f, %f]\n", mLinearAcceleration.get(0), mLinearAcceleration.get(1), mLinearAcceleration.get(2));
        } else {
            str += String.format("[0, 0, 0]\n");
        }
        return str;
    }

    public String getGravityString() {
        String str = "Gravity (" + System.currentTimeMillis() + ")\n";
        if (mGravity.size() == 3) {
            str += String.format("[%f, %f, %f]\n", mGravity.get(0), mGravity.get(1), mGravity.get(2));
        } else {
            str += String.format("[0, 0, 0]\n");
        }
        return str;
    }

    public String getGyroscopeString() {
        String str = "Gyroscope (" + System.currentTimeMillis() + ")\n";
        if (mGyroscope.size() == 3) {
            str += String.format("[%f, %f, %f]\n", mGyroscope.get(0), mGyroscope.get(1), mGyroscope.get(2));
        } else {
            str += String.format("[0, 0, 0]\n");
        }
        return str;
    }

    public String getRotationVectorString() {
        String str = "Rotation Vector (" + System.currentTimeMillis() + ")\n";
        if (mRotationVector.size() == 4) {
            str += String.format("[%f, %f, %f, %f]\n", mRotationVector.get(0), mRotationVector.get(1), mRotationVector.get(2), mRotationVector.get(3));
        } else {
            str += String.format("[0, 0, 0, 0]\n");
        }
        return str;
    }

    public String getMagneticFieldString() {
        String str = "Magnetic Field (" + System.currentTimeMillis() + ")\n";
        if (mMagneticField.size() == 3) {
            str += String.format("[%f, %f, %f]\n", mMagneticField.get(0), mMagneticField.get(1), mMagneticField.get(2));
        } else {
            str += String.format("[0, 0, 0]\n");
        }
        return str;
    }

    public String getOrientationString() {
        String str = "Orientation (" + System.currentTimeMillis() + ")\n";
        if (mOrientation.size() == 3) {
            str += String.format("[%f, %f, %f]\n", mOrientation.get(0), mOrientation.get(1), mOrientation.get(2));
        } else {
            str += String.format("[0, 0, 0]\n");
        }
        return str;
    }

    public String getEnvironmentString() {
        String str = "Environment (" + System.currentTimeMillis() + ")\n";
        str += String.format("[Temperature: %.0f, Humidity: %.0f, Pressure: %.0f, Light: %.0f]\n", mTemperature.floatValue(), mHumidity.floatValue(), mPressure.floatValue(), mLight.floatValue());
        return str;
    }

    public String getProximityString() {
        String str = "Proximity (" + System.currentTimeMillis() + ")\n";
        str += String.format("[%f cm]\n", mProximity);
        return str;
    }

    public String getSignificantMotionString() {
        String str = "Current Motion\n";
        str += String.format("[%.0f]\n", mSignificantMotion);
        return str;
    }

    public String getStepCountString() {
        String str = "Step Detector (" + System.currentTimeMillis() + ")\n";
        str += String.format("[%d]\n", mStepCount);
        return str;
    }

    public String getLocationString() {
        String str = "Last Location\n[" + mSensingLocation.getLatitude() + ", " + mSensingLocation.getLongitude() + ", " + mSpeed + " m/s]" + "\n";
        return str;
    }

    public String getElapsedTimeString() {
        String str = String.format("elapsed time: %.0f ms", (mElapsedTime.floatValue() / 1000000));
        return str;
    }
}
