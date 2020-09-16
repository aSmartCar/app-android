package com.example.autonomesfahrzeug;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Logger {

  private ArrayList<String[]> logs;
  private CurrentSettings cs;
  private String logPath;
  private String name;
  public Logger(CurrentSettings currentSettings, String name) {
    logs = new ArrayList<>();
    cs = currentSettings;
    logPath = cs.getAppRootPath() + File.separator + "Logs";
    new File(logPath).mkdirs();
    this.name = name;
  }

  public void addLog(String[] strings) {
    String data = "";
    for(int i = 0; i < strings.length; i++) {
      if (i < strings.length - 1) {
        data += strings[i] + ", ";
      } else {
        data += strings[i];
      }
    }
    writeData(data,  logPath + File.separator + name + ".csv");
  }

  public ArrayList<String[]> getLogs() {
    return logs;
  }


  public static void writeData(String data,String strFilePath)
  {
    PrintWriter csvWriter;
    try
    {

      File file = new File(strFilePath);
      if(!file.exists()){
        file = new File(strFilePath);
      }
      csvWriter = new  PrintWriter(new FileWriter(file,true));


      csvWriter.print(data);
      csvWriter.append("\r\n");
      csvWriter.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
