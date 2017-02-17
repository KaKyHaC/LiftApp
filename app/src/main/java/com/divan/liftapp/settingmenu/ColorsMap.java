package com.divan.liftapp.settingmenu;

import android.graphics.Color;

import java.util.Vector;

/**
 * Created by Димка on 06.01.2017.
 */
public class ColorsMap {
    private static ColorsMap ourInstance = new ColorsMap();

   /* public static ColorsMap getInstance() {
        return ourInstance;
    }*/

    private static Vector<ColorPair> colors ;

    private ColorsMap() {
        colors=new Vector<>();
        addDefaultColors();
    }
    private static void addDefaultColors(){
        colors.add(new ColorPair("Red", Color.RED));
        colors.add(new ColorPair("Green",Color.GREEN));
        colors.add(new ColorPair("Blue",Color.BLUE));
        colors.add(new ColorPair("Black",Color.BLACK));
        colors.add(new ColorPair("White",Color.WHITE));
        colors.add(new ColorPair("Yellow",Color.YELLOW));
    }
    /*public static Vector<ColorPair> getColors(){
         return  getInstance().colors;
    }*/

    public static void addMyColorToMap(String name,int color){
        color=makeRGB(color);
        if(!isHaveColor(color))
        {
            colors.add(new ColorPair(name,color));
        }
    }
    private static boolean isHaveColor(int color){
        color = makeRGB(color);
        for(ColorPair cp:colors){
            if(cp.value==color)
                return true;
        }
        return false;
    }

    public static int colorAt(int index){
        return colors.elementAt(index%size()).value;
    }
    public static String nameAt(int index){return colors.elementAt(index%size()).Name;}
    public static int size(){return colors.size();}
    public static int getColorIndex(String Name,int color){
        color=makeRGB(color);
        if(!isHaveColor(color))
            addMyColorToMap(Name,color);

        for(int i=0;i<colors.size();i++){
            if(colors.elementAt(i).value==color)
                return i;
        }
        return -1;
    }

    public static int makeRGB(int color){
        return Color.argb(Color.alpha(Color.BLACK),Color.red(color),Color.green(color),Color.blue(color));
    }
    public static int makeARGB(int alpha,int color){
        return Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
    }

    public static String getArgbHexColor(int alpha,int index){
        int c=colorAt(index);
        c=makeARGB(alpha,c);
        return Integer.toHexString(c);
    }
    public static int getArgbColorAt(int alpha,int index){
        int c=colorAt(index);
        return makeARGB(alpha,c);
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