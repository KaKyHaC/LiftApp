package com.example.universalliftappsetting.settingmenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Димка on 28.01.2017.
 */

public class DateSetting extends SettingItem {
    public enum TypeDate{year,month,day,hour,min};
    TypeDate curType;
    String Name;
    static Calendar curDate,resDate;
    static Date systemDate;
    public static Long deltaTime=new Long(0);
    //public Long deltaTime;
    //public static long dYear=0,dMonth=0,dDay=0,dHour=0,dMinute=0;
    public DateSetting(String name, TypeDate curType) {
        Name = name;
        this.curType = curType;
        //this.deltaTime=new Long(0);
    }

    @Override
    public void onClick(Key key) {
        int month=curDate.get(Calendar.MONTH);
        int day=curDate.get(Calendar.DAY_OF_MONTH);
        int hour=curDate.get(Calendar.HOUR_OF_DAY);
        int min=curDate.get(Calendar.MINUTE);

        if(key==Key.left) {
            switch (curType) {
                case year:
                    curDate.add(Calendar.YEAR, -1);
                    break;
                case month:
                   // if(month!=Calendar.JANUARY)
                         curDate.add(Calendar.MONTH, -1);
                    break;
                case day:
                    int minDay=curDate.getMinimum(Calendar.DAY_OF_MONTH);
                    //if(day!=minDay)
                        curDate.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case hour:
                    int minHour=curDate.getMinimum(Calendar.HOUR_OF_DAY);
                    //if(hour!=minHour)
                        curDate.add(Calendar.HOUR_OF_DAY, -1);
                    break;
                case min:
                    int minMinute=curDate.getMinimum(Calendar.MINUTE);
                    //if(min!=minMinute)
                        curDate.add(Calendar.MINUTE, -1);
                    break;
            }
        } else if(key==Key.right){
            switch (curType) {
                case year:
                    curDate.add(Calendar.YEAR, 1);
                    break;
                case month:
                    //if(month!=Calendar.DECEMBER)
                        curDate.add(Calendar.MONTH, 1);
                    break;
                case day:
                    //if(day!=curDate.getMaximum(Calendar.DAY_OF_MONTH))
                        curDate.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case hour:
                    //if(hour!=curDate.getMaximum(Calendar.HOUR_OF_DAY))
                        curDate.add(Calendar.HOUR_OF_DAY, 1);
                    break;
                case min:
                    //if(min!=curDate.getMaximum(Calendar.MINUTE))
                        curDate.add(Calendar.MINUTE, 1);
                    break;
            }
        } else if(key==Key.ok){
            resDate=curDate;
            deltaTime=resDate.getTimeInMillis()-systemDate.getTime();
        }

        else if (key==Key.up){
            onClick(Key.right);
            onClick(Key.ok);
        }else if (key==Key.down){
            onClick(Key.left);
            onClick(Key.ok);
        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getValue() {
        Date bufDate=new Date(curDate.getTime().getTime());
        String format="";


        switch(curType){
            case year:format="yyyy";
                break;
            case month:format="MM";
                break;
            case day:format="dd";
                break;
            case hour:format="HH";
                break;
            case min:format="mm";
                break;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault()); //"dd:MM:yyyy EEEE HH:mm"
        final String strDate = simpleDateFormat.format(curDate.getTime());
        return strDate;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public void setFocus(boolean isFocus) {
        if(isFocus) {
           curDate=resDate;
        }

    }
    public static void StopTime(){
        systemDate = Calendar.getInstance().getTime();
        curDate = Calendar.getInstance();
        resDate=Calendar.getInstance();
    }

    @Override
    public String toString() {
        return deltaTime.toString();
    }

    @Override
    public void setValue(String value) {
        deltaTime=Long.parseLong(value);
    }
}


