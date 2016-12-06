package com.divan.liftapp;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Димка on 03.12.2016.
 */

public class Setting {
    private final String LOG_TAG="LiftApp";
    public String MainPath,BackGroundFolder,ImageFolder,SoundFolder,MusicFolder,MassageFolder,InformationFolder,ResourcesFolder;
    public String TextColor,LayOutBackGraundColor;
    public int TextInfoSize=30,TextDateSize=30,TextMassageSize=30;
    public int NumberSize=130;
    public String typeDate="dd:MM:yyyy EEEE HH:mm";
    public String pathSerialPort="";
    public int baudrate=-1;

    String[] AllDevicesPath;

    public Setting(String folderSetting,String settingFile)
    {
        StartRead(folderSetting,settingFile);
        FindAllDevices(folderSetting,"devices.txt");
        MainPath=folderSetting;
    }
    private void FindAllDevices(String folder,String devicesFile)
    {
        SerialPortFinder spf=new SerialPortFinder();
        AllDevicesPath=spf.getAllDevicesPath();

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folder);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, devicesFile);
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter(sdFile));
            for (String a:AllDevicesPath) {
                bw.write(a+'\n');
            }
            bw.close();
        }catch (IOException e){

        }
    }
    public void CreateDefaultSetting(File setting ,String folderSetting){
        try{
            FileWriter fw=new FileWriter(setting);
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(folderSetting+"--  mainPath\n");
            bw.write("BackGround"+"--  BackGround Folder\n");
            bw.write("Image"+"--  Image Folder\n");
            bw.write("Sound"+"--  Sound Folder\n");
            bw.write("Music"+"--  Music Folder\n");
            bw.write("Massage"+"--  Massage Folder\n");
            bw.write("Information"+"--  Information Folder\n");
            bw.write("Resources"+"--  Resources Folder\n");
            bw.write(TextColor+"--  Text Color\n");
            bw.write(LayOutBackGraundColor+"--  LayOut BackGraund Color\n");

            bw.write(TextInfoSize+"--  Text Information Size\n");
            bw.write(TextDateSize+"--  Text Date Size\n");
            bw.write(TextMassageSize+"--  Text Massage Size\n");
            bw.write(NumberSize+"--  Number Size\n");
            bw.write(typeDate+"--  type Date\n");

            bw.write(pathSerialPort+"-- path Serial Port\n");
            bw.write(baudrate+"-- baudrate\n");
            bw.close();
        }catch (IOException r){

        }
    }

    public void StartRead(String folderSetting,String settingFile)
    {
        File sdPath;
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            sdPath=Environment.getDataDirectory();
            //return;
        }
        else {// получаем путь к SD
            sdPath = Environment.getExternalStorageDirectory();
        }
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + folderSetting);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, settingFile);
        try{
            if(!sdFile.canRead())
            {
                CreateDefaultSetting(sdFile,folderSetting);
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
            TextColor= getStringBeforCommends(br.readLine());
            LayOutBackGraundColor= getStringBeforCommends(br.readLine());

            TextInfoSize=Integer.parseInt(getStringBeforCommends(br.readLine()));
            TextDateSize=Integer.parseInt(getStringBeforCommends(br.readLine()));
            TextMassageSize=Integer.parseInt(getStringBeforCommends(br.readLine()));
            NumberSize=Integer.parseInt(getStringBeforCommends(br.readLine()));

            typeDate= getStringBeforCommends(br.readLine());
            pathSerialPort= getStringBeforCommends(br.readLine());

            baudrate=Integer.parseInt(getStringBeforCommends(br.readLine()));

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
