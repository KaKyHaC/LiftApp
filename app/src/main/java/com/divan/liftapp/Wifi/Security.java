package com.divan.liftapp.Wifi;

/**
 * Created by Dima on 07.02.2017.
 */

public class Security {
    public static String prefLift="Lift",prefSetting="Sett";
    public static String getPref(String name,int len){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len&&i<name.length();i++)
            sb.append(name.charAt(i));
        return sb.toString();
    }
    public static String appendPref(String name,String pref){
        return pref+name;
    }
    public static String setPref(String name,String pref){
        if(!hasPref(name,pref))
            return appendPref(name,pref);
        return name;
    }
    public static boolean hasPref(String name,String pref){
        for(int i=0;i<pref.length();i++){
            if(i>=name.length()){
                return false;
            }
            if(name.charAt(i)!=pref.charAt(i))
                return false;
        }
        return true;
    }
}
