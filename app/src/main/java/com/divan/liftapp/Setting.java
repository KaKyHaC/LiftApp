package com.divan.liftapp;

import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import com.divan.liftapp.settingmenu.AccessSetting;
import com.divan.liftapp.settingmenu.ColorSetting;
import com.divan.liftapp.settingmenu.DateSetting;
import com.divan.liftapp.settingmenu.NumberedSetting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Димка on 03.12.2016.
 */

public class Setting {
    public String folderLiftApp,fileSetting;
    public String pathLiftFolder,pathSDcard=null;
    public String folderBackGraund,folderImage,folderSound,folderMusic,folderMassage,folderInformation,folderVideo,folderSpecialSound;
    public String typeDate;

    public ColorSetting colorText;//Integer.toHexString(Color.WHITE);
    public ColorSetting colorLayoutBackgraund;
    public NumberedSetting sizeTextInfo,sizeTextDate,sizeTextMassage;//30
    public NumberedSetting sizeNumber;//230
    public ColorSetting colorTextFragment;
    public NumberedSetting sizeTextFragment;
    public NumberedSetting volumeDay;
    public NumberedSetting volumeNight;
    public ColorSetting colorIcon;
    public int indexCurStation;
    public NumberedSetting sizeOfBuffer;
    public AccessSetting accessVideo;
    public AccessSetting accessMusic;
    public NumberedSetting sizeTextSetting;
    public NumberedSetting indexBAUDRATE;
    public DateSetting year,month,day,hour,min;



    public void InitDefault(){
        folderImage="Image";
        folderSpecialSound="SpecialSound";
        folderBackGraund="BackGraund";
        folderVideo="Video";
        folderInformation="Information";
        folderMassage="Massage";
        folderMusic="Music";
        folderSound="Sound";
        
        
        typeDate="dd/MM/yyyy EEEE HH:mm";
            colorText = new ColorSetting("Цвет текста", Color.RED);
            colorLayoutBackgraund = new ColorSetting("Цвет подложки", Integer.parseInt("5300ff00",16));// Integer.parseInt("534056ff",16)
            sizeTextInfo = new NumberedSetting(30, "Размер шрифта информации");
            sizeTextDate = new NumberedSetting(15, "Размер шрифта времени");
            sizeTextMassage = new NumberedSetting(30, "Размер шрифта сообщеня");
            sizeNumber = new NumberedSetting(230, "Размер шрифта этажа");

        colorTextFragment = new ColorSetting("Цвет текста фрагмента",Color.RED);
        sizeTextFragment=new NumberedSetting(100,"Размер шрифта фрагмента");

        volumeDay=new NumberedSetting(100,"Громкость днем", NumberedSetting.NumberedType.Volume);
        volumeNight=new NumberedSetting(50,"Громкость ночью", NumberedSetting.NumberedType.Volume);

        colorIcon=new ColorSetting("Цвет иконок",Color.YELLOW);

        sizeOfBuffer=new NumberedSetting(64,"Размер буффера", NumberedSetting.NumberedType.Buffer);

        indexCurStation=0;

        accessVideo=new AccessSetting("Воспроизведение видео",AccessSetting.typeAccess[0]);
        accessMusic=new AccessSetting("Воспроизведение музыки",AccessSetting.typeAccess[0]);

        sizeTextSetting=new NumberedSetting(15,"Размер шрифта настроек", NumberedSetting.NumberedType.Text);

        indexBAUDRATE=new NumberedSetting(0,"Частота", NumberedSetting.NumberedType.BaudRate);

        year=new DateSetting("Год", DateSetting.TypeDate.year);
        month=new DateSetting("Месяц", DateSetting.TypeDate.month);
        day=new DateSetting("День", DateSetting.TypeDate.day);
        hour=new DateSetting("Час", DateSetting.TypeDate.hour);
        min=new DateSetting("Минуты", DateSetting.TypeDate.min);

        DateSetting.deltaTime=new Long(0);


    }
    public Setting(String folderLiftApp, String fileSetting) {
        this.folderLiftApp=folderLiftApp;
        this.fileSetting=fileSetting;
        pathSDcard=setStoragePath();
        pathLiftFolder=pathSDcard+ "/" + folderLiftApp;

//        if(sizeNumber==null)
            InitDefault();
        StartRead();
    }

    public void WriteSetting(){
        try{
           File sdPath=new File(pathSDcard);
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/" + folderLiftApp);
            // создаем каталог
            sdPath.mkdirs();
            // формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, fileSetting);

            FileWriter fw=new FileWriter(sdFile);

            BufferedWriter bw=new BufferedWriter(fw);
           
            WriteSetting(bw);

            bw.close();
        }catch (IOException r){

        }
    }
    private void WriteSetting(BufferedWriter bw) {
        try {
            bw.write("\n\n@@ folders @@");
            bw.write("\nfolderLiftApp=" + folderLiftApp);
            bw.write("\nfolderBackGraund=" + folderBackGraund);
            bw.write("\nfolderImage=" + folderImage);
            bw.write("\nfolderSound=" + folderSound);
            bw.write("\nfolderMusic=" + folderMusic);
            bw.write("\nfolderMassage=" + folderMassage);
            bw.write("\nfolderInformation=" + folderInformation);
            bw.write("\nfolderVideo=" + folderVideo);
            bw.write("\nfolderSpecialSound=" + folderSpecialSound);
            
            bw.write("\n\n@@ colors @@");
            bw.write("\ncolorLayoutBackgraund=" + colorLayoutBackgraund);
            bw.write("\ncolorText="+colorText);
            bw.write("\ncolorIcon="+colorIcon);
            bw.write("\ncolorTextFragment="+colorTextFragment);

            bw.write("\n\n@@ size @@");
            bw.write("\nsizeNumber="+sizeNumber);
            bw.write("\nsizeOfBuffer="+sizeOfBuffer);
            bw.write("\nsizeTextDate="+sizeTextDate);
            bw.write("\nsizeTextFragment="+sizeTextFragment);
            bw.write("\nsizeTextInfo="+sizeTextInfo);
            bw.write("\nsizeTextMassage="+sizeTextMassage);
            bw.write("\nsizeTextSetting="+sizeTextSetting);

            bw.write("\n\n@@ special @@");
            bw.write("\ntypeDate="+typeDate);
            bw.write("\nvolumeDay="+volumeDay);
            bw.write("\nvolumeNight="+volumeNight);
            bw.write("\nindexCurStation="+indexCurStation);
            bw.write("\naccessVideo="+accessVideo);
            bw.write("\naccessMusic="+accessMusic);
            bw.write("\nindexBAUDRATE="+indexBAUDRATE);
            bw.write("\ndeltaTime="+DateSetting.deltaTime);
        }catch (IOException e){}
    }
    //TODO remake reading function
    private void ReadSettings(BufferedReader br){
        try {
            folderLiftApp = getValueInString(br.readLine());
            folderBackGraund= getValueInString(br.readLine());
            folderImage= getValueInString(br.readLine());
            folderSound= getValueInString(br.readLine());
            folderMusic= getValueInString(br.readLine());
            folderMassage= getValueInString(br.readLine());
            folderInformation= getValueInString(br.readLine());
            folderVideo= getValueInString(br.readLine());
            folderSpecialSound= getValueInString(br.readLine());
            colorLayoutBackgraund.setColor(getValueInString(br.readLine()));
            colorText.setColor(getValueInString(br.readLine()));
            sizeTextInfo.value=Integer.parseInt(getValueInString(br.readLine()));
            sizeTextDate.value=Integer.parseInt(getValueInString(br.readLine()));
            sizeTextMassage.value=Integer.parseInt(getValueInString(br.readLine()));
            sizeNumber.value=Integer.parseInt(getValueInString(br.readLine()));
            typeDate= getValueInString(br.readLine());
            colorTextFragment.setColor(getValueInString(br.readLine()));
            sizeTextFragment.value=Integer.parseInt(getValueInString(br.readLine()));
            volumeDay.value=Integer.parseInt(getValueInString(br.readLine()));
            volumeNight.value=Integer.parseInt(getValueInString(br.readLine()));
            colorIcon.setColor(getValueInString(br.readLine()));
            indexCurStation=Integer.parseInt(getValueInString(br.readLine()));
            sizeOfBuffer.value=Integer.parseInt(getValueInString(br.readLine()));
            accessVideo.Access=Boolean.parseBoolean(getValueInString(br.readLine()));
            accessMusic.Access=Boolean.parseBoolean(getValueInString(br.readLine()));

            sizeTextSetting.value=Integer.parseInt(getValueInString(br.readLine()));
            indexBAUDRATE.value=Integer.parseInt(getValueInString(br.readLine()));

            DateSetting.deltaTime=Long.parseLong(getValueInString(br.readLine()));
            /*month.deltaTime=Long.parseLong(getValueInString(br.readLine()));
            day.deltaTime=Long.parseLong(getValueInString(br.readLine()));
            hour.deltaTime=Long.parseLong(getValueInString(br.readLine()));
            min.deltaTime=Long.parseLong(getValueInString(br.readLine()));*/


        }catch (IOException r)
        {

        }
    }
    public void StartRead() {
       File sdPath=new File(pathSDcard);
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folderLiftApp);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fileSetting);
        try{
            if(!sdFile.canRead()||sdFile.getUsableSpace()<100)
            {
                WriteSetting();
            }
            FileReader reader=new FileReader(sdFile);
            if(reader!=null) {
                BufferedReader br = new BufferedReader(reader);
                ReadSettings(br);
                br.close();
                if(reader!=null)
                reader.close();
            }
            CreateFolder(sdPath);
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    private void CreateFolder(File sdPath) {
        new File(sdPath.getAbsolutePath()+'/'+folderBackGraund).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderImage).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderSound).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderMusic).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderMassage).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderInformation).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderVideo).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+folderSpecialSound).mkdir();
    }

    private String confirmString="Setting 2.0";
    private void confirmSetting(BufferedWriter bw){
        try{
            bw.write(confirmString+'\n');
        }catch (IOException e){}
    }
    private boolean confirmSetting(BufferedReader br){
        try{
            String ac=br.readLine();
            return ac.equals(confirmString);
        }catch (IOException e){}
    }

    private String getValueInString(String s) {
        //TODO remake for '='
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<s.length();i++) {
            char cur=s.charAt(i);
            if(!(cur=='-'&&s.charAt(i+1)=='-'))
                sb.append(cur);
            else
                break;
        }
        return sb.toString();
    }

    public Uri getUri(){
        File sdPath= new File(pathSDcard);
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folderLiftApp);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fileSetting);
        return Uri.fromFile(sdFile);
    }
    private String setStoragePath(){
        File sdPath= Environment.getExternalStorageDirectory();
        File parent=sdPath.getParentFile();
        if(parent!=null) {
            File[] files = parent.listFiles();
            if(files!=null) {
                for (File f : sdPath.getParentFile().listFiles()) {
                    String n = f.getName();
                    if (n.equals("extsd"))
                        sdPath = f;
                }
            }
        }
        return sdPath.getAbsolutePath();
    }
}
