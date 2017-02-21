package com.divan.liftapp;

import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import com.divan.liftapp.settingmenu.AccessSetting;
import com.divan.liftapp.settingmenu.ColorSetting;
import com.divan.liftapp.settingmenu.DateSetting;
import com.divan.liftapp.settingmenu.SettingItem;
import com.divan.liftapp.settingmenu.StringSetting;
import com.divan.liftapp.settingmenu.NumberedSetting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Димка on 03.12.2016.
 */

public class Setting {
    public String folderLiftApp,fileSetting;
    public String pathLiftFolder,pathSDcard=null;

    public StringSetting folderBackGraund,folderImage,folderSound,folderMusic,folderMassage,folderInformation,folderVideo,folderSpecialSound;
    public ColorSetting colorText, colorLayoutBackgraund,colorTextFragment,colorIcon;;
    public NumberedSetting sizeTextInfo,sizeTextDate,sizeTextMassage,sizeNumber,sizeTextFragment,sizeOfBuffer,sizeTextSetting;;//230
    public NumberedSetting volumeDay, volumeNight;
    public NumberedSetting indexBAUDRATE;
    public AccessSetting accessVideo,accessMusic;
    public DateSetting year,month,day,hour,min;//TODO universal date

    public NumberedSetting indexCurStation;
    public StringSetting typeDate;


    public void InitDefault(){
        folderImage=new StringSetting("Папка с Изображением","Image");
        folderSpecialSound=new StringSetting("Папка с Спец. Звуками","SpecialSound");
        folderBackGraund=new StringSetting("Папка с Фонами","BackGraund");
        folderVideo=new StringSetting("Папка с Видео","Video");
        folderInformation=new StringSetting("Папка с информацией","Information");
        folderMassage=new StringSetting("Папка с сообщениями","Massage");
        folderMusic=new StringSetting("Папка с музыкой","Music");
        folderSound=new StringSetting("Папка с звуками","Sound");
        
        
        typeDate=new StringSetting("формат даты","dd/MM/yyyy EEEE HH:mm");
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

        indexCurStation=new NumberedSetting(0,"индекс станции");

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
        new File(pathLiftFolder).mkdir();
//      if(sizeNumber==null)
        InitDefault();
        StartRead();
        CreateFolder(new File(pathLiftFolder));
    }

    public void WriteSetting(){
        try{
            File sdFile = new File(pathLiftFolder, fileSetting);
            FileWriter fw=new FileWriter(sdFile);
            BufferedWriter bw=new BufferedWriter(fw);
            confirmSetting(bw);
            WriteSetting(bw);
            bw.close();
            fw.close();
        }catch (IOException r){

        }
    }
    private void WriteSetting(BufferedWriter bw) {
        try {
            bw.write("\n\n@@ folders @@");
//            bw.write("\nfolderLiftApp=" + folderLiftApp);
            writeItem(folderVideo,bw);
            writeItem(folderSpecialSound,bw);
            writeItem(folderSound,bw);
            writeItem(folderMusic,bw);
            writeItem(folderBackGraund,bw);
            writeItem(folderImage,bw);
            writeItem(folderInformation,bw);
            writeItem(folderMassage,bw);
            
            bw.write("\n\n@@ colors @@");
            writeItem(colorTextFragment,bw);
            writeItem(colorText,bw);
            writeItem(colorLayoutBackgraund,bw);
            writeItem(colorIcon,bw);

            bw.write("\n\n@@ sizes @@");
            writeItem(sizeTextSetting,bw);
            writeItem(sizeTextMassage,bw);
            writeItem(sizeTextInfo,bw);
            writeItem(sizeTextFragment,bw);
            writeItem(sizeNumber,bw);
            writeItem(sizeOfBuffer,bw);
            writeItem(sizeTextDate,bw);

            bw.write("\n\n@@ special @@");
            writeItem(volumeNight,bw);
            writeItem(volumeDay,bw);
            writeItem(typeDate,bw);
            writeItem(indexCurStation,bw);
            writeItem(indexBAUDRATE,bw);
            writeItem(accessVideo,bw);
            writeItem(accessMusic,bw);
            writeItem(year,bw);

        }catch (IOException e){}
    }
    private void writeItem(SettingItem item,BufferedWriter bw){
        try {
            bw.write('\n' + item.getName() + '=' + item);
        }catch (IOException e){}
    }

    public void StartRead() {
        File settFile = new File(pathLiftFolder, fileSetting);
        Vector<String> strings=readAllFromFile(settFile);
        if(!confirmSetting(strings))
            WriteSetting();
        else{
            SetAllItemsValues(strings);
        }


    }
    private void SetAllItemsValues(Vector<String> strings){
        SetItemValue(folderBackGraund,strings);
        SetItemValue(folderImage,strings);
        SetItemValue(folderInformation,strings);
        SetItemValue(folderMassage,strings);
        SetItemValue(folderMusic,strings);
        SetItemValue(folderSound,strings);
        SetItemValue(folderSpecialSound,strings);
        SetItemValue(folderVideo,strings);

        SetItemValue(colorIcon,strings);
        SetItemValue(colorLayoutBackgraund,strings);
        SetItemValue(colorText,strings);
        SetItemValue(colorTextFragment,strings);

        SetItemValue(sizeNumber,strings);
        SetItemValue(sizeOfBuffer,strings);
        SetItemValue(sizeTextDate,strings);
        SetItemValue(sizeTextFragment,strings);
        SetItemValue(sizeTextInfo,strings);
        SetItemValue(sizeTextMassage,strings);
        SetItemValue(sizeTextSetting,strings);

        SetItemValue(volumeDay,strings);
        SetItemValue(volumeNight,strings);
        SetItemValue(accessMusic,strings);
        SetItemValue(accessVideo,strings);

        SetItemValue(indexCurStation,strings);
        SetItemValue(indexBAUDRATE,strings);
        SetItemValue(typeDate,strings);

        SetItemValue(year,strings);
    }

    private Vector<String> readAllFromFile(File file){
        Vector<String> strings=new Vector<>();
        try{
            FileReader reader=new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            strings=readAllFromThread(br);
            br.close();
            reader.close();
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return strings;
    }
    private Vector<String> readAllFromThread(BufferedReader br){
        Vector<String> v=new Vector<>();
        String s=new String();
        try {
            while ((s = br.readLine()) != null) {
                v.add(s);
            }
        }catch (IOException e){}
        return v;
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
            bw.write('\n'+confirmString+'\n');
        }catch (IOException e){}
    }
    private boolean confirmSetting(Vector<String> strings){
        for(String s:strings){
            if(s.equals(confirmString))
                return true;
        }
        return false;
    }

    private void SetItemValue(SettingItem item,Vector<String> strings){
        String val=findValueByName(item.getName(),strings);
        if(val!=null)
            item.setValue(val);
    }
    private String findValueByName(String name,Vector<String> strings){
        for(String s:strings){
            if(name.equals(getNameInString(s)))
                return getValueInString(s);
        }
        return null;
    }
    private String getNameInString(String s){
        StringBuilder sb=new StringBuilder();
        for(char c:s.toCharArray()){
            if(c=='=')return sb.toString();
            sb.append(c);
        }
        return null;
    }
    private String getValueInString(String s){
        StringBuilder sb=new StringBuilder();
        boolean isValue=false;

        for(char c:s.toCharArray()){
            if(isValue)sb.append(c);
            else if(c=='=')isValue=true;
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
