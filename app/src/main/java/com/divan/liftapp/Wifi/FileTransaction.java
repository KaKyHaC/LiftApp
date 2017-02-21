package com.divan.liftapp.Wifi;

import android.support.annotation.NonNull;
import android.util.Log;

import com.divan.liftapp.Setting;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Dima on 21.02.2017.
 */

public abstract class FileTransaction {
    public enum FileType{setting,image,video,music,sound,backgraund,undefined}

    public static boolean sendFile(File file,OutputStream soccet){
        if(!file.isFile())
            return false;
        try {
            InputStream is = new FileInputStream(file);
            return sendFile(is,soccet,getFileType(file));
        }catch (FileNotFoundException e){}
        return false;
    }
    public static boolean sendFile(File file,OutputStream soccet,FileType fileType){
        if(!file.isFile())
            return false;
        try {
            InputStream is = new FileInputStream(file);
            return sendFile(is,soccet,fileType);
        }catch (FileNotFoundException e){}
        return false;
    }
    public static boolean sendFile(InputStream in,OutputStream out,FileType fileType){
        byte buf[] = new byte[1024];
        int len;
        try {
            out.write(getCode(fileType));
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            in.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    @NonNull
    public static boolean receiveFile(InputStream soccet, final Setting setting ){
        try {
            int typeCode=soccet.read();
            FileType fileType = getFileType(typeCode);
            String path=getPathToSave(fileType,setting);
            if(path==null)
                return false;

            OutputStream out=new FileOutputStream(path);
            return copyFile(soccet,out);

        }catch (IOException e){}
        return false;
    }
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    private static int getCode(FileType fileType){
        FileType[] types=FileType.values();
        for(int i=0;i<types.length;i++){
            if(fileType.equals(types[i]))
                return i;
        }
        return -1;
    }
    private static FileType getFileType(int Code){
        FileType[] types=FileType.values();
        if(Code>=0&&Code<types.length)
            return types[Code];
        return FileType.undefined;
    }
    private static FileType getFileType(File file){
        if(!file.isFile())
            return FileType.undefined;

        String path = file.getAbsolutePath();
        StringBuilder type=new StringBuilder();
        boolean isType=false;
        for(char c:path.toCharArray()){
            if(isType)
                type.append(c);
            else if(c=='.')
                isType=true;
        }
        switch (type.toString()){
            case "jpeg":
            case "jpg":return FileType.image;
            case "mp3":
            case "wav":return FileType.sound;
            case "txt":return FileType.setting;
            case "mp4":
            case "3gp":return  FileType.video;
            default:return FileType.undefined;
        }

    }

    private static String getPathToSave(final FileType fileType,final Setting setting){
        String path=setting.pathLiftFolder+'/';
        switch (fileType){
            case setting:path+=setting.settingFile;
                break;
            case image:path+=setting.ImageFolder;
                break;
            case video:path+=setting.ResourcesFolder;
                break;
            case music:path+=setting.MusicFolder;
                break;
            case sound:path+=setting.SoundFolder;
                break;
            case backgraund:path+=setting.BackGroundFolder;
                break;
            case undefined:
            default:return null;
        }
        return path;
    }
}
