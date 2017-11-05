package com.divan.liftapp.Activitys;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divan.liftapp.Fragments.FragmentImage;
import com.divan.liftapp.Fragments.FragmentText;
import com.divan.liftapp.Fragments.FragmentVideo;
import com.divan.liftapp.Fragments.MyFragment;
import com.divan.liftapp.R;
import com.divan.liftapp.Utils.LaunchLiftAppService;
import com.divan.liftapp.Utils.QueuePlayer;
import com.divan.liftapp.Wifi.WiFiDirectActivity;
import com.example.universalliftappsetting.Setting;
import com.example.universalliftappsetting.settingmenu.DateSetting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dima on 05.11.2017.
 */

abstract public class MyFullscreeanActivity extends AppCompatActivity {
    public static final int UiSetting= View.SYSTEM_UI_FLAG_FULLSCREEN
            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            |View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
    public static final int SIZEOFMASSAGE=64;
    public static final int BAUDRATE=100,PosSpecialThings=9,ValAlert=2,ValNormal=2,PosSetStation=12;
    public static final  String SettingFolder="LiftApp",settingFile="setting.txt",GONG="gong.wav";
    public static String pathSDcard= Environment.getExternalStorageDirectory().getAbsolutePath();


    public View mContentView;
    public View mControlsView;
    public TextView date,info,massage,number;
    public ImageView imageArrow,fire,ring;
    public FrameLayout frameLayout;
    public boolean mVisible,voiceSupport=false;
    Timer mTimer;
    public Setting setting;
    MediaPlayer musicPlayer;
    QueuePlayer soundPlayer,specialSoundPlayer;
    AudioManager am;
    FragmentVideo fragVideo;
    FragmentText fragText;
    FragmentImage fragImage;


    final Context context=this;

    // CatTask catTask;

    enum PathOfDay{DAY,NIGHT};
    PathOfDay pathOfDay=null;

    Drawable drawableUp,drawableDown,drawableRing;

    private FullscreenActivity.MyTimerTask mMyTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)supportActionBar.hide();

        android.app.ActionBar actionBar=getActionBar();
        if(actionBar!=null)actionBar.hide();

        setting=new Setting(SettingFolder,settingFile);
        Initialaze();

        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000, 1000);

    }

    public void Initialaze() {
//        main=new Main();
//        wiFiDirectActivity=new WiFiDirectActivity(this);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        date=(TextView)findViewById(R.id.date);
        massage=(TextView)findViewById(R.id.massage);
        info=(TextView)findViewById(R.id.information);
        number=(TextView)findViewById(R.id.number);
        mTimer = new Timer();
        imageArrow=(ImageView)findViewById(R.id.imageArrow);
        fire=(ImageView)findViewById(R.id.fire);
        ring=(ImageView)findViewById(R.id.ring);

        drawableUp=getResources().getDrawable(R.drawable.up);
        drawableDown=getResources().getDrawable(R.drawable.down);
        drawableRing=getResources().getDrawable(R.drawable.ring);

        frameLayout=(FrameLayout)findViewById(R.id.mainLayout);

        String infoSt="Производитель : РФ \n" +
                "default text";
        info.setText(infoSt);


        /*StringBuilder sb=new StringBuilder(massage.getText());
        for(int i=0;i<100;i++)
            sb.append("  ");
        sb.append(massage.getText());
        for(int i=0;i<100;i++)
            sb.append("  ");
        //massage.setText(sb.toString());*/

        massage.setSelected(true);

        musicPlayer = new MediaPlayer();
        soundPlayer  = new QueuePlayer(new MediaPlayer());
        specialSoundPlayer = new QueuePlayer(new MediaPlayer());

        fragVideo= new FragmentVideo();
        fragText=new FragmentText();
        fragImage=new FragmentImage();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        //TODO wtf 2 add
        fragmentTransaction.add(R.id.fragment,fragImage);
//        fragmentTransaction.add(R.id.fragment, fragText);

        fragmentTransaction.commit();
    }

    public boolean isContainSdCard(){
        File root=Environment.getExternalStorageDirectory().getParentFile();
        File[] files=root.listFiles();
        if(files==null)
            return false;
        for(File f : files) {
            if (f.getAbsolutePath().contains("extsd")) {
                if (f.getTotalSpace() > 0)
                    return true;
            }
        }
        return false;
    }
    public void setTextFragmentAsWithoutSD(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment,fragText);
        fragmentTransaction.commit();
        fragText.onUpdate(0,5);//without SD card
    }

    abstract void StartAsync();
    abstract void FinishAsync();
   
    @Override
    public void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
        if(!isContainSdCard())
        {
            setTextFragmentAsWithoutSD();
            return;
        }
        setting.StartRead();
        SetSetting();
        StartAsync();
    }
    @Override
    public void onPause() {
        super.onPause();
        if(musicPlayer!=null)
            musicPlayer.pause();
        
        FinishAsync();
    }



    public void PlayMusic(String fileName){
        try {
            //mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.folderMusic+'/'+fileName);
            if(f.canRead()) {
                musicPlayer.reset();
                musicPlayer.setDataSource(f.getAbsolutePath());
                musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                musicPlayer.prepareAsync();
                musicPlayer.start();
            }else{
                Toast.makeText(this,"Cann't read music file",Toast.LENGTH_LONG);
            }
        }catch (IOException e){

        }
    }
    public void PlaySound(String fileName){
        File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.folderSound+'/'+fileName);
        soundPlayer.add(f.getAbsolutePath());
    }
    public void PlaySpecialSound(String fileName){
        File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.folderSpecialSound+'/'+fileName);
        specialSoundPlayer.add(f.getAbsolutePath());
    }
    public void SetBackGraund(String fileName){
        File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.folderBackGraund+'/'+fileName);
        if(f.canRead()) {
            Bitmap bp = BitmapFactory.decodeFile(f.getAbsolutePath());
            Drawable d = new BitmapDrawable(getResources(), bp);
            frameLayout.setBackground(d);
        }else{
            Toast.makeText(this,"Cann't read background file",Toast.LENGTH_LONG);
        }
    }
    public void SetImageViewIcon(ImageView iv,String folderName,String fileName){
        File f=new File(pathSDcard+'/'+setting.folderLiftApp+'/'+folderName+'/'+fileName);
        if(f.canRead()) {
            Bitmap bp = BitmapFactory.decodeFile(f.getAbsolutePath());
            iv.setImageBitmap(bp);
        }else{
            Toast.makeText(this,"Cann't read image file",Toast.LENGTH_LONG);
        }
    }
    public void SetTextViewMassage(TextView tv,String folderName,String fileName){
        try {
            File f=new File(pathSDcard + '/' + setting.folderLiftApp + '/' + folderName + '/' + fileName);
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

    public void FullScreencall() {
        View decorView=null;
        if(Build.VERSION.SDK_INT < 19) {//19 or above api
            decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.GONE|UiSetting);
        } else {
            //for lower api versions.
            decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  |  UiSetting;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    public void SetSetting() {
        pathSDcard=setting.pathSDcard;
        date.setTextSize(setting.sizeTextDate.value);
        massage.setTextSize(setting.sizeTextMassage.value);
        info.setTextSize(setting.sizeTextInfo.value);
        number.setTextSize(setting.sizeNumber.value);
//        SetTextViewMassage(info,setting.folderInformation.toString(),"information.txt");

        int color=(int)Long.parseLong(setting.colorText.toString(),16);
        date.setTextColor(color);
        massage.setTextColor(color);
        info.setTextColor(color);
        number.setTextColor(color);




        int colorBack=(int )Long.parseLong(setting.colorLayoutBackgraund.toString(),16);
        SetBackGraunds(colorBack);

        int iconColor=(int )Long.parseLong(setting.colorIcon.getColor(),16);
        drawableUp.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        drawableDown.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        drawableRing.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);

        pathOfDay=null;
        SetVolume();

        MyFragment curFrag=((MyFragment)this.getFragmentManager().findFragmentById(R.id.fragment));
        if(curFrag!=null)
            curFrag.onUpdate(0,0);

        //add 25.09.2017
        StringBuilder sb=new StringBuilder();
        sb.append("Производитель:РФ\n");
        sb.append(setting.capacityMass.getValue()+" кг, ");
        sb.append(setting.capacityPeople.getValue()+" пасс.");
        info.setText(sb.toString());

    }
    public void SetBackGraunds(int color){
        Drawable drawable=date.getBackground();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC);
        date.setBackground(drawable);

        drawable=info.getBackground();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC);
        info.setBackground(drawable);

        drawable=number.getBackground();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC);
        number.setBackground(drawable);

        drawable=massage.getBackground();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC);
        massage.setBackground(drawable);

        drawable=((LinearLayout)findViewById(R.id.iconsLayout)).getBackground();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC);
        ((LinearLayout)findViewById(R.id.iconsLayout)).setBackground(drawable);
    }
    public void SetVolume(){
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        PathOfDay now=((hour>=7&&hour<=22)?PathOfDay.DAY:PathOfDay.NIGHT);

        if(now!=pathOfDay||pathOfDay==null) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int value = ((hour >= 7 && hour <= 22) ? setting.volumeDay.value : setting.volumeNight.value);
            int res = value * max / 100;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, res, 0);
            pathOfDay=now;
        }
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            Date curDate =calendar.getTime();
//            Long deltaTime=setting.year.deltaTime+setting.month.deltaTime+setting.day.deltaTime+setting.hour.deltaTime+setting.min.deltaTime;
            curDate.setTime(curDate.getTime()+ DateSetting.deltaTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(setting.typeDate.toString(), Locale.getDefault()); //"dd:MM:yyyy EEEE HH:mm"
            final String strDate = simpleDateFormat.format(curDate);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    date.setText(strDate);
                    SetVolume();
                    //((TextView)findViewById(R.id.specialText)).setText(strDate);//TODO check it
                }
            });
        }
    }

}

