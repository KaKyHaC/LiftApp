package com.divan.liftapp.Activitys;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divan.liftapp.FTDriver;
import com.divan.liftapp.Utils.FileManager;
import com.divan.liftapp.Fragments.FragmentImage;
import com.divan.liftapp.Fragments.FragmentText;
import com.divan.liftapp.Fragments.FragmentVideo;
import com.divan.liftapp.Fragments.MyFragment;
import com.divan.liftapp.R;
import com.divan.liftapp.Utils.DisconnectCounter;
import com.divan.liftapp.Utils.LaunchLiftAppService;
import com.divan.liftapp.Utils.LogToFile;
import com.divan.liftapp.Utils.QueuePlayer;
import com.divan.liftapp.Utils.RebootSystem;
import com.divan.liftapp.Wifi.WiFiDirectActivity;

import com.example.universalliftappsetting.Setting;
import com.example.universalliftappsetting.settingmenu.DateSetting;
import com.example.universalliftappsetting.settingmenu.NumberedSetting;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends MyFullscreeanActivity {
    FullscreenActivity.Main main;
    WiFiDirectActivity wiFiDirectActivity;

    boolean isAsyn=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((FrameLayout)findViewById(R.id.fragment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TotalReboot(1);
//                Intent intent = new Intent(context,ActivitySetting.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    void StartAsync() {
        if (!isAsyn) {
            main = new FullscreenActivity.Main();
            main.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            RunWiFiTask();
            isAsyn = true;
        }

    }

    @Override
    void FinishAsync() {
        if(isAsyn) {
            if (main != null)
                main.cancel(false);
            StopWiFiTask();
            isAsyn = false;
        }
    }
    public void TotalReboot(final long sleepTime){
        new RebootSystem(this).startLaunchService();
    }

    public void SetSettingFromWiFi(){
        setting.StartRead();
        this.SetSetting();
        main.setFiles();
    }
    public void RunWiFiTask(){
        wiFiDirectActivity=new WiFiDirectActivity(this);
        wiFiDirectActivity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void StopWiFiTask(){
        if(wiFiDirectActivity!=null)
            wiFiDirectActivity.cancel(false);
    }



    enum Fragment{Video,Image,Text}
    class Main extends AsyncTask<Void,byte[],Void>{
        String PathToLiftApp=pathSDcard + '/' + setting.folderLiftApp +'/' ;
        List<String> images,backGrounds,musics,videos;
        FTDriver ftDriver;
        MyFragment myFrag;
        int nBack=0,nMusic=0;
        int musicSeek=0;
        boolean isOpen=false;
        boolean isMusicPlayed=false;
        private DisconnectCounter disconnectCounter=new DisconnectCounter(10);
        LogToFile logToFile;

        public Main(){
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            number.setText("b");

            ftDriver=new FTDriver((UsbManager)getSystemService(Context.USB_SERVICE));
            isOpen=ftDriver.begin(NumberedSetting.BAUDRATE[setting.indexBAUDRATE.value%NumberedSetting.BAUDRATE.length],setting.sizeOfBuffer.value);

            images=new LinkedList<>();
            backGrounds=new LinkedList<>();
            musics=new LinkedList<>();
            videos=new LinkedList<>();

            setFiles();

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

            try{
                logToFile=new LogToFile(PathToLiftApp+"log.txt");
            }catch (Exception e){
                Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        public void setFiles(){
            synchronized (massage) {
                massage.setText(FileManager.getAllTextFromDirectory(PathToLiftApp + setting.folderMassage));
            }synchronized (images) {
                images = FileManager.getAllFilesPath(PathToLiftApp + setting.folderImage, "BMP", "bmp", "jpg", "JPG");
            }synchronized (backGrounds){
                backGrounds=FileManager.getAllFilesPath(PathToLiftApp+setting.folderBackGraund,"BMP","bmp","jpg","JPG");
            }synchronized (musics) {
                musics = FileManager.getAllFilesPath(PathToLiftApp + setting.folderMusic, "mp3", "wav");
            }synchronized (videos) {
                videos = FileManager.getAllFilesPath(PathToLiftApp + setting.folderVideo, "mp4", "3gp");
                fragVideo.setVideos(videos);
            }
            fragImage.setLists(images,musics);
        }

        @Override
        protected Void doInBackground(Void... params) {
//            return testProcess();
            return mainProcess();
        }

        private Void mainProcess(){
            byte[] buf = new byte[setting.sizeOfBuffer.value];//work while size=64

            while(true){
                try {
                    if (isCancelled()) {
                        ftDriver.end();
                        return null;
                    }
                    System.gc();
                    //isOpen=ftDriver.isConnection();
                    //isOpen=ftDriver.begin(FTDriver.BAUD9600);// work while it commented (2d branch)
                    if (isOpen) {
                        ftDriver.readInMy(buf);// work while read(buf)
                        //isOpen=ftDriver.isConnection();
                    } else//comment it (2d branch)
                    {
                        isOpen = ftDriver.begin(NumberedSetting.BAUDRATE[setting.indexBAUDRATE.value % NumberedSetting.BAUDRATE.length], setting.sizeOfBuffer.value);
                    }
                    publishProgress(buf);

                    Thread.sleep(BAUDRATE);
                }catch (Exception e){
                    try {
                        logToFile.Log(e.toString());
                    } catch (Exception e1) {
//                        e1.printStackTrace();
                    }
                }
            }
        }
        private Void testProcess(){
            byte[] buf = new byte[setting.sizeOfBuffer.value];//work while size=64

            isOpen=true;
          byte[][] test=new byte[][]{
          //          0 1 2 3 4 5 6 7 8 9
                    {50,4,0,2,0,0,0,0,1,0},
                    {50,1,0,3,0,0,0,0,0,0},
                    {50,2,0,4,0,0,0,0,1,0},
                   /* {50,4,0,0,0,0,0,1,0,0},
                    {50,5,0,0,0,9,0,0,0,0},
                    {50,6,4,1,0,0,0,2,0,0},
                    {50,7,5,0,0,4,0,2,1,0},
                    {50,8,0,0,0,9,0,0,0,0},
                    {50,9,6,0,0,9,0,1,0,0},
                    {50,(byte)231,0,4,0,0,0,0,0,0},*/
                    {50,3,0,3,0,0,0,0,0,0}};
            int index=0;
            while(true){
                if (isCancelled()) return null;
                publishProgress(test[index++%test.length]);
                try{
                TimeUnit.SECONDS.sleep(2);
                 }catch(InterruptedException e){}
                 }
        }

        @Override
        protected void onProgressUpdate(byte[]... values) {
            super.onProgressUpdate(values);
            try {
                byte[] b = values[0];
                if (isOpen) {
                    if (b[0] == 50) {
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

                        disconnectCounter.reSet();
                    } else {
                        if (disconnectCounter.isDisconnected()) {
                            Toast.makeText(context, "Reboot", Toast.LENGTH_SHORT).show();
                            TotalReboot(5000);
                            disconnectCounter.reSet();
                        }
                    }
                } else {
                    number.setText("-");
                    Images((byte) 7);
                    Media((byte) 8);
                }
                FullScreencall();
            }catch (Exception e){
                try {
                    logToFile.Log(e.toString());
                } catch (Exception e1) {
//                    e1.printStackTrace();
                }
            }
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
                case 5:SetImageViewIcon(imageArrow,setting.folderImage.toString(),"overload.png");break;
                case 6:SetImageViewIcon(imageArrow,setting.folderImage.toString(),"photoreverse.png");break;
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
                soundPlayer.add(PathToLiftApp + setting.folderSound + '/' + String.valueOf(b) + ".mp3");
                priorityPause();
            }
        }
        void SpecialSound(byte b){
            if(b!=0) {
                specialSoundPlayer.add(PathToLiftApp + setting.folderSpecialSound + '/' + String.valueOf(b) + ".mp3");
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
                RunWiFiTask();
            }else if(b==9){
                StopWiFiTask();
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
                if(videos.size()>0||true) {
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
            fragText.setText(vS.toString());
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


}