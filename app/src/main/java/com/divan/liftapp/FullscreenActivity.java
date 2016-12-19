package com.divan.liftapp;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.support.annotation.UiThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
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
public class FullscreenActivity extends SerialPortActivity {
    private final int UiSetting=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    private View mContentView;
    private View mControlsView;
    private TextView date,info,massage,number;
    private ImageView imageArrow,fire,ring;
    //ImageView image;
    private FrameLayout frameLayout;
    private boolean mVisible,voiceSupport=false;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private Setting setting;
    //private VideoView videoView;
    //Controller con;
    MediaPlayer mediaPlayer;
    AudioManager am;
    FragmentVideo fragVideo;
    FragmentText fragText;
    FragmentImage fragImage;

    final String SettingFolder="LiftApp",settingFile="setting.txt",GONG="gong.wav";
    final String pathSDcard= Environment.getExternalStorageDirectory().getAbsolutePath();
    final Context context=this;



    @Override
    protected void onDataReceived(final byte[] buffer,final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
            //TODO your logic
                Toast.makeText(context, "Data Received", Toast.LENGTH_LONG).show();
                if(size>=4) {
                    Evidence(buffer[3]);
                    if (voiceSupport) Orders(buffer[2]);
                    Floors(buffer[1]);
                    SpecialSignal(buffer[0]);
                }

            }
        });

    }
    //TODO режим
    private void Evidence(final byte b){
        runOnUiThread(new Runnable() {
            public void run() {
                   /* up.setVisibility(isChecked(b,0)?View.VISIBLE:View.INVISIBLE);
                    down.setVisibility(isChecked(b,1)?View.VISIBLE:View.INVISIBLE);*/
                if(isChecked(b,2))
                    PlaySound(GONG);
                voiceSupport=isChecked(b,3);
            }
        });

    }
    private void Orders(byte b){
        int myInt = b & 0xff;
        final String soundFile=String.valueOf(myInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaySound(soundFile);
            }
        });
    }
    private void Floors(byte b){
        final int myInt = b & 0xff;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                number.setText(String.valueOf(myInt));
            }
        });
    }
    private void SpecialSignal(byte b){
        int myInt = b & 0xff;
        final String soundFile=String.valueOf(myInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlaySpecialSound(soundFile);
            }
        });
    }
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

        new Demo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

       /* mediaPlayer=MediaPlayer.create(this,R.raw.nature);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
        mediaPlayer.start();
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
    private void PlaySound(String fileName){
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.SoundFolder+'/'+fileName);
            if(f.canRead()) {
                mediaPlayer.setDataSource(f.getAbsolutePath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(false);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }else{
                Toast.makeText(this,"Cann't read music file",Toast.LENGTH_LONG);
            }
        }catch (IOException e){

        }
    }
    private void PlaySpecialSound(String fileName){
        try {
             MediaPlayer mediaPlayer = new MediaPlayer();
            //mediaPlayer.setDataSource(filePath);
            File f=new File(pathSDcard+'/'+SettingFolder+'/'+setting.SpecialSoundFolder+'/'+fileName);
            if(f.canRead()) {
                mediaPlayer.setDataSource(f.getAbsolutePath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(false);
                mediaPlayer.prepare();
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

        mediaPlayer = new MediaPlayer();

        fragVideo= new FragmentVideo();
        fragText=new FragmentText();
        fragImage=new FragmentImage();


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();


        fragmentTransaction.add(R.id.fragment, fragImage);
        fragmentTransaction.commit();

        //videoView=((VideoView) fragVideo.getView().findViewById(R.id.videoFragment));






    }

    private void SetSetting()
    {
        date.setTextSize(setting.TextDateSize);
        massage.setTextSize(setting.TextMassageSize);
        info.setTextSize(setting.TextInfoSize);
        number.setTextSize(setting.NumberSize);
        SetTextViewMassage(info,setting.InformationFolder,"information.txt");

        int color=(int)Long.parseLong(setting.textColorHex,16);
        date.setTextColor(color);
        massage.setTextColor(color);
        info.setTextColor(color);

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("DEVICE", setting.pathSerialPort);
        ed.commit();
    }

    private void GravityNumber(){

    }
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
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
            myFrag.onApdate(val,values[1]);

          /*  if(images.size()!=0&&val%2==0) {
                Bitmap bm = BitmapFactory.decodeFile(images.get(nImage++ % images.size()));
                fragImage.setImage(bm);
            }*/

        }
    }

}