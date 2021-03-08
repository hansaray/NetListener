package com.simpla.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.simpla.netlistener.NetListener;

public class ShowOnceActivity extends AppCompatActivity {

    private ImageView image;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show);
        findIds();
        boolean connected = NetListener.isInternetConnected(getApplicationContext());
        setView(connected);
    }

    private void findIds(){
        image = findViewById(R.id.image);
        text = findViewById(R.id.info);
        ImageView back = findViewById(R.id.backButton);
        back.setOnClickListener(view -> onBackPressed());
    }

    private void setView(boolean connected){
        if(connected){
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                    ,R.drawable.ic_internet,getTheme()));
            String txt = getResources().getString(R.string.internet) + "!";
            text.setText(txt);
        }else{
            text.setText(getResources().getString(R.string.no_internet));
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources()
                    ,R.drawable.ic_no_internet_connection,getTheme()));
        }
    }
}