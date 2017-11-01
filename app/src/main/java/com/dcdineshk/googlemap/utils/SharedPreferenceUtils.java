package com.dcdineshk.googlemap.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {

    static SharedPreferences sp;

    public SharedPreferenceUtils(Context mContext) {
        sp = mContext.getSharedPreferences(AppConstants.sp_Name, 0);
    }

    //----------------------FIRE BASE UID-------------------------
    public static String getUID() {
        return sp.getString(AppConstants.Remember_email, "");
    }

    public static void setUID(String flag) {
        sp.edit().putString(AppConstants.Remember_email, flag).apply();
    }

    //----------------------IS_SIGNUP-------------------------
    public static Boolean getSignup() {
        return sp.getBoolean(AppConstants.login_app, false);
    }

    public static void setSignup(Boolean flag) {
        sp.edit().putBoolean(AppConstants.login_app, flag).apply();
    }

    public static String getUUID() {
        return sp.getString(AppConstants.sp_UUID, "");
    }

    public static void setUUID(String flag) {
        sp.edit().putString(AppConstants.sp_UUID, flag).apply();
    }

    public static String getEmail() {
        return sp.getString(AppConstants.sp_Email, "");
    }


    public static void setEmail(String flag) {
        sp.edit().putString(AppConstants.sp_Email, flag).apply();
    }

    public static String getPassword() {
        return sp.getString(AppConstants.sp_Password, "");
    }

    public static void setPassword(String flag) {
        sp.edit().putString(AppConstants.sp_Password, flag).apply();
    }
    public static boolean getSaveSwitch() {
        return sp.getBoolean(AppConstants.sp_Switch_STATE, false);
    }

    public static void setSaveSwitch(Boolean flag) {
        sp.edit().putBoolean(AppConstants.sp_Switch_STATE, flag).apply();
    }

    public static String getImageURL() {
        return sp.getString(AppConstants.sp_URL_IMAGE, "");
    }

    public static void setImageURL(String flag) {
        sp.edit().putString(AppConstants.sp_URL_IMAGE, flag).apply();
    }
    
    public static String getUsername() {
        return sp.getString(AppConstants.sp_Username, "");
    }

    public static void setUsername(String flag) {
        sp.edit().putString(AppConstants.sp_Username, flag).apply();
    }

    public static String getPointsArr() {
        return sp.getString(AppConstants.PointsARR, "");
    }

    public static void setPointsArr(String flag) {
        sp.edit().putString(AppConstants.PointsARR, flag).apply();
    }
}
