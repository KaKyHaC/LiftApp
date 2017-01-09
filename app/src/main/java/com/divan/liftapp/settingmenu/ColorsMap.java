package com.divan.liftapp.settingmenu;

import android.graphics.Color;

import java.util.Vector;

/**
 * Created by Димка on 06.01.2017.
 */
public class ColorsMap {
    private static ColorsMap ourInstance = new ColorsMap();

    public static ColorsMap getInstance() {
        return ourInstance;
    }

    public Vector<ColorPair> colors = new Vector<>();

    private ColorsMap() {
        colors.add(new ColorPair("Red", Color.RED));
        colors.add(new ColorPair("Green",Color.GREEN));
        colors.add(new ColorPair("Blue",Color.BLUE));
        colors.add(new ColorPair("Black",Color.BLACK));
        colors.add(new ColorPair("White",Color.WHITE));
    }
    public static Vector<ColorPair> getColors(){
         return  getInstance().colors;
    }
}

class ColorPair{
   public ColorPair(String name, int value) {
       Name = name;
       this.value = value;
   }


   public String Name;
   public int value;
}