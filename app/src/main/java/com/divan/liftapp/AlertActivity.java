package com.divan.liftapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.divan.liftapp.FullscreenActivity.BAUDRATE;
import static com.divan.liftapp.FullscreenActivity.SIZEOFMASSAGE;
import static com.divan.liftapp.FullscreenActivity.UiSetting;
import static com.divan.liftapp.FullscreenActivity.ValAlert;
import static com.divan.liftapp.FullscreenActivity.ValNormal;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AlertActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Timer   mTimer = new Timer();
        MyTimerTask mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 1000);
        new CatTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    void FinishIt(){finish();}

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy EEEE HH:mm", Locale.getDefault()); //"dd:MM:yyyy EEEE HH:mm"
            final String strDate = simpleDateFormat.format(calendar.getTime());

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                  //  date.setText(strDate);
                    ((TextView)findViewById(R.id.textAlert)).setText(strDate);//TODO check it
                }
            });
        }
    }
    class CatTask extends AsyncTask<Void, byte[], Void> {

        FTDriver ftDriver;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ftDriver=new FTDriver((UsbManager)getSystemService(Context.USB_SERVICE));
            ftDriver.begin(FTDriver.BAUD9600);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while(true) {
                    Thread.sleep(BAUDRATE);
                    byte[] buf=new byte[SIZEOFMASSAGE];
                    ftDriver.read(buf);
                    publishProgress(buf);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(byte[]... values) {
            super.onProgressUpdate(values);
            getWindow().getDecorView().setSystemUiVisibility(UiSetting);
            if(values[0][FullscreenActivity.PosSpecialThings]==ValNormal){
                FinishIt();
            }
        }

    }
}