package com.divan.liftapp;

import android.graphics.Color;
import android.os.Environment;

import com.divan.liftapp.settingmenu.ColorSetting;
import com.divan.liftapp.settingmenu.SizeSetting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Димка on 03.12.2016.
 */

public class Setting {
    private final String LOG_TAG="LiftApp";
    private String folderSetting,settingFile;
    public String MainPath,BackGroundFolder,ImageFolder,SoundFolder,MusicFolder,MassageFolder,InformationFolder,ResourcesFolder,SpecialSoundFolder;
    public String typeDate="dd:MM:yyyy EEEE HH:mm";

    public ColorSetting textColorHex;//Integer.toHexString(Color.WHITE);
    public ColorSetting LayOutBackGraundColor;
    public SizeSetting TextInfoSize,TextDateSize,TextMassageSize;//30
    public SizeSetting NumberSize;//230

    public ColorSetting textFragmentColor;
    public SizeSetting textFragmenSize;



    public void InitDefault(){
            textColorHex = new ColorSetting("Цвет текста", Color.WHITE);
            LayOutBackGraundColor = new ColorSetting("Цвет подложки", Integer.parseInt("534056ff",16));
            TextInfoSize = new SizeSetting(30, "Размер шрифта информации");
            TextDateSize = new SizeSetting(30, "Размер шрифта времени");
            TextMassageSize = new SizeSetting(30, "Размер шрифта сообщеня");
            NumberSize = new SizeSetting(230, "Размер шрифта этажа");

        textFragmentColor = new ColorSetting("Цвет текста фрагмента",Color.RED);
        textFragmenSize=new SizeSetting(100,"Размер шрифта фрагмента");

    }
    public Setting(String folderSetting, String settingFile)
    {
        this.folderSetting=folderSetting;
        this.settingFile=settingFile;
        if(NumberSize==null)
            InitDefault();
        StartRead();
        MainPath=folderSetting;
    }

    public void WriteSetting(){
        try{
            File sdPath= Environment.getExternalStorageDirectory();;
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
           // bw.write(TextColor+"--  Text Color\n");
            bw.write(LayOutBackGraundColor+"--  LayOut BackGraund Color\n");
            bw.write(textColorHex+"-- цвет текста (Text Color)\n");

            bw.write(TextInfoSize+"--  Текст Information Size\n");
            bw.write(TextDateSize+"--  Text Date Size\n");
            bw.write(TextMassageSize+"--  Text Massage Size\n");
            bw.write(NumberSize+"--  Number Size\n");
            bw.write(typeDate+"--  type Date\n");

            bw.write(textFragmentColor+"--  text Fragment Color\n");
            bw.write(textFragmenSize+"--  text Fragment Color\n");

            bw.close();
        }catch (IOException r){

        }
    }

    public void StartRead()
    {
        File sdPath= Environment.getExternalStorageDirectory();;
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folderSetting);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, settingFile);
        try{
            if(!sdFile.canRead())
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
            //TextColor= getStringBeforCommends(br.readLine());
            LayOutBackGraundColor.setColor(getStringBeforCommends(br.readLine()));
            textColorHex.setColor(getStringBeforCommends(br.readLine()));

            TextInfoSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            TextDateSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            TextMassageSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));
            NumberSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));

            typeDate= getStringBeforCommends(br.readLine());

            textFragmentColor.setColor(getStringBeforCommends(br.readLine()));
            textFragmenSize.value=Integer.parseInt(getStringBeforCommends(br.readLine()));


        }catch (IOException r)
        {

        }
    }
    private String getStringBeforCommends(String s)
    {
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


}
