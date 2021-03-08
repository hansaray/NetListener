package com.simpla.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.simpla.netlistener.InternetConnectionListener;
import com.simpla.netlistener.NetListener;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {

    private Button showOnce,showLive;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findIds();
    }

    private void findIds(){
        showOnce = findViewById(R.id.nextButton);
        showLive = findViewById(R.id.next2Button);
        layout = findViewById(R.id.mainLayout);
        setListeners();
    }

    private void setListeners(){
        showOnce.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ShowOnceActivity.class)));
        showLive.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,ShowLiveActivity.class)));
    }

    @Override
    public void onConnected(int source) {
    }

    @Override
    public View onDisconnected() {
        return layout;
    }

    @Override
    protected void onResume() {
        NetListener.observer(MainActivity.this)
                .setSnackBarEnabled(true)
                .setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.snackbar_shape,getTheme()))
                .setTextColor(R.color.GhostWhite)
                .setIcon(R.drawable.ic_no_internet)
                .setLogsEnabled(false)
                .setSnackBarCancelable(false)
                .setSnackBarDuration(Snackbar.LENGTH_INDEFINITE)
                .setCallBack(this)
                .setSensitivity(4)
                .build();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetListener.unregister(MainActivity.this);
    }
}