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
    //TODO make as library
    public enum FileType{setting,image,video,music,sound,backgraund,undefined}
    public static final int MEGABYTE=1024;

    public static boolean sendFile(File file,OutputStream socket){
        if(!file.isFile())
            return false;
        try {
            InputStream is = new FileInputStream(file);
            return sendFile(is,socket,getFileType(file),file.getAbsolutePath());
        }catch (FileNotFoundException e){}
        return false;
    }
    public static boolean sendFile(File file,OutputStream socket,FileType fileType){
        if(!file.isFile())
            return false;
        try {
            InputStream is = new FileInputStream(file);
            return sendFile(is,socket,fileType,file.getAbsolutePath());
        }catch (FileNotFoundException e){}
        return false;
    }
    private static boolean sendFile(InputStream in,OutputStream out,FileType fileType,String fullPath){
        byte buf[] = new byte[MEGABYTE];
        int len;
        try {
            out.write(getCode(fileType));

            String stName=giveNameOfFile(fullPath);
            byte[] mbName=convertNameToMB(stName);
            out.write(mbName);
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
    public static boolean receiveFile(InputStream socket, final Setting setting ){
        try {
            int typeCode=socket.read();
            FileType fileType = getFileType(typeCode);
            String path=getPathToSave(fileType,setting);
            if(path==null)
                return false;

            byte[] mbName=new byte[MEGABYTE];
            socket.read(mbName);
            String stName=convertMBtoName(mbName);
            path+=stName;
            OutputStream out=new FileOutputStream(path);
            return copyFile(socket,out);

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
            case setting:
                //path+=setting.fileSetting;
                return path;
            case image:path+=setting.folderImage;
                break;
            case video:path+=setting.folderVideo;
                break;
            case music:path+=setting.folderMusic;
                break;
            case sound:path+=setting.folderSound;
                break;
            case backgraund:path+=setting.folderBackGraund;
                break;
            case undefined:
            default:return null;
        }
        return path+'/';
    }

    private static byte[] convertNameToMB(String name){
        byte[] buf=new byte[MEGABYTE];
        char[] cName=name.toCharArray();
        for(int i=0;i<cName.length;i++){
            buf[i]=(byte)cName[i];
        }
        return buf;
    }
    private static String convertMBtoName(byte[] mb){
        StringBuilder sb=new StringBuilder();
        for(byte b:mb){
            if(b==0)break;
            sb.append((char)b);
        }
        return sb.toString();
    }

    private static String giveNameOfFile(String fullPath){
        int i;
        for(i=fullPath.length()-1;i>=0;i--){
            if(fullPath.charAt(i)=='/')
                break;
        }
        StringBuilder sb=new StringBuilder();
        for(i++;i<fullPath.length();i++){
            sb.append(fullPath.charAt(i));
        }
        return sb.toString();

    }
}
