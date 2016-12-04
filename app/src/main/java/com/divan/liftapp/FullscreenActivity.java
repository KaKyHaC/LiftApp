package com.divan.liftapp;

import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private View mContentView;
    private View mControlsView;
    private TextView date,info,massage,number;
    private boolean mVisible;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private Setting setting;
    Controller con;
    String SettingFolder="LiftApp",settingFile="setting.txt";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Initialaze();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        SetSetting();
        mTimer.schedule(mMyTimerTask, 1000, 1000);

        con=new Controller();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }



    private void Initialaze()
    {
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        date=(TextView)findViewById(R.id.date);
        massage=(TextView)findViewById(R.id.massage);
        info=(TextView)findViewById(R.id.information);
        number=(TextView)findViewById(R.id.number);
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        //con=new Controller();


        String infoSt="Производитель : РФ \n" +
                "400кг 5 пасс. \n" +
                "№0000000001 2016г \n" +
                "Сделано в России";
        info.setText(infoSt);

        setting=new Setting(SettingFolder,settingFile);



    }

    private void SetSetting()
    {
        date.setTextSize(setting.TextSize);
        massage.setTextSize(setting.TextSize);
        info.setTextSize(setting.TextSize);
        number.setTextSize(setting.NumberSize);
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("DEVICE", setting.pathSerialPort);
        ed.commit();
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(setting.typeDate, Locale.getDefault()); //"dd:MM:yyyy EEEE HH:mm"
            final String strDate = simpleDateFormat.format(calendar.getTime());

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    date.setText(strDate);
                }
            });
        }
    }

}
