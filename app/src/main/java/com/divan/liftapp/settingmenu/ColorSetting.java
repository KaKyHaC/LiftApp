package com.divan.liftapp.settingmenu;

/**
 * Created by Димка on 06.01.2017.
 */
import android.graphics.Color;

import java.util.Vector;

public class ColorSetting extends SettingItem {
    String Name;
    int color;
    private int curIndex,bufIndex;
    private int alpha;

    public ColorSetting(String name, int color) {
        Name = name;
        this.color = color;
        AddMyColorToMap();
    }
    public ColorSetting(String name,String hexColor){
        Name = name;
        this.color =(int) Long.parseLong(hexColor,16);
        AddMyColorToMap();
    }

    private void AddMyColorToMap(){
        alpha=Color.alpha(color);
        int maxAlpha=Color.alpha(Color.RED);
        color=Color.argb(maxAlpha,Color.red(color),Color.green(color),Color.blue(color));
       Vector<ColorPair> map=ColorsMap.getInstance().colors;
        boolean hasColor=false,hasName=false;
        for(int i=0;i<map.size();i++){
            if(map.elementAt(i).value==color)
                hasColor=true;
            if(map.elementAt(i).Name==this.Name)
                hasName=true;
        }
        if(!hasColor&&!hasName){
            map.add(new ColorPair(Name,color));
        }
        curIndex=getMyIndex();
    }
    private int getMyIndex(){
        Vector<ColorPair> v=ColorsMap.getColors();
        for(int i=0;i<v.size();i++){
            ColorPair buf=v.elementAt(i);
            if(buf.value==this.color)
                return i;
        }
        return -1;
    }
    @Override
    public String toString() {
        return Integer.toHexString(Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color)));
    }
    private void setColor(ColorPair p){
        this.color=p.value;
    }
    public void setColor(String hexColor){
        this.color =(int) Long.parseLong(hexColor,16);
    }
    @Override
    public void onClick(Key key) {
        Vector<ColorPair> v=ColorsMap.getColors();
        if(hasFocus) {
            switch (key) {
                case up:
                    curIndex++;bufIndex=curIndex;
                    break;
                case down:
                    curIndex--;bufIndex=curIndex;
                    break;
                case left:bufIndex--;
                    break;
                case right:bufIndex++;
                    break;
                case ok:curIndex=bufIndex;
                    break;
            }
        }
        while (curIndex<=0) {
            curIndex += v.size();
        }
        while (bufIndex<=0) {
            bufIndex += v.size();
        }
        setColor(v.elementAt(bufIndex%v.size()));
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getValue() {
        Vector<ColorPair> v=ColorsMap.getColors();
        while (bufIndex<0)curIndex+=v.size();
        return v.elementAt(bufIndex%v.size()).Name;
    }

    @Override
    public String getColor() {
        return  Integer.toHexString(Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color)));
    }

    @Override
    public void setFocus(boolean isFocus) {
        hasFocus=isFocus;
        bufIndex=curIndex;
        if(!isFocus)
        {
            onClick(Key.ok);
        }

    }
}


