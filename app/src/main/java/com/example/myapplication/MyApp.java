package com.example.myapplication;

import android.app.Application;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

public static final String formatTimesTamp (long timestamp){
    Calendar cal=Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(timestamp);


    String data= DateFormat.getDateInstance().format("dd/mm/yyyy");

    return data;
 }





}
