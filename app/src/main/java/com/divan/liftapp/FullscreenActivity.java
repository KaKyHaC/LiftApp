package com.divan.liftapp;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.UiThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    public static final int UiSetting=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    public static final int SIZEOFMASSAGE=64;
    public static final int BAUDRATE=500,PosSpecialThings=9,ValAlert=2,ValNormal=1;



    private View mContentView;
    private View mControlsView;
    private TextView date,info,massage,number;
    private ImageView imageArrow,fire,ring;
    private FrameLayout frameLayout;
    private boolean mVisible,voiceSupport=false;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private Setting setting;
    MediaPlayer musicPlayer,soundPlayer,specialSoundPlayer;
    AudioManager am;
    FragmentVideo fragVideo;
    FragmentText fragText;
    FragmentImage fragImage;

    final String SettingFolder="LiftApp",settingFile="setting.txt",GONG="gong.wav";
    final String pathSDcard= Environment.getExternalStorageDirectory().getAbsolutePath();
    final Context context=this;


    private boolean isChecked(byte b,int pos){
        assert (pos<8);
        return (b&(byte)Math.pow(2,pos))!=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        Initialaze();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setting=new Setting(SettingFolder,settingFile);
        SetSetting();

        mTimer.schedule(mMyTimerTask, 1000, 1000);

        new CatTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new Main().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
      //  mediaPlayer.start();
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
        if(!soundPlayer.isPlaying())
        try {
          // MediaPlayer mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.SoundFolder+'/'+fileName);
            if(f.canRead()) {
                soundPlayer.reset();
                soundPlayer.setDataSource(f.getAbsolutePath());
                soundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                soundPlayer.setLooping(false);
                soundPlayer.prepare();
                soundPlayer.start();
            }else{
                Toast.makeText(this,"Cann't read music file",Toast.LENGTH_LONG);
            }
        }catch (IOException e){

        }
    }
    private void PlaySpecialSound(String fileName){
        if(!specialSoundPlayer.isPlaying())
        try {//  MediaPlayer mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);

            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.SpecialSoundFolder+'/'+fileName);
            if(f.canRead()) {
                specialSoundPlayer.reset();
                specialSoundPlayer.setDataSource(f.getAbsolutePath());
                specialSoundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                specialSoundPlayer.setLooping(false);
                specialSoundPlayer.prepare();
                specialSoundPlayer.start();
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
        /*image=(ImageView)findViewById(R.id.imageMain);
        videoView=(VideoView)findViewById(R.id.video);*/


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
        soundPlayer  = new MediaPlayer();
        specialSoundPlayer = new MediaPlayer();

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






    }

    private void SetSetting() {
        date.setTextSize(setting.TextDateSize);
        massage.setTextSize(setting.TextMassageSize);
        info.setTextSize(setting.TextInfoSize);
        number.setTextSize(setting.NumberSize);
        SetTextViewMassage(info,setting.InformationFolder,"information.txt");

        int color=(int)Long.parseLong(setting.textColorHex,16);
        date.setTextColor(color);
        massage.setTextColor(color);
        info.setTextColor(color);

       /* SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("DEVICE", setting.pathSerialPort);
        ed.commit();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mediaPlayer.pause();
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
                    //((TextView)findViewById(R.id.specialText)).setText(strDate);//TODO check it
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
                    //onDataReceived(new byte[5],5);
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

    enum Fragment{Video,Image,Text};
    class Main extends AsyncTask<Void,byte[],Void>{
        String PathToLiftApp=pathSDcard + '/' + setting.MainPath +'/' ;
        List<String> images,backGrounds,musics,videos;
        FTDriver ftDriver;
        MyFragment myFrag;
        int nBack=0,nMusic=0;
        int musicSeek=0;
        boolean isOpen=false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ftDriver=new FTDriver((UsbManager)getSystemService(Context.USB_SERVICE));
            isOpen=ftDriver.begin(FTDriver.BAUD9600);

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
                            musicPlayer.start();
                        } catch (IOException e) {
                        }
                    }
                });
                musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
               // musicPlayer.start();

            }
        }
        @Override
        protected Void doInBackground(Void... params) {

            byte[] signal=new byte[64];
           /* byte[][] test=new byte[][]{
                    {0,1,1,1,1,0,1,0,0,3},
                    {0,2,2,6,1,1,0,1,1,4},
                    {0,3,3,2,3,2,2,1,0,2},
                    {0,4,4,5,1,3,1,2,0,4},
                    {0,5,5,3,1,4,3,2,1,6},
                    {0,6,6,3,6,5,2,1,0,4},
                    {0,7,7,4,1,6,1,0,0,3},
                    {0,8,0,5,0,7,0,3,1,0}};
            int index=0;*/
            while(true){
          /*      publishProgress(test[index++%test.length]);
                try{
                TimeUnit.SECONDS.sleep(5);
                 }catch(InterruptedException e){}*/
                byte[] buf = new byte[SIZEOFMASSAGE];
                //isOpen=ftDriver.begin(FTDriver.BAUD9600);
                if(isOpen) {
                    ftDriver.read(buf);
                    isOpen=ftDriver.isConnection();
                }
                else {
                    isOpen=ftDriver.begin(FTDriver.BAUD9600);
                }
                publishProgress(buf,signal);
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
                SpecialThings(b[PosSpecialThings]);
                Flours(b[1]);
                Images(b[2]);
                Media(b[3]);
                NamedSound(b[4]);
                Sounds(b[5]);
                SpecialSound(b[6]);
                MucisPalyer(b[7]);
                NextBackGraund(b[8]);
            }
            else
            {
                number.setText("-");
                Media((byte)7);
                //fragText.SetText(ftDriver.getHistory());
                Images((byte)7);
            }


        }

        void Flours(byte b){
            int myInt = b & 0xff;
            number.setText(String.valueOf(myInt));
        }
        void Images(byte b){
            switch (b)
            {
                case 1:imageArrow.setImageResource(R.drawable.up);break;
                case 2:imageArrow.setImageResource(R.drawable.down);break;
                case 3:imageArrow.setImageResource(R.drawable.fire1);break;
                case 4:imageArrow.setImageResource(R.drawable.ring);break;
                case 5:SetImageViewIcon(imageArrow,setting.ImageFolder,"overload.png");break;
                case 6:SetImageViewIcon(imageArrow,setting.ImageFolder,"photoreverse.png");break;
                case 7:imageArrow.setImageBitmap(null);
            }
        }
        void Media(byte b){
            switch(b){
                case 1:SetFragment(Fragment.Image);myFrag.onUpdate(0,1);break;
                case 2:SetFragment(Fragment.Video);myFrag.onUpdate(0,1);break;//start
                case 3:SetFragment(Fragment.Video);myFrag.onUpdate(0,0);break;//stop
                case 4:SetFragment(Fragment.Video);myFrag.onUpdate(0,2);break;//next
                case 5:SetFragment(Fragment.Text);myFrag.onUpdate(0,1);break;//fire
                case 6:SetFragment(Fragment.Text);myFrag.onUpdate(0,2);break;//overload
                case 7:SetFragment(Fragment.Text);myFrag.onUpdate(0,3);break;//no connection

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
        void Sounds(byte b){PlaySound(String.valueOf(b)+"mp3");}
        void SpecialSound(byte b){PlaySpecialSound(String.valueOf(b)+"mp3");}
        void MucisPalyer(byte b){
            if(musics.size()!=0) {
                if (b == 1) {
                    musicPlayer.start();
                }
                if (b == 2) {
                    musicPlayer.pause();
                }
                if (b == 3) {
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
        void NextBackGraund(byte b){
            if(backGrounds.size()>0&&b==1) {
                //frameLayout.startAnimation(animationFlipOut);
                Drawable drawable = BitmapDrawable.createFromPath(backGrounds.get((nBack++ % backGrounds.size())));
                frameLayout.setBackground(drawable);
            }}
        void SpecialThings(byte b){
            if(b==ValNormal)
            {
              //  setContentView(R.layout.activity_fullscreen);
            }
            if(b==ValAlert){
              //  setContentView(R.layout.white);
                Intent intent = new Intent(context,AlertActivity.class);
                startActivity(intent);
            }
            if(b==3){
                PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Your Tag");
                wl.acquire();
                wl.release();

            }
            if(b==4){
                try {
                    Process mSU = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(mSU.getOutputStream());
                    os.writeBytes("input keyevent 26");
                    os.flush();
                    os.close();
                }catch (IOException e){}
            }
            if(b==5){
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = 0;
                getWindow().setAttributes(params);
            }
            if(b==6){
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = 100;
                getWindow().setAttributes(params);
            }
            if(b==7){
                DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
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