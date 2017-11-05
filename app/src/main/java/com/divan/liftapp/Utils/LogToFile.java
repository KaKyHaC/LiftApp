package com.divan.liftapp.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Dima on 03.11.2017.
 */

public class LogToFile {
    File log;

    public LogToFile(String pathToFile) throws Exception {
        File f=new File(pathToFile);
        log=f;
    }

    public void Log(String s)throws Exception{
        FileWriter fw=new FileWriter(log,true);
        fw.write("\n");
        fw.write(Calendar.getInstance().toString()+'\n');
        fw.write(s+'\n');
        fw.close();
    }
}
