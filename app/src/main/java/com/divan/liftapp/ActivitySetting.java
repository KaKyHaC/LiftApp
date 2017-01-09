package com.divan.liftapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import static com.divan.liftapp.FullscreenActivity.BAUDRATE;
import static com.divan.liftapp.FullscreenActivity.SIZEOFMASSAGE;

public class ActivitySetting extends AppCompatActivity {
    public static final int UiSetting= View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    final String SettingFolder="LiftApp",settingFile="setting.txt",GONG="gong.wav";
    Vector<SettingItem> settingItems;
    Setting setting=new Setting(SettingFolder,settingFile);

    ListView listView;
    TextView name,value;
    int indexSelected=-1;
    boolean isSelect=false;
    View selected;

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

        context=this;
        StartAsync();

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


                if(position==settingItems.size()) {
                    isSelect=false;
                }
                else {
                    indexSelected = position;
                    isSelect = true;
                    updateView();
                }
                selected=itemClicked;
                focusItem(itemClicked);
                //itemClicked.setBackgroundColor(Color.BLUE);

            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelect) {
                    settingItems.elementAt(indexSelected).onClick(SettingItem.Key.up);
                    updateView();
                }
                else{
                    setting.InitDefault();
                    InitSettingItems();
                    Toast.makeText(context,"make default",Toast.LENGTH_LONG);
                }
            }
        });
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelect) {
                    settingItems.elementAt(indexSelected).onClick(SettingItem.Key.down);
                    updateView();
                }
                else{
                    setting.InitDefault();
                    InitSettingItems();
                    Toast.makeText(context,"make default",Toast.LENGTH_LONG);
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                focusItem(selected);
            }
        });
    }
    private void focusItem(View view){
        for(int i=0;i<listView.getCount();i++)
        {
            View a= listView.getChildAt(i);
            if(a!=null) {
                a.setBackgroundColor(Color.WHITE);
                if(((TextView)a).getText()==((TextView)view).getText())
                    a.setBackgroundColor(Color.YELLOW);
            }
        }

    }
    private void updateView(){
        name.setText(settingItems.elementAt(indexSelected).getName());
        value.setText(settingItems.elementAt(indexSelected).getValue());
        String color=settingItems.elementAt(indexSelected).getColor();
        if(color!=null){
            value.setBackgroundColor((int) Long.parseLong(color,16));
        }
        else {
            value.setBackgroundColor(Color.WHITE);
        }
    }
    private void InitSettingItems(){
        settingItems=new Vector<>();
        settingItems.add(setting.LayOutBackGraundColor);
        settingItems.add(setting.textColorHex);
        settingItems.add(setting.textFragmentColor);
        settingItems.add(setting.NumberSize);
        settingItems.add(setting.TextDateSize);
        settingItems.add(setting.TextInfoSize);
        settingItems.add(setting.TextMassageSize);
        settingItems.add(setting.textFragmenSize);
        ;
    }
    private void FillList(){


// определяем массив типа String
        String[] catNames = new String[settingItems.size()+1];
        for(int i=0;i<settingItems.size();i++){
            catNames[i]=settingItems.elementAt(i).getName();
        }
        catNames[catNames.length-1]="default";


// используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, catNames);

        listView.setAdapter(adapter);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        setting.WriteSetting();
        if(isAsync)
            main.cancel(false);
    }

    class Main extends AsyncTask<Void,byte[],Void> {
        FTDriver ftDriver;
        boolean isOpen=false;
        int index=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ftDriver=new FTDriver((UsbManager)getSystemService(Context.USB_SERVICE));
            isOpen=ftDriver.begin(FTDriver.BAUD9600);
            onListItemClick(index);
        }
        @Override
        protected Void doInBackground(Void... params) {

       /*     byte[] signal=new byte[64];
            isOpen=true;
           byte[][] test=new byte[][]{
                    {0,0,1,1,1,0,1,0,0,3},
                    {0,2,0,0,0,0,0,0,0,0},
                    {0,3,2,6,1,1,0,1,1,4},
                    {0,1,3,2,3,2,2,1,0,2},
                   {0,1,0,0,0,0,0,0,0,0},
                    {0,3,4,5,1,3,1,2,0,4},
                    {0,3,5,3,1,4,3,2,1,6},
                   {0,1,0,0,0,0,0,0,0,0},
                    {0,4,6,3,6,5,2,1,0,4},
                    {0,4,7,4,1,6,1,0,0,3},
                    {0,5,0,5,0,7,0,3,1,0}};
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
                    return null;
                }
                byte[] buf = new byte[SIZEOFMASSAGE];
                //isOpen=ftDriver.isConnection();
                //isOpen=ftDriver.begin(FTDriver.BAUD9600);
                if(isOpen) {
                    ftDriver.read(buf);
                    //isOpen=ftDriver.isConnection();
                }
                else
                {
                    isOpen=ftDriver.begin(FTDriver.BAUD9600);
                }
                publishProgress(buf);
                try{
                    Thread.sleep(BAUDRATE/2);
                }catch (InterruptedException e){}
            }


        }


        @Override
        protected void onProgressUpdate(byte[]... values) {
            super.onProgressUpdate(values);
            byte [] b=values[0];
            if(isOpen) {
                Handler(b[11]);
            }
            else
            {
                name.setText("No connection");
                value.setText("-");
                Toast.makeText(context,"No connection",Toast.LENGTH_SHORT);
            }

        }
        void Handler(byte b){
            if(index<=0)index+=listView.getCount();
            switch (b){
                case 1:onListItemClick(++index%listView.getCount());break;
                case 2:onListItemClick(--index%listView.getCount());break;
                case 3:name.callOnClick();break;
                case 4:value.callOnClick();break;
                case 5:finish();
            }
        }

    }
}