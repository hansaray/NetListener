package com.simpla.netlistener;

import android.content.Context;

public interface Ping_navigator {
    void timeout(Context context);
    void replied(Context context);
    void ended(Context context);
}
