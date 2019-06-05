package com.alvaroruiz.tenisspfc;

import android.app.Activity;
import android.app.Application;

public class MyApp extends Application {
    public void onCreate(){
        super.onCreate();
    }
    Activity currentActivity = null;
    public Activity getActivity(){
        return currentActivity;
    }
    public  void setActivity(Activity activity){
        this.currentActivity = activity;
    }
}
