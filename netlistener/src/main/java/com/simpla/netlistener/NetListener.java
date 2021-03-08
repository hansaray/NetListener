package com.simpla.netlistener;

import android.app.Activity;
import android.content.Context;

public class NetListener {

    public static Observer observer(Context context){
        return Observer.getInstance((Activity) context);
    }
    public static Observer observer(Activity activity){
        return Observer.getInstance(activity);
    }

    public static void unregister(Context context){
        Observer.getInstance(context).unregister();
    }

    public static boolean isActive(Context context){
        return Observer.getInstance((Activity) context).isConnected();
    }

    public static boolean isInternetConnected(Context context){
        return Observer.getConnectionInfo(context);
    }

    public static int getConnectionType(Context context){
        return Observer.getConnectionType(context);
    }
}