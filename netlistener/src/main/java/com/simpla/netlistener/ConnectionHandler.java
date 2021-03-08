package com.simpla.netlistener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntRange;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ConnectionHandler extends BroadcastReceiver {

    private static String TAG = ConnectionHandler.class.getSimpleName();
    private static int DISCONNECTED = 0;
    private static int CONNECTED = 3;
    private static int LAST_STATE = -1;
    private static int GENERAL_PING_INTERVAL_MAX_DELAY = 8000;
    private static int GENERAL_PING_INTERVAL_MIN_DELAY = 1000;
    private static int unchanged_counter = 0;
    private static InternetConnectionListener uiNavigator;
    private static int repeat = 1, sensitivity=5 ;
    private static Ping ping;
    private static Snackbar snackbar;
    //customize SnackBar values
    private static boolean cancelable = true,logsEnabled=false;
    private static String message;
    private static int icon = R.drawable.ic_no_internet;
    private static Drawable drawable;
    private static int color = R.color.GhostWhite;
    @IntRange(from = -2 ,to = 0)
    private static int snackBarTime = Snackbar.LENGTH_INDEFINITE;
    private static boolean snackBarEnabled = true;


    public ConnectionHandler() {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (LAST_STATE == getConnectivityStatus(context))return;
        unchanged_counter = 0; //Reduce delay so checker will be more sensitive for a while

        if (ping != null)ping.resume();

    }

    private static void detectAndAct(Context context, int status){
        try {
            if (LAST_STATE == status) return;
            else unchanged_counter = 0;//reset counter
            if (status == DISCONNECTED) {
                if (uiNavigator == null) {
                    if(snackbar != null) snackbar.dismiss();
                    return;
                } else {
                    View view = uiNavigator.onDisconnected();
                    if (view != null) showSnackBar(view,context);
                }
            } else {
                if (uiNavigator != null) {
                    hideSnackBar();
                    uiNavigator.onConnected(getConnectionType(context));
                }
            }
            LAST_STATE = status;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void hideSnackBar() {
        try {
            if(snackbar != null){
                snackbar.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void showSnackBar(View view, Context context) {
        try {
            if(snackBarEnabled) {
                if (view == null) return;
                if(message == null) message = context.getResources().getString(R.string.NoConnection);
                snackbar = Snackbar.make(view,"\t\t"+message
                        ,snackBarTime);
                if(!cancelable) snackbar.setBehavior(new NoSwipeBehavior());
                View snackBarLayout = snackbar.getView();
                if(drawable == null) drawable = context.getResources().getDrawable(R.drawable.snackbar_shape);
                snackBarLayout.setBackground(drawable);
                TextView textView = snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
                if(textView != null){
                    textView.setTextColor(context.getResources().getColor(color));
                    textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                    textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding));
                }
                snackbar.show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * SnackBar not cancelable future
     */
    static class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {

        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }

    /**
     * @param context;
     * @return Connectivity status as a Code
     * Return value can be :DISCONNECTED=0 or :CONNECTED=3
     */
    private static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {

            return CONNECTED;
        }
        return DISCONNECTED;
    }

    /**
     * getConnectionType()  Detects the connection type if one existed
     * @param context;
     * @return Type of Connection returned: NONE=0,TYPE_MOBILE=1,TYPE_WIFI=2,TYPE_VPN=3
     */
    static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: vpn
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = 3;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = 2;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = 1;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        result = 3;
                    }
                }
            }
        }
        return result;


    }

    public static void checkState(Context context){
        if (ping==null) {
            LAST_STATE= -1;
            repeat=1;
            initPing(context,repeat);
        }
//        else ping.resume();
    }

    /**
     * initPing() Tries to detects and understand connectivity in 2 available ways.
     *
     * @param context;
     * @param repeat
     *
     * restarts ping interval to make detection more sensitive by shorter ping delays
     */
    public static  void initPing(final Context context, int repeat){
        try{
            if (repeat==0){
                ConnectionHandler.repeat = 1;

            }else {
                ConnectionHandler.repeat = repeat;
            }


            ping=new Ping().setCb(new Ping_navigator() {
                @Override
                public void timeout(Context context) {
                    if (ConnectionHandler.repeat == 1) {
                        detectAndAct(context, ConnectionHandler.DISCONNECTED);
                    }
                }

                @Override
                public void replied(Context context) {

                    detectAndAct(context , ConnectionHandler.CONNECTED);
                    ConnectionHandler.repeat = sensitivity;
                }

                @Override
                public void ended(Context context) {
                    unchanged_counter ++;
                    ConnectionHandler.repeat = ConnectionHandler.repeat > 1 ? ConnectionHandler.repeat - 1:1;
                    initPing(context , ConnectionHandler.repeat );
                }
            });
            ping.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,context);



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * unregister() Removes listeners , receivers
     * @param context
     * Context to unregister network change receivers
     */
    public void unregister(Context context){
        hideSnackBar();
        try {
            context.unregisterReceiver(this);
        } catch(IllegalArgumentException ignored) {

        }
    }

    /**
     * Getter and Setter functions
     */
    public static int getSnackBarTime() {
        return snackBarTime;
    }

    public static void setSnackBarTime(int snackBarTime) {
        ConnectionHandler.snackBarTime = snackBarTime;
    }

    public static boolean getSnackBarEnabled() { return snackBarEnabled; }

    public static void setSnackBarEnabled(boolean snackBarEnabled) {
        ConnectionHandler.snackBarEnabled = snackBarEnabled;
    }

    public static int getColor() {
        return color;
    }

    public static void setColor(int color) {
        ConnectionHandler.color = color;
    }

    public static Drawable getDrawable() {
        return drawable;
    }

    public static void setDrawable(Drawable drawable) {
        ConnectionHandler.drawable = drawable;
    }

    public static void setIcon(int notificationIcon) {
        ConnectionHandler.icon = notificationIcon;
    }

    public static void setUiNavigator(InternetConnectionListener uiNavigator) {
        ConnectionHandler.uiNavigator = uiNavigator;
    }

    public static void setMessage(String message) {
        ConnectionHandler.message = message;
    }

    private static int getIcon() {
        return icon;
    }

    static long getDelay() {
        long delay=GENERAL_PING_INTERVAL_MIN_DELAY;
        try {

            int GENERAL_PING_INTERVAL_MULTIPLIER_MS = 20;
            delay = unchanged_counter * unchanged_counter * GENERAL_PING_INTERVAL_MULTIPLIER_MS;
            if (delay > GENERAL_PING_INTERVAL_MAX_DELAY)
                delay = GENERAL_PING_INTERVAL_MAX_DELAY;

            else if (delay < GENERAL_PING_INTERVAL_MIN_DELAY)
                delay = GENERAL_PING_INTERVAL_MIN_DELAY;


            if (logsEnabled)
                Log.e(TAG, unchanged_counter + "==" + delay);
        }catch (Exception e){
            e.printStackTrace();
        }
        return delay;
    }

    public static void setCancelable(boolean cancelable) {
        ConnectionHandler.cancelable = cancelable;
    }

    public static String getMessage() {
        return message;
    }

    public static boolean isCancelable() {
        return cancelable;
    }

    public static boolean isConnected() {
        return LAST_STATE == CONNECTED;
    }

    public static boolean isLogsEnabled() {
        return logsEnabled;
    }

    public static void setLogsEnabled(boolean logsEnabled) {
        ConnectionHandler.logsEnabled = logsEnabled;
    }

    public static int getGeneralPingIntervalMaxDelay() {
        return GENERAL_PING_INTERVAL_MAX_DELAY;
    }

    public static void setGeneralPingIntervalMaxDelay(int generalPingIntervalMaxDelay) {
        GENERAL_PING_INTERVAL_MAX_DELAY = generalPingIntervalMaxDelay;
    }

    public static int getGeneralPingIntervalMinDelay() {
        return GENERAL_PING_INTERVAL_MIN_DELAY;
    }

    public static void setGeneralPingIntervalMinDelay(int generalPingIntervalMinDelay) {
        GENERAL_PING_INTERVAL_MIN_DELAY = generalPingIntervalMinDelay;
    }

    public static int getSensitivity() {
        return sensitivity;
    }

    public static void setSensitivity(int sensitivity) {
        ConnectionHandler.sensitivity = sensitivity;
    }
}