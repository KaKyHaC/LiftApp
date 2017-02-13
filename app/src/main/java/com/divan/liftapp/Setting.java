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
    private final String LOG_TAG="LiftApp";
    private String folderSetting,settingFile;
    public String pathSDcard=null;
    private File StoragePath;
    public String MainPath,BackGroundFolder,ImageFolder,SoundFolder,MusicFolder,MassageFolder,InformationFolder,ResourcesFolder,SpecialSoundFolder;
    public String typeDate="dd/MM/yyyy EEEE HH:mm";

    public ColorSetting textColorHex;//Integer.toHexString(Color.WHITE);
    public ColorSetting LayOutBackGraundColor;
    public NumberedSetting TextInfoSize,TextDateSize,TextMassageSize;//30
    public NumberedSetting NumberSize;//230
    public ColorSetting textFragmentColor;
    public NumberedSetting textFragmenSize;
    public NumberedSetting volumeDay;
    public NumberedSetting volumeNight;
    public ColorSetting iconColor;
    public int indexCurStation;
    public NumberedSetting sizeOfBuffer;
    public AccessSetting accessVideo;
    public AccessSetting accessMusic;

    public NumberedSetting sizeTextSetting;

    public NumberedSetting indexBAUDRATE;

    public DateSetting year,month,day,hour,min;



    public void InitDefault(){
            textColorHex = new ColorSetting("Цвет текста", Color.RED);
            LayOutBackGraundColor = new ColorSetting("Цвет подложки", Integer.parseInt("5300ff00",16));// Integer.parseInt("534056ff",16)
            TextInfoSize = new NumberedSetting(30, "Размер шрифта информации");
            TextDateSize = new NumberedSetting(15, "Размер шрифта времени");
            TextMassageSize = new NumberedSetting(30, "Размер шрифта сообщеня");
            NumberSize = new NumberedSetting(230, "Размер шрифта этажа");

        textFragmentColor = new ColorSetting("Цвет текста фрагмента",Color.RED);
        textFragmenSize=new NumberedSetting(100,"Размер шрифта фрагмента");

        volumeDay=new NumberedSetting(100,"Громкость днем", NumberedSetting.NumberedType.Volume);
        volumeNight=new NumberedSetting(50,"Громкость ночью", NumberedSetting.NumberedType.Volume);

        iconColor=new ColorSetting("Цвет иконок",Color.YELLOW);

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
    public Setting(String folderSetting, String settingFile) {
        this.folderSetting=folderSetting;
        this.settingFile=settingFile;
        setStoragePath();
        if(NumberSize==null)
            InitDefault();
        StartRead();
        MainPath=folderSetting;
    }

    public void WriteSetting(){
        try{
           File sdPath=StoragePath;
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/" + folderSetting);
            // создаем каталог
            sdPath.mkdirs();
            // формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, settingFile);

            FileWriter fw=new FileWriter(sdFile);

            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(folderSetting+"--  mainPath\n");
            bw.write("BackGround"+"--  BackGround Folder\n");
            bw.write("Image"+"--  Image Folder\n");
            bw.write("Sound"+"--  Sound Folder\n");
            bw.write("Music"+"--  Music Folder\n");
            bw.write("Massage"+"--  Massage Folder\n");
            bw.write("Information"+"--  Information Folder\n");
            bw.write("Resources"+"--  Resources Folder\n");
            bw.write("SpecialSound"+"--  Special Sound Folder\n");
            bw.write(LayOutBackGraundColor+"--  LayOut BackGraund Color\n");
            bw.write(textColorHex+"-- цвет текста (Text Color)\n");
            bw.write(TextInfoSize+"--  Текст Information Size\n");
            bw.write(TextDateSize+"--  Text Date Size\n");
            bw.write(TextMassageSize+"--  Text Massage Size\n");
            bw.write(NumberSize+"--  Number Size\n");
            bw.write(typeDate+"--  type Date\n");
            bw.write(textFragmentColor+"--  text Fragment Color\n");
            bw.write(textFragmenSize+"--  text Fragment Color\n");
            bw.write(volumeDay+"--  volume Day\n");
            bw.write(volumeNight+"--  volume Night\n");
            bw.write(iconColor+"--  icon Color\n");
            bw.write(indexCurStation+"--  indexCurStation\n");
            bw.write(sizeOfBuffer+"--  size Of Buffer\n");
            bw.write(accessVideo+"--  accessVideo\n");
            bw.write(accessMusic+"--  accessMusic\n");

            bw.write(sizeTextSetting+"--  sizeTextSetting\n");
            bw.write(indexBAUDRATE+"--  indexBAUDRATE\n");

            bw.write(DateSetting.deltaTime+"--  deltaTime\n");


            bw.close();
        }catch (IOException r){

        }
    }
    private void ReadSettings(BufferedReader br){
        try {
            MainPath = getStringBeforCommends(br.readLine());
            BackGroundFolder= getStringBeforCommends(br.readLine());
            ImageFolder= getStringBeforCommends(br.readLine());
            SoundFolder= getStringBeforCommends(br.readLine());
            MusicFolder= getStringBeforCommends(br.readLine());
            MassageFolder= getStringBeforCommends(br.readLine());
            InformationFolder= getStringBeforCommends(br.readLine());
            ResourcesFolder= getStringBeforCommends(br.readLine());
            SpecialSoundFolder= getStringBeforCommends(br.readLine());
            LayOutBackGraundColor.setColor(getStringBeforCommends(br.readLine()));
            textColorHex.setColor(getStringBeforCommends(br.readLine()));
            TextInfoSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            TextDateSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            TextMassageSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            NumberSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            typeDate= getStringBeforCommends(br.readLine());
            textFragmentColor.setColor(getStringBeforCommends(br.readLine()));
            textFragmenSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            volumeDay.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            volumeNight.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            iconColor.setColor(getStringBeforCommends(br.readLine()));
            indexCurStation=Integer.parseInt(getStringBeforCommends(br.readLine()));
            sizeOfBuffer.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            accessVideo.Access=Boolean.parseBoolean(getStringBeforCommends(br.readLine()));
            accessMusic.Access=Boolean.parseBoolean(getStringBeforCommends(br.readLine()));

            sizeTextSetting.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            indexBAUDRATE.value=Integer.parseInt(getStringBeforCommends(br.readLine()));

            DateSetting.deltaTime=Long.parseLong(getStringBeforCommends(br.readLine()));
            /*month.deltaTime=Long.parseLong(getStringBeforCommends(br.readLine()));
            day.deltaTime=Long.parseLong(getStringBeforCommends(br.readLine()));
            hour.deltaTime=Long.parseLong(getStringBeforCommends(br.readLine()));
            min.deltaTime=Long.parseLong(getStringBeforCommends(br.readLine()));*/


        }catch (IOException r)
        {

        }
    }
    public void StartRead() {
       File sdPath=StoragePath;
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folderSetting);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, settingFile);
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

    private void CreateFolder(File sdPath)
    {
        new File(sdPath.getAbsolutePath()+'/'+BackGroundFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+ImageFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+SoundFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+MusicFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+MassageFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+InformationFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+ResourcesFolder).mkdir();
        new File(sdPath.getAbsolutePath()+'/'+SpecialSoundFolder).mkdir();
    }

    private String getStringBeforCommends(String s) {
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
        File sdPath= StoragePath;
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folderSetting);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, settingFile);
        return Uri.fromFile(sdFile);
    }
    private void setStoragePath(){
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
        pathSDcard=sdPath.getAbsolutePath();
        StoragePath=sdPath;
    }
}
