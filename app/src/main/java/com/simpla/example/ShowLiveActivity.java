package com.simpla.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.simpla.netlistener.InternetConnectionListener;
import com.simpla.netlistener.NetListener;

public class ShowLiveActivity extends AppCompatActivity implements InternetConnectionListener {

    private ImageView image;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show);
        findIds();
        boolean connected = NetListener.isInternetConnected(getApplicationContext());
        setView(connected,0);
    }

    private void findIds(){
        image = findViewById(R.id.image);
        text = findViewById(R.id.info);
        ImageView back = findViewById(R.id.backButton);
        back.setOnClickListener(view -> onBackPressed());
    }

    private void setView(boolean connected, int type){
        if(connected){
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                    ,R.drawable.ic_internet,getTheme()));
            String txt;
            if(type != 0){
                String typeTxt = "";
                switch (type){
                    case 1:
                        typeTxt = "with Cellular!";
                        break;
                    case 2:
                        typeTxt = "with WiFi!";
                        break;
                    case 3:
                        typeTxt = "with VPN!";
                        break;
                }
                txt = getResources().getString(R.string.internet) + " " + typeTxt;
            }else txt = getResources().getString(R.string.internet) + "!";
            text.setText(txt);
        }else{
            text.setText(getResources().getString(R.string.no_internet));
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                    ,R.drawable.ic_no_internet_connection,getTheme()));
        }
    }

    @Override
    public void onConnected(int source) {
        setView(true,source);
    }

    @Override
    public View onDisconnected() {
        setView(false,0);
        return null;
    }

    @Override
    protected void onResume() {
        NetListener.observer(ShowLiveActivity.this)
                .setSnackBarEnabled(false)
                .setLogsEnabled(false)
                .setCallBack(this)
                .setSensitivity(4)
                .build();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetListener.unregister(getApplicationContext());
    }
}