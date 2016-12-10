package com.example.xiazhituo.ecase;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.StringBuilderPrinter;

/**
 * Created by xiazhituo on 2016/12/10.
 */

public class EcaseApplication extends MultiDexApplication {
    public String email;
    public String userId;
    public String deviceId;
    public String name;
    public String deviceSim;
    public String phoneNum;
    public boolean isSignedIn;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return  name;
    }

    public void setDeviceSim(String deviceSim) {
        this.deviceSim = deviceSim;
    }

    public String getDeviceSim() {
        return deviceSim;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setSignedIn(boolean flag) {
        this.isSignedIn = flag;
    }

    public boolean getCorrectSetted() {
        return isSignedIn;
    }
}
