package com.divan.liftapp;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends SerialPortActivity {
    private final int UiSetting=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    private View mContentView;
    private View mControlsView;
    private TextView date,info,massage,number;
    private ImageView up,down,man,women,image;
    private FrameLayout frameLayout;
    private boolean mVisible;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private Setting setting;
    //Controller con;
    MediaPlayer mediaPlayer;
    AudioManager am;
    String SettingFolder="LiftApp",settingFile="setting.txt";
    String pathSDcard= Environment.getExternalStorageDirectory().getAbsolutePath();


    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        Toast.makeText(this, "Data Received", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Initialaze();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setting=new Setting(SettingFolder,settingFile);
        SetSetting();

        mTimer.schedule(mMyTimerTask, 1000, 1000);

        //con=new Controller(savedInstanceState);

        new CatTask().execute();

        SetBackGraund("1.bmp");
        SetTextViewMassage(info,setting.MassageFolder,"1.txt");


    }


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
    }


    private void PlayMusic(String fileName){
        try {
            mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.MusicFolder+'/'+fileName);
            if(f.canRead()) {
                mediaPlayer.setDataSource(f.getAbsolutePath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.start();
            }else{
                Toast.makeText(this,"Cann't read music file",Toast.LENGTH_LONG);
            }
        }catch (IOException e){

        }
    }
    private void SetBackGraund(String fileName){
        File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.BackGroundFolder+'/'+fileName);
        if(f.canRead()) {
            Bitmap bp = BitmapFactory.decodeFile(f.getAbsolutePath());
            Drawable d = new BitmapDrawable(getResources(), bp);
            frameLayout.setBackground(d);
        }else{
            Toast.makeText(this,"Cann't read background file",Toast.LENGTH_LONG);
        }
    }
    private void SetImageViewIcon(ImageView iv,String folderName,String fileName){
        File f=new File(pathSDcard+'/'+setting.MainPath+'/'+folderName+'/'+fileName);
        if(f.canRead()) {
            Bitmap bp = BitmapFactory.decodeFile(f.getAbsolutePath());
            iv.setImageBitmap(bp);
        }else{
            Toast.makeText(this,"Cann't read image file",Toast.LENGTH_LONG);
        }
    }
    private void SetTextViewMassage(TextView tv,String folderName,String fileName){
        try {
            File f=new File(pathSDcard + '/' + setting.MainPath + '/' + folderName + '/' + fileName);
            if(f.canRead()) {
            BufferedReader br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
            StringBuilder sb=new StringBuilder();
            String s=br.readLine();
            while (s!=null) {
                sb.append(s+'\n');
                s=br.readLine();
            }
            tv.setText(sb.toString());
            br.close();
            }else{
                Toast.makeText(this,"Cann't read massage file",Toast.LENGTH_LONG);
            }
        }catch (IOException r){

        }
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
        up=(ImageView)findViewById(R.id.imageUp);
        down=(ImageView)findViewById(R.id.imageDown);
        man=(ImageView)findViewById(R.id.man);
        women=(ImageView)findViewById(R.id.women);
        image=(ImageView)findViewById(R.id.imageMain);

        frameLayout=(FrameLayout)findViewById(R.id.mainLayout);
        //con=new Controller();


        String infoSt="Производитель : РФ \n" +
                "400кг 5 пасс. \n" +
                "№0000000001 2016г \n" +
                "Сделано в России";
        info.setText(infoSt);





    }

    private void SetSetting()
    {
        date.setTextSize(setting.TextDateSize);
        massage.setTextSize(setting.TextMassageSize);
        info.setTextSize(setting.TextInfoSize);
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

    class CatTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                while(true) {
                    TimeUnit.SECONDS.sleep(2);
                    publishProgress(1);
                                    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            getWindow().getDecorView().setSystemUiVisibility(UiSetting);
        }

           }

}
