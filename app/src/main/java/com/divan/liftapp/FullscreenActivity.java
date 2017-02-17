package com.divan.liftapp;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Instrumentation;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.UiThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.divan.liftapp.Fragments.FragmentImage;
import com.divan.liftapp.Fragments.FragmentText;
import com.divan.liftapp.Fragments.FragmentVideo;
import com.divan.liftapp.Fragments.MyFragment;
import com.divan.liftapp.ActivitySetting;
import com.divan.liftapp.Wifi.WiFiDirectActivity;
import com.divan.liftapp.settingmenu.DateSetting;
import com.divan.liftapp.settingmenu.NumberedSetting;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    public static final int UiSetting=View.SYSTEM_UI_FLAG_FULLSCREEN
            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            |View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
    public static final int SIZEOFMASSAGE=64;
    public static final int BAUDRATE=100,PosSpecialThings=9,ValAlert=2,ValNormal=2,PosSetStation=12;
    public static final  String SettingFolder="LiftApp",settingFile="setting.txt",GONG="gong.wav";
    public static String pathSDcard= Environment.getExternalStorageDirectory().getAbsolutePath();


    private View mContentView;
    private View mControlsView;
    private TextView date,info,massage,number;
    private ImageView imageArrow,fire,ring;
    private FrameLayout frameLayout;
    private boolean mVisible,voiceSupport=false;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private Setting setting;
    MediaPlayer musicPlayer;
    QueuePlayer soundPlayer,specialSoundPlayer;
    AudioManager am;
    FragmentVideo fragVideo;
    FragmentText fragText;
    FragmentImage fragImage;


    final Context context=this;

   // CatTask catTask;
    Main main;
    boolean isAsyn=false;

    enum PathOfDay{DAY,NIGHT};
    PathOfDay pathOfDay=null;

    Drawable drawableUp,drawableDown,drawableRing;

    WiFiDirectActivity wiFiDirectActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        Initialaze();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)supportActionBar.hide();

        android.app.ActionBar actionBar=getActionBar();
        if(actionBar!=null)actionBar.hide();


        setting=new Setting(SettingFolder,settingFile);
        SetSetting();

        mTimer.schedule(mMyTimerTask, 1000, 1000);

        ((FrameLayout)findViewById(R.id.fragment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ActivitySetting.class);
                startActivity(intent);
            }
        });
//        StartAsync();

    }

    private void StartAsync(){
        if(!isAsyn) {
           // catTask = new CatTask();
            main = new Main();

            if (!isAsyn &&  main != null) {
                RunWiFiTusk();
                //   catTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                main.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                isAsyn = true;

            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
       // musicPlayer.start();
       StartAsync();
        setting.StartRead();
        SetSetting();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(musicPlayer!=null)
            musicPlayer.pause();
        if(main!=null)
            main.cancel(false);
        if(wiFiDirectActivity!=null)
            wiFiDirectActivity.cancel(false);
        /*if(catTask!=null)
            catTask.cancel(false);*/
        isAsyn=false;
    }


    private void PlayMusic(String fileName){
        try {
            //mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.MusicFolder+'/'+fileName);
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
    private void PlaySound(String fileName){
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.SoundFolder+'/'+fileName);
            soundPlayer.add(f.getAbsolutePath());
    }
    private void PlaySpecialSound(String fileName){
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.SpecialSoundFolder+'/'+fileName);
            specialSoundPlayer.add(f.getAbsolutePath());
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
    private void Initialaze() {
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        date=(TextView)findViewById(R.id.date);
        massage=(TextView)findViewById(R.id.massage);
        info=(TextView)findViewById(R.id.information);
        number=(TextView)findViewById(R.id.number);
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        imageArrow=(ImageView)findViewById(R.id.imageArrow);
        fire=(ImageView)findViewById(R.id.fire);
        ring=(ImageView)findViewById(R.id.ring);

        drawableUp=getResources().getDrawable(R.drawable.up);
        drawableDown=getResources().getDrawable(R.drawable.down);
        drawableRing=getResources().getDrawable(R.drawable.ring);


        frameLayout=(FrameLayout)findViewById(R.id.mainLayout);
        //con=new Controller();


        String infoSt="Производитель : РФ \n" +
                "400кг 5 пасс. \n" +
                "№0000000001 2016г \n" +
                "Сделано в России";
        info.setText(infoSt);


        StringBuilder sb=new StringBuilder(massage.getText());
        for(int i=0;i<100;i++)
            sb.append("  ");
        sb.append(massage.getText());
        for(int i=0;i<100;i++)
            sb.append("  ");
        //massage.setText(sb.toString());

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
        //fragmentTransaction.add(R.id.fragment, fragText);

        fragmentTransaction.commit();

        //videoView=((VideoView) fragVideo.getView().findViewById(R.id.videoFragment));


        wiFiDirectActivity=new WiFiDirectActivity(this);
    }

    private void SetSetting() {
        pathSDcard=setting.pathSDcard;
        date.setTextSize(setting.TextDateSize.value);
        massage.setTextSize(setting.TextMassageSize.value);
        info.setTextSize(setting.TextInfoSize.value);
        number.setTextSize(setting.NumberSize.value);
        SetTextViewMassage(info,setting.InformationFolder,"information.txt");

        int color=(int)Long.parseLong(setting.textColorHex.toString(),16);
        date.setTextColor(color);
        massage.setTextColor(color);
        info.setTextColor(color);
        number.setTextColor(color);




        int colorBack=(int )Long.parseLong(setting.LayOutBackGraundColor.toString(),16);
        SetBackGraunds(colorBack);

        int iconColor=(int )Long.parseLong(setting.iconColor.getColor(),16);
        drawableUp.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        drawableDown.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        drawableRing.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);

        pathOfDay=null;
        SetVolume();

        MyFragment curFrag=((MyFragment)this.getFragmentManager().findFragmentById(R.id.fragment));
        if(curFrag!=null)
            curFrag.onUpdate(0,0);



    }
    private void SetBackGraunds(int color){
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
    private void SetVolume(){
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

    public void SetSettingFromWiFi(){
        setting.StartRead();
        this.SetSetting();

    }
    public void RunWiFiTusk(){
            StopWiFiTusk();
            wiFiDirectActivity=new WiFiDirectActivity(this);
            wiFiDirectActivity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void StopWiFiTusk(){
        if(wiFiDirectActivity!=null)
            wiFiDirectActivity.cancel(false);
    }


    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            Date curDate =calendar.getTime();
//            Long deltaTime=setting.year.deltaTime+setting.month.deltaTime+setting.day.deltaTime+setting.hour.deltaTime+setting.min.deltaTime;
            curDate.setTime(curDate.getTime()+ DateSetting.deltaTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(setting.typeDate, Locale.getDefault()); //"dd:MM:yyyy EEEE HH:mm"
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


    enum Fragment{Video,Image,Text};
    class Main extends AsyncTask<Void,byte[],Void>{
        String PathToLiftApp=pathSDcard + '/' + setting.MainPath +'/' ;
        List<String> images,backGrounds,musics,videos;
        FTDriver ftDriver;
        MyFragment myFrag;
        int nBack=0,nMusic=0;
        int musicSeek=0;
        boolean isOpen=false;
        boolean isMusicPlayed=false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            number.setText("b");

            ftDriver=new FTDriver((UsbManager)getSystemService(Context.USB_SERVICE));
            isOpen=ftDriver.begin(NumberedSetting.BAUDRATE[setting.indexBAUDRATE.value%NumberedSetting.BAUDRATE.length],setting.sizeOfBuffer.value);

            massage.setText(FileManager.getAllTextFromDirectory(PathToLiftApp + setting.MassageFolder));
            images= FileManager.getAllFilesPath(PathToLiftApp+setting.ImageFolder,"BMP","bmp","jpg","JPG");
            backGrounds=FileManager.getAllFilesPath(PathToLiftApp+setting.BackGroundFolder,"BMP","bmp","jpg","JPG");
            musics=FileManager.getAllFilesPath(PathToLiftApp+setting.MusicFolder,"mp3","wav");
            videos=FileManager.getAllFilesPath(PathToLiftApp+setting.ResourcesFolder,"mp4","3gp");

            fragVideo.setVideos(videos);
            fragImage.setLists(images,musics);

            if(musics.size()!=0) {
                musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            musicSeek = 0;
                            musicPlayer.stop();
                            musicPlayer.reset();
                            musicPlayer.setDataSource(musics.get(nMusic++ % musics.size()));
                            musicPlayer.prepare();
                            if(isMusicPlayed)
                                musicPlayer.start();
                        } catch (IOException e) {
                        }
                    }
                });
               // musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                musicPlayer.pause();
               // musicPlayer.start();
            }
        }
        @Override
        protected Void doInBackground(Void... params) {
            byte[] buf = new byte[setting.sizeOfBuffer.value];//work while size=64

          /*  isOpen=true;
          byte[][] test=new byte[][]{
          //          0 1 2 3 4 5 6 7 8 9
                    {50,1,1,0,0,0,0,0,0,0},
                    {50,2,2,0,0,0,0,0,0,0},
                    {50,3,2,0,0,1,0,1,1,0},
                    {50,4,0,0,0,0,0,1,0,0},
                    {50,5,0,0,0,9,0,0,0,0},
                    {50,6,4,1,0,0,0,2,0,0},
                    {50,7,5,0,0,4,0,2,1,0},
                    {50,8,0,0,0,9,0,0,0,0},
                    {50,9,6,0,0,9,0,1,0,0},
                    {50,(byte)231,0,4,0,0,0,0,0,0},
                    {50,8,0,5,0,0,0,3,1,0}};
            int index=0;
            while(true){
                if (isCancelled()) return null;
                publishProgress(test[index++%test.length]);
                try{
                TimeUnit.SECONDS.sleep(2);
                 }catch(InterruptedException e){}
                 }*/

            while(true){
             if (isCancelled()) {
                 ftDriver.end();
                 return null;
             }
                System.gc();
                //isOpen=ftDriver.isConnection();
                //isOpen=ftDriver.begin(FTDriver.BAUD9600);// work while it commented (2d branch)
                if(isOpen) {
                    ftDriver.readInMy(buf);// work while read(buf)
                    //isOpen=ftDriver.isConnection();
                }
                else//comment it (2d branch)
                {
                    isOpen=ftDriver.begin(NumberedSetting.BAUDRATE[setting.indexBAUDRATE.value%NumberedSetting.BAUDRATE.length],setting.sizeOfBuffer.value);
                }
                publishProgress(buf);
                try{
                    Thread.sleep(BAUDRATE);
                }catch (InterruptedException e){}
            }
        }
        @Override
        protected void onProgressUpdate(byte[]... values) {
            super.onProgressUpdate(values);
            byte [] b=values[0];
            if(isOpen) {
                if(b[0]==50) {
                    SpecialThings(b[PosSpecialThings]);
                    // Status(b[0]);
                    Flours(b[1]);
                    Images(b[2]);
                    //viewMassage(b);
                    NamedSound(b[4]);
                    Sounds(b[5]);
                    SpecialSound(b[6]);
                    Media(b[3]);
                    MucisPalyer(b[7]);
                    NextBackGraund(b[8]);
                }
            }
            else
            {
                number.setText("-");
                Images((byte)7);
                Media((byte)8);
            }
            FullScreencall();
        }

        void Status(byte b){
            if(b==1)
            {
                Intent intent = new Intent(context,ActivitySetting.class);
                startActivity(intent);
            }
        }
        void Flours(byte b){
            int myInt = b & 0xff;
            if(myInt!=0) {
                if (myInt > 0 && myInt <= 199)
                    number.setText(String.valueOf(myInt));
                else if(myInt==200)
                    number.setText("0");
                else if(myInt>200&&myInt<=220)
                {
                    myInt-=200;
                    myInt*=(-1);
                    number.setText(String.valueOf(myInt));
                }
                else if(myInt>220&&myInt<=233){
                    String sym="я";
                    switch (myInt){
                        case 221:sym="П";break;
                        case 222:sym="п";break;
                        case 223:sym="C";break;
                        case 224:sym="L";break;
                        case 225:sym="H";break;
                        case 226:sym="P";break;
                        case 227:sym="U";break;
                        case 228:sym="b";break;
                        case 229:sym="F";break;
                        case 230:sym="A";break;
                        case 231:sym="E";break;
                        case 232:sym="-";break;
                        case 233:sym="--";break;
                    }
                    number.setText(sym);
                }
            }
        }
        void Images(byte b){
            switch (b)
            {
                case 1:imageArrow.setImageDrawable(drawableUp);break;
                case 2:imageArrow.setImageDrawable(drawableDown);break;
                case 3:imageArrow.setImageResource(R.drawable.fire1);break;
                case 4:imageArrow.setImageDrawable(drawableRing);break;
                case 5:SetImageViewIcon(imageArrow,setting.ImageFolder,"overload.png");break;
                case 6:SetImageViewIcon(imageArrow,setting.ImageFolder,"photoreverse.png");break;
                case 7:imageArrow.setImageBitmap(null);
            }
        }
        void Media(byte b){
            switch(b){
                case 1:SetFragment(Fragment.Image);myFrag.onUpdate(0,1);break;

                case 5:SetFragment(Fragment.Text);myFrag.onUpdate(0,1);break;//fire
                case 6:SetFragment(Fragment.Text);myFrag.onUpdate(0,2);break;//overload
                case 7:SetFragment(Fragment.Text);myFrag.onUpdate(0,3);break;//No communication with the station
                case 8:SetFragment(Fragment.Text);myFrag.onUpdate(0,4);break;//No communication with the controller

            }
            if(setting.accessVideo.Access&&!QueuePlayer.isPlaying()){
                switch (b){
                    case 2:SetFragment(Fragment.Video);myFrag.onUpdate(0,2);break;//start
                    case 3:SetFragment(Fragment.Video);myFrag.onUpdate(0,1);break;//stop
                    case 4:SetFragment(Fragment.Video);myFrag.onUpdate(0,3);break;//next
                }
            }
        }
        void NamedSound(byte b){
            switch(b){
                case 1:PlaySpecialSound("fire.mp3");break;
                case 2:PlaySpecialSound("gong.mp3");break;
                case 3:PlaySpecialSound("fan.mp3");break;
                case 4:PlaySpecialSound("overload.mp3");break;
                case 5:PlaySpecialSound("up.mp3");break;
                case 6:PlaySpecialSound("down.mp3");break;
                case 7:PlaySpecialSound("out.mp3");break;
            }
        }
        void Sounds(byte b){
            if(b!=0) {
                soundPlayer.add(PathToLiftApp + setting.SoundFolder + '/' + String.valueOf(b) + ".mp3");
                priorityPause();
            }
        }
        void SpecialSound(byte b){
            if(b!=0) {
                specialSoundPlayer.add(PathToLiftApp + setting.SpecialSoundFolder + '/' + String.valueOf(b) + ".mp3");
                priorityPause();
            }
        }
        void MucisPalyer(byte b){

            if(setting.accessMusic.Access&&!QueuePlayer.isPlaying()) {
                if (musics.size() != 0) {
                    if (b == 1) {
                        musicPlayer.start();
                        isMusicPlayed = true;
                    } else if (b == 2) {
                        musicPlayer.pause();
                        isMusicPlayed = false;
                    } else if (b == 3) {
                        try {
                            musicPlayer.stop();
                            musicPlayer.reset();
                            musicPlayer.setDataSource(musics.get(nMusic++ % musics.size()));
                            musicPlayer.prepare();
                            musicPlayer.start();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        void NextBackGraund(byte b){
            if(backGrounds.size()>0&&b==1) {
                //frameLayout.startAnimation(animationFlipOut);
                Drawable drawable = BitmapDrawable.createFromPath(backGrounds.get((nBack++ % backGrounds.size())));
                frameLayout.setBackground(drawable);
            }}
        void SpecialThings(byte b){
            if(b==1) {
                Intent intent = new Intent(context,ActivitySetting.class);
                startActivity(intent);
            }else if(b==ValAlert){//2
              //  setContentView(R.layout.white);
                Intent intent = new Intent(context,AlertActivity.class);
                startActivity(intent);
            }else if(b==3){
                PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Your Tag");
                wl.acquire();
                wl.release();

            }else if(b==4){
                try {
                    Process mSU = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(mSU.getOutputStream());
                    os.writeBytes("input keyevent 26");
                    os.flush();
                    os.close();
                }catch (IOException e){}
            }else if(b==5){
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = 0;
                getWindow().setAttributes(params);
            }else if(b==6){
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = 100;
                getWindow().setAttributes(params);
            }else if(b==7){
                DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }else if(b==8){
                RunWiFiTusk();
            }else if(b==9){
                StopWiFiTusk();
            }
           /* if(b==8){
                InputConnection ic = getCurrentInputConnection();
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_WAKEUP))
            }*/
        }

        void SetFragment(Fragment fragment){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();


            if(fragment==Fragment.Image&&myFrag!=fragImage){
                fragmentTransaction.replace(R.id.fragment,fragImage);
                myFrag=fragImage;
            }
            else if(fragment==Fragment.Video&&myFrag!=fragVideo)
            {
                if(videos.size()>0) {
                    fragmentTransaction.replace(R.id.fragment, fragVideo);
                    myFrag = fragVideo;
                }
            }
            else if (fragment==Fragment.Text&&myFrag!=fragText)
            {
                fragmentTransaction.replace(R.id.fragment, fragText);
                myFrag = fragText;
            }

            fragmentTransaction.commit();
        }
        Vector<String> sBuf=new Vector<>();
        void viewMassage(byte[] b){
            boolean isMassage=false;
            StringBuilder s=new StringBuilder();
            for(int i=0;i<15;i++){
                if(b[i]!=0)
                    isMassage=true;

                s.append(String.valueOf(b[i]));
                if(i<14)s.append(",");
            }
            if(isMassage)
                sBuf.add(s.toString());
           SetFragment(Fragment.Text);
            StringBuilder vS=new StringBuilder();
            for(int i=sBuf.size()-1;i>=0&&i>sBuf.size()-10;i--)
            {
                vS.append(sBuf.elementAt(i)+'\n');
            }
            fragText.SetText(vS.toString());
        }
        void priorityPause(){
            if(QueuePlayer.isPlaying()) {
                if(musicPlayer.isPlaying())
                    musicPlayer.pause();
                if(myFrag!=null)
                    if (myFrag.equals(fragVideo))
                        myFrag.onUpdate(0, 0);
            }
        }
    }

    class Demo extends  AsyncTask<Void,Integer,Void>{
        private int index=1;
        String PathToLiftApp=pathSDcard + '/' + setting.MainPath +'/' ;
        List<String> images,backGrounds,musics,videos;
        int nImage=1,nBack=1,nMusic=0,nVideo=0;
        int maxLvl=9;
        boolean isFire=false;
        MyFragment myFrag;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            massage.setText(FileManager.getAllTextFromDirectory(PathToLiftApp + setting.MassageFolder));
            /*------------------------*/
            images= FileManager.getAllFilesPath(PathToLiftApp+setting.ImageFolder,"BMP","bmp","jpg","JPG");
            backGrounds=FileManager.getAllFilesPath(PathToLiftApp+setting.BackGroundFolder,"BMP","bmp","jpg","JPG");
            musics=FileManager.getAllFilesPath(PathToLiftApp+setting.MusicFolder,"mp3","wav");
            videos=FileManager.getAllFilesPath(PathToLiftApp+setting.ResourcesFolder,"mp4","3gp");

           /* if(videos.size()!=0) {
                videoView.setVideoPath(videos.get(nVideo++ % videos.size()));
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.setVideoPath(videos.get(nVideo++ % videos.size()));
                        videoView.start();
                    }
                });
                videoView.start();
            }*/
            fragVideo.setVideos(videos);
            fragImage.setLists(images,musics);

        }

        @Override

        protected Void doInBackground(Void... params) {

            int i=-1;
            publishProgress(1,1);
            try {
                while (true) {

                    for (i+=2 ; i <= maxLvl; i++) {
                        if (i == 9) {
                            publishProgress(i, 0);
                            TimeUnit.SECONDS.sleep(6);
                        }
                        if (i == maxLvl)
                            publishProgress(i, -1);
                        else
                            publishProgress(i, 1);
                        TimeUnit.SECONDS.sleep(3);
                    }

                    for (i-=2; i >= 1; i--) {
                        if (i == 3) {
                            publishProgress(i, 0);
                            TimeUnit.SECONDS.sleep(6);
                        }
                        if(i==1)
                            publishProgress(i,1);
                        else
                            publishProgress(i,-1);
                        TimeUnit.SECONDS.sleep(3);
                    }

                    publishProgress(2, 2);//fire
                    TimeUnit.SECONDS.sleep(5);

                }
            }catch (InterruptedException r){

            }
            return null;
        }

        protected void directionOfMovement(int value1 ){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();


            if(value1==1){
                imageArrow.setImageResource(R.drawable.up);
                
                fragmentTransaction.replace(R.id.fragment,fragImage);
                myFrag=fragImage;
            }
            else if(value1==-1)
            {
                imageArrow.setImageResource(R.drawable.down);
                if(videos.size()>0) {
                    fragmentTransaction.replace(R.id.fragment, fragVideo);
                    myFrag = fragVideo;
                }
            }

            fragmentTransaction.commit();
        }
        protected void isStopOnFloor(int value1,int val){
            if(value1==0){
                final Animation animationFlipOut,animationFlipIn;
                animationFlipIn = AnimationUtils.loadAnimation(context,
                        android.R.anim.slide_in_left);
                animationFlipOut = AnimationUtils.loadAnimation(context,
                        android.R.anim.slide_out_right);
                animationFlipOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(backGrounds.size()!=0) {
                            Drawable drawable = BitmapDrawable.createFromPath(backGrounds.get((nBack++ % backGrounds.size())));
                            frameLayout.setBackground(drawable);
                            frameLayout.startAnimation(animationFlipIn);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageArrow.setImageBitmap(null);

                if(backGrounds.size()>0) {
                    //frameLayout.startAnimation(animationFlipOut);
                    Drawable drawable = BitmapDrawable.createFromPath(backGrounds.get((nBack++ % backGrounds.size())));
                    frameLayout.setBackground(drawable);
                }
                PlaySound(String.valueOf(val)+".mp3");
                ring.setImageResource(R.drawable.ring);
               // mediaPlayer.pause();
            }
            else{
                ring.setImageBitmap(null);
                //mediaPlayer.start();
            }
        }
        protected void isFireAlert(int value1){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            if(value1==2)
            {
                imageArrow.setImageBitmap(null);
                fire.setImageResource(R.drawable.fire1);
                //fire.setVisibility(View.VISIBLE);
                isFire=true;
                fragmentTransaction.replace(R.id.fragment,fragText);

                PlaySpecialSound("fire.mp3");

            }
            else if(isFire==true)
            {
               // fire.setVisibility(View.INVISIBLE);
                fire.setImageBitmap(null);
                isFire=false;
                fragmentTransaction.replace(R.id.fragment,myFrag);
            }
            fragmentTransaction.commit();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int val=values[0];
            directionOfMovement(values[1]);
            isStopOnFloor(values[1],val);
            isFireAlert(values[1]);


            number.setText(String.valueOf(val));
            myFrag.onUpdate(val,values[1]);

          /*  if(images.size()!=0&&val%2==0) {
                Bitmap bm = BitmapFactory.decodeFile(images.get(nImage++ % images.size()));
                fragImage.setImage(bm);
            }*/

        }
    }

}