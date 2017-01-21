package com.divan.liftapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.divan.liftapp.Fragments.MyFragment;
import com.divan.liftapp.R;
import com.divan.liftapp.Setting;
import com.divan.liftapp.settingmenu.SettingItem;
import com.divan.liftapp.settingmenu.SpecialSetting;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import static com.divan.liftapp.FullscreenActivity.BAUDRATE;
import static com.divan.liftapp.FullscreenActivity.PosSetStation;
import static com.divan.liftapp.FullscreenActivity.SIZEOFMASSAGE;
import static com.divan.liftapp.settingmenu.SpecialSetting.stationNames;

public class ActivitySetting extends AppCompatActivity {
    public static final int UiSetting= View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    final String SettingFolder="LiftApp",settingFile="setting.txt",GONG="gong.wav",EXIT="Выход",DEFAULT="По умолчанию",STATION="Станции";
    Vector<SettingItem> settingItems;
    public Setting setting=new Setting(SettingFolder,settingFile);

    ListView listView;
    TextView name,value;
    //int indexSelected=-1;
   // boolean isSelect=false;
    View selectedView;
    SettingItem curItem;
    CharSequence curText=null;
    byte byteToSend=0;

    Context context;
    boolean isAsync=false;
    Main main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(UiSetting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // получаем экземпляр элемента ListView
        listView= (ListView)findViewById(R.id.listView);
        name=(TextView)findViewById(R.id.textName);
        name.setClickable(true);
        value=(TextView)findViewById(R.id.textValue);

        name.setBackgroundColor(Color.WHITE);
        value.setBackgroundColor(Color.WHITE);

        InitSettingItems();
        FillList();
        setListener();

        onListItemClick(0);
        context=this;
        StartAsync();

    }
    private void InitSettingItems(){
        settingItems=new Vector<>();
        settingItems.add(setting.LayOutBackGraundColor);
        settingItems.add(setting.textColorHex);
        settingItems.add(setting.textFragmentColor);
        settingItems.add(setting.iconColor);
        settingItems.add(setting.NumberSize);
        settingItems.add(setting.TextDateSize);
        settingItems.add(setting.TextInfoSize);
        settingItems.add(setting.TextMassageSize);
        settingItems.add(setting.textFragmenSize);
        settingItems.add(setting.sizeTextSetting);
        settingItems.add(setting.sizeOfBuffer);
        settingItems.add(setting.volumeDay);
        settingItems.add(setting.volumeNight);
        settingItems.add(setting.accessMusic);
        settingItems.add(setting.accessVideo);
        settingItems.add(new SpecialSetting(SpecialSetting.TypeSpecialItem.STATION,this,STATION));
        settingItems.add(new SpecialSetting(SpecialSetting.TypeSpecialItem.DEFAULT,this,DEFAULT));
        settingItems.add(new SpecialSetting(SpecialSetting.TypeSpecialItem.INSTRUCTION,this,"Инструкция"));
        settingItems.add(new SpecialSetting(SpecialSetting.TypeSpecialItem.EXIT,this,EXIT));


        ;
    }
    private void FillList(){
// определяем массив типа String
        String[] catNames = new String[settingItems.size()];
        for(int i=0;i<settingItems.size();i++){
            catNames[i]=settingItems.elementAt(i).getName();
        }

// используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, catNames);

        listView.setAdapter(adapter);
    }
    private void StartAsync(){
        if(!isAsync){
            main=new Main();
            main.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            isAsync=true;
        }
    }

    private void setListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                listView.smoothScrollToPosition(position);

                if(curItem!=null) {
                    curItem.setFocus(false);
                }
                    //indexSelected = position;
                    curItem= settingItems.elementAt(position);
                    curItem.setFocus(true);

                    updateView();

                curText=((TextView)itemClicked).getText();
                selectedView=itemClicked;
                focusItem(itemClicked);
                //itemClicked.setBackgroundColor(Color.BLUE);

            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    curItem.onClick(SettingItem.Key.up);
                    updateView();

                    if(curText.toString()==DEFAULT) {
                       MakeDefaultSetting();
                    }
                    else if(curText.toString()==EXIT)
                        Exit();

            }
        });
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   curItem.onClick(SettingItem.Key.down);
                   updateView();


                    if(curText.toString()==DEFAULT) {
                       MakeDefaultSetting();
                    }
                    else if(curText.toString()==EXIT)
                        Exit();

            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //focusItem(selected);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                focusItem(selectedView);
            }
        });
    }
    private void focusItem(View view){
        for(int i=0;i<listView.getCount();i++)
        {
            View a= listView.getChildAt(i);
            if(a!=null) {
                a.setBackgroundColor(Color.WHITE);
                if(((TextView)a).getText()==curText)
                    a.setBackgroundColor(Color.YELLOW);
            }
        }

    }
    private void updateView(){
        if(curItem!=null) {
            name.setText(curItem.getName());
            value.setText(curItem.getValue());
            String color = curItem.getColor();
            if (color != null) {
                value.setBackgroundColor((int) Long.parseLong(color, 16));
            } else {
                value.setBackgroundColor(Color.WHITE);
            }

            setSizeSetting();//bad idea
        }
    }
    private void setSizeSetting(){
        for(View e:listView.getTouchables()){
            ((TextView)e).setTextSize(setting.sizeTextSetting.value);
        }
        name.setTextSize(setting.sizeTextSetting.value);
        value.setTextSize(setting.sizeTextSetting.value);
    }

    public void onListItemClick(int pos) {

        int activePosition = pos; // первый элемент списка
        listView.performItemClick(listView.getAdapter().
                getView(activePosition, null, null), activePosition, listView.getAdapter().
                getItemId(activePosition));
    }
    @Override
    protected void onResume() {
        super.onResume();
        setting.StartRead();
        StartAsync();
        setSizeSetting();
    }
    @Override
    protected void onPause() {
        super.onPause();
        setting.WriteSetting();
        if(isAsync)
            main.cancel(false);
    }

    public void Exit(){finish();}
    public void MakeDefaultSetting(){
        setting.InitDefault();
        InitSettingItems();
        value.setText("Установлены значения по умолчанию");
        Toast.makeText(context, "make default", Toast.LENGTH_LONG);
    }
    public void SendByte(){
        byteToSend=(byte)(setting.indexCurStation%stationNames.length+1);
    }
    public void SendByte(byte b){
        byteToSend=b;
    }
    public void OpenInstruction(){
        File file = new File(FullscreenActivity.pathSDcard+'/'+FullscreenActivity.SettingFolder +"/"+ "instruction.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    class Main extends AsyncTask<Void,byte[],Void> {
        FTDriver ftDriver;
        boolean isOpen=false;
        int index=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ftDriver=new FTDriver((UsbManager)getSystemService(Context.USB_SERVICE));
            isOpen=ftDriver.begin(FTDriver.BAUD9600,setting.sizeOfBuffer.value);
            onListItemClick(index);
        }
        @Override
        protected Void doInBackground(Void... params) {
          

           /* byte[] signal=new byte[64];
            isOpen=true;
           byte[][] test=new byte[][]{
                    {50,0,1,1,1,0,1,0,0,3,21,4,1,1,1},
                    {50,2,0,0,0,0,0,0,0,0,21,4,1,1,1},
                    {50,3,2,6,1,1,0,1,1,4,21,4,1,1,1},
                    {50,1,3,2,3,2,2,1,0,2,21,5,1,1,1},
                   {50,1,0,0,0,0,0,0,0,0,21,1,1,1,1},
                    {50,3,4,5,1,3,1,2,0,4,21,1,1,1,1},
                    {50,3,5,3,1,4,3,2,1,6,21,1,1,1,1},
                   {50,1,0,0,0,0,0,0,0,0,21,1,1,1,1},
                    {50,4,6,3,6,5,2,1,0,4,21,1,1,1,1},
                    {50,4,7,4,1,6,1,0,0,3,21,1,1,1,1},
                    {50,5,0,5,0,7,0,3,1,0,21,1,1,1,1}};
            int index=0;
            while(true){
                if (isCancelled()) return null;
                publishProgress(test[index++%test.length]);
                try{
                TimeUnit.SECONDS.sleep(2);
                 }catch(InterruptedException e){}
                 }*/

            while(true){
                System.gc();
                if (isCancelled()) {
                    ftDriver.end();
                    return null;
                }
                
                byte[] buf = new byte[setting.sizeOfBuffer.value];
                
                if(byteToSend!=0&&isOpen){
                    byte[] bufS=new byte[setting.sizeOfBuffer.value];
                    bufS[PosSetStation]=byteToSend;
                    ftDriver.write(bufS,setting.sizeOfBuffer.value);
                    byteToSend=0;
                }
                
                if(isOpen) {
                    ftDriver.readInMy(buf);
                    //isOpen=ftDriver.isConnection();
                }
                else
                {
                    isOpen=ftDriver.begin(FTDriver.BAUD9600,setting.sizeOfBuffer.value);
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
                if(b[0]==FullscreenActivity.ValNormal)
                    Exit();
                if(b[0]==50)
                    Handler(b[11]);

                updateView();
                //viewMassage(b);

            }
            else
            {
                name.setText("Нет сигнала");
                value.setText("-");
                Toast.makeText(context,"Нет сигнала",Toast.LENGTH_SHORT);
            }

        }
        void Handler(byte b){
            if(index<=0)index+=listView.getCount();
            switch (b){
                case 1:onListItemClick(++index%listView.getCount());break;
                case 2:onListItemClick(--index%listView.getCount());break;
                case 3:curItem.onClick(SettingItem.Key.right);break;
                case 4:curItem.onClick(SettingItem.Key.left);break;
                case 5:curItem.onClick(SettingItem.Key.ok);break;
            }


        }

        Vector<String> sBuf=new Vector<>();
        void viewMassage(byte[] b){
            boolean isMassage=false;
            StringBuilder s=new StringBuilder();
            for(int i=0;i<15;i++){
                if(b[i]!=0)
                    isMassage=true;
                if(i==0||i==11)
                    s.append(String.valueOf(b[i]));
                if(i<14)s.append(",");
            }
            if(isMassage)
                sBuf.add(s.toString());

            StringBuilder vS=new StringBuilder();
            for(int i=sBuf.size()-1;i>=0&&i>sBuf.size()-10;i--)
            {
                vS.append(sBuf.elementAt(i)+'\n');
            }
            name.setText(vS.toString());
        }
    }
}
