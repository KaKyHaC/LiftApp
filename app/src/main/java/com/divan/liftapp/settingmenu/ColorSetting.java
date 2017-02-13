package com.divan.liftapp.settingmenu;

/**
 * Created by Димка on 06.01.2017.
 */
import android.graphics.Color;

import java.util.Vector;

public class ColorSetting extends SettingItem {
    String Name;
    private int color;
    private int curIndex,bufIndex;
    private int alpha;

    public ColorSetting(String name, int color) {
        Name = name;
        this.color = color;
        alpha=Color.alpha(color);
        bufIndex=curIndex=ColorsMap.getColorIndex(name,color);
    }
    public ColorSetting(String name,String hexColor){
        Name = name;
        this.color =(int) Long.parseLong(hexColor,16);
        alpha=Color.alpha(color);
        bufIndex=curIndex=ColorsMap.getColorIndex(name,color);
    }

    public void setColor(String hexColor){
        color=(int)Long.parseLong(hexColor,16);
        curIndex=bufIndex=ColorsMap.getColorIndex(Name,color);
    }



    @Override
    public String toString() {
        return Integer.toHexString(color);
    }
   /* private void setColor(ColorPair p){
        this.color=p.value;
    }*/
    /*public void setColor(String hexColor){
        this.color =(int) Long.parseLong(hexColor,16);
    }*/
    @Override
    public void onClick(Key key) {
      //  Vector<ColorPair> v=ColorsMap.getColors();
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
            curIndex += ColorsMap.size();
        }
        while (bufIndex<=0) {
            bufIndex += ColorsMap.size();
        }
        color=ColorsMap.makeARGB(alpha,ColorsMap.colorAt(curIndex));
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getValue() {

        return ColorsMap.nameAt(bufIndex);
    }

    @Override
    public String getColor() {
//        return ColorsMap.getArgbHexColor(alpha,bufIndex);
        return Integer.toHexString(Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color)));
    }

    @Override
    public void setFocus(boolean isFocus) {
        hasFocus=isFocus;
        if(isFocus)
            bufIndex=curIndex;

    }
}


