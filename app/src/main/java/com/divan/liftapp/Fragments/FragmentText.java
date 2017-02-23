package com.divan.liftapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divan.liftapp.R;

import static com.divan.liftapp.FullscreenActivity.SettingFolder;
import static com.divan.liftapp.FullscreenActivity.settingFile;


public class FragmentText extends MyFragment {

    TextView tv;
    int lastSignal=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_text, null);
        tv=(TextView)v.findViewById(R.id.textFragment);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUpdate(0,lastSignal);
    }

    public void SetText(String text){
        if(tv!=null){
            tv.setText(text);
            tv.setTextSize(20);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Setting setting=new Setting(SettingFolder,settingFile);
        tv.setTextColor((int)Long.parseLong(setting.colorTextFragment.toString(),16));
        tv.setTextSize(setting.sizeTextFragment.value);
    }

    @Override
    public void onUpdate(int floor, int signal) {
        lastSignal=signal;
        if (tv != null) {
            if (signal == 1) {
                tv.setText("Пожар");
            }
            else if (signal == 2) {
                tv.setText("Перегруз");
            }
            else if(signal==3){
                tv.setText("Нет связи со станцией");
            }
            else if(signal==4){
                tv.setText("Нет связи с контроллером");
            }
        }
    }
}
