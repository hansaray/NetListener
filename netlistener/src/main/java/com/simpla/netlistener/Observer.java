package com.simpla.netlistener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.IntRange;

public class Observer {

    private static Observer instance;
    private ConnectionHandler receiver;
    private IntentFilter filter = new IntentFilter();
    private Activity activity;
    private Context context;

    static Observer getInstance(Activity activity){
        if (instance==null)
            instance = new Observer(activity);

        return instance;
    }

    static Observer getInstance(Context context){
        if (instance==null)
            instance = new Observer(context);

        return instance;
    }

    static boolean getConnectionInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null;
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                // connected to the internet
                return activeNetwork != null;
            }
        }else{
            return false;
        }
    }

    private Observer(Activity activity) {
        try {
            this.activity = activity;
            receiver = new ConnectionHandler();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Observer(Context context) {
        try {

            this.context = context;
            receiver = new ConnectionHandler();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Observer setIcon(int icon){
        ConnectionHandler.setIcon(icon);
        return this;
    }

    public void build(){
        try{
            if (context==null) {
                activity.startService(new Intent(activity, OnKillApp.class));
                activity.registerReceiver(receiver, filter);

                ConnectionHandler.checkState(activity);
            }else{
                context.startService(new Intent(context, OnKillApp.class));
                context.registerReceiver(receiver, filter);

                ConnectionHandler.checkState(context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Observer setCallBack(InternetConnectionListener uiNavigator){
        ConnectionHandler.setUiNavigator(uiNavigator);
        return this;
    }

    public Observer setMessage(String message){
        ConnectionHandler.setMessage(message);
        return this;
    }

    public Observer setSnackBarCancelable(boolean autoCancel) {
        ConnectionHandler.setCancelable(autoCancel);
        return this;
    }


    public Observer setSnackBarDuration(@IntRange(from=-2,to=0) int duration) {
        ConnectionHandler.setSnackBarTime(duration);
        return this;
    }

    public Observer setSnackBarEnabled(boolean enabled){
        ConnectionHandler.setSnackBarEnabled(enabled);
        return this;
    }

    public static boolean getSnackBarEnabled() { return ConnectionHandler.getSnackBarEnabled();}

    public static int getSnackBarDuration() { return ConnectionHandler.getSnackBarTime();}

    public Observer setBackground(Drawable drawable) {
        ConnectionHandler.setDrawable(drawable);
        return this;
    }

    public static Drawable getBackground() { return ConnectionHandler.getDrawable();}

    public Observer setTextColor(int color) {
        ConnectionHandler.setColor(color);
        return this;
    }

    public static int getTextColor() { return ConnectionHandler.getColor();}

    public static String getMessage() {
        return ConnectionHandler.getMessage();
    }

    void unregister() {
        if (context==null)
            receiver.unregister(activity);
        else
            receiver.unregister(context);
    }

    Activity getActivity() {
        return context==null?activity: (Activity) context;
    }

    boolean isConnected() {
        return ConnectionHandler.isConnected();
    }

    static int getConnectionType(Context context) { return ConnectionHandler.getConnectionType(context);}

    public boolean logsEnabled() {
        return ConnectionHandler.isLogsEnabled();
    }
    public Observer setLogsEnabled(boolean logsEnabled){
        ConnectionHandler.setLogsEnabled(logsEnabled);
        return this;
    }

    public Observer setMaxDelay(int maxDelay){
        ConnectionHandler.setGeneralPingIntervalMaxDelay(maxDelay);
        return this;
    }

    public Observer setMinDelay(int minDelay){
        ConnectionHandler.setGeneralPingIntervalMinDelay(minDelay);
        return this;
    }

    public int getMaxDelay(){
        return ConnectionHandler.getGeneralPingIntervalMaxDelay();
    }

    public int getMinDelay(){
        return ConnectionHandler.getGeneralPingIntervalMinDelay();
    }

    public int getSensitivity(){
        return ConnectionHandler.getSensitivity();
    }

    public Observer setSensitivity(int sensitivity){
        ConnectionHandler.setSensitivity(sensitivity);
        return this;
    }
}