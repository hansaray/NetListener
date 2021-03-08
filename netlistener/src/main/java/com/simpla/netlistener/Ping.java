package com.simpla.netlistener;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

class Ping extends AsyncTask<Context, Void, Context> {
    private Ping_navigator cb;
    private int mExitValue = 0;
    private Thread activeThread;

    Ping() {
    }

    @Override
    protected Context doInBackground(Context... contexts) {
        try
        {
            activeThread=Thread.currentThread();
            Thread.sleep(ConnectionHandler.getDelay());
            pingProcess();

        } catch (InterruptedException e) {
            pingProcess();
            if (ConnectionHandler.isLogsEnabled())
                System.out.println("Ping Exception:"+e.getMessage());
        }

        return contexts[0];
    }
    @Override
    protected void onPostExecute(Context context) {
        if(mExitValue==0){
            cb.replied(context);

        }else{
            cb.timeout(context);

        }
        cb.ended(context);
        super.onPostExecute(context);
    }

    private void pingProcess(){
        try {
            Runtime runtime = Runtime.getRuntime();
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + "8.8.8.8");/////////////
            mExitValue = mIpAddrProcess.waitFor();
            if (ConnectionHandler.isLogsEnabled())
                System.out.println(" Ping mExitValue " + mExitValue);


        }catch (InterruptedException | IOException e) {
            e.printStackTrace();
            if (ConnectionHandler.isLogsEnabled())
                System.out.println("Ping Exception:"+e);

        }
    }

    public Ping(Ping_navigator cb) {
        this.cb = cb;
    }

    Ping setCb(Ping_navigator cb) {
        this.cb = cb;
        return this;
    }

    void resume(){
        activeThread.interrupt();
    }

}