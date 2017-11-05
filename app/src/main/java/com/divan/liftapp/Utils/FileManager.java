package com.divan.liftapp.Utils;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ????? on 13.12.2016.
 */

public class FileManager {

    public static List<String> getAllFilesPath(final File Directory, final String... types) {
        return fill(Directory.listFiles(), types);
    }

    public static List<String> getAllFilesPath(final String Directory, final String... types) {
        File path = new File(Directory);
        return fill(path.listFiles(), types);
    }

    public static File[] getAllFiles(final File Directory) {
        return Directory.listFiles();
    }

    public static File[] getAllFiles(final String Directory) {
        return new File(Directory).listFiles();
    }

    private static List<String> fill(File[] files, final String... types) {
        //clear list
        List<String> directoryEntries = new ArrayList<String>();

        //add only BMP file into list
        for (File file : files) {
            String path = file.getAbsolutePath();
            StringBuilder type = new StringBuilder();
            for (int i = path.length() - 3; i < path.length(); i++)
                type.append(path.charAt(i));

            for (String typ : types) {
                if (type.toString().equals(typ)) {
                    directoryEntries.add(file.getAbsolutePath());
                    break;
                }

            }

        }
        return directoryEntries;
    }

    public static String getAllTextFromDirectory(String Directory) {
        File[] files = getAllFiles(Directory);
        StringBuilder sb = new StringBuilder();
        if (files != null)
            for (File f : files) {
                try {
                    if (f.canRead()) {
                        BufferedReader br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
                        String s = br.readLine();
                        while (s != null) {
                            sb.append(s + "  ");
                            s = br.readLine();
                        }
                        br.close();
                    }
                } catch (IOException e) {

                }

            }

        return sb.toString();
    }
}