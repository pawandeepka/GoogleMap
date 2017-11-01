package com.dcdineshk.googlemap.utils;

import android.app.Application;

/**
 * Created by DC\dineshk on 12/10/17.
 */

public class GlobalClass extends Application{


    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public  String mDuration;
}
