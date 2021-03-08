package com.simpla.netlistener;

import android.view.View;

public interface InternetConnectionListener {
    void onConnected(int source);
    View onDisconnected();
}