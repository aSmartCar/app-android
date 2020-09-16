package com.example.autonomesfahrzeug;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.util.IOUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * The type Save management.
 */
public class SaveManagement {
  private static final String string_FILENAME = Environment.getExternalStorageDirectory()
          + File.separator + "Autonomes Fahrzeug" + File.separator;

  /**
   * The App ordner.
   */
  static String appOrdner;

  /**
   * Root Path of App.
   *
   * @param activity Activity
   * @return String root path
   */
  public static String getRootPath(Activity activity) {
    String path;
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      path = activity.getExternalFilesDir(null).getAbsolutePath()
              + File.separator + "Autonomes Fahrzeug" + File.separator;
    } else {
      path = Environment.getExternalStorageDirectory() + File.separator
              + "Autonomes Fahrzeug" + File.separator;
    }
    return path;
  }

  /**
   * Save File to Path.
   *
   * @param path String
   * @param data byte array
   */
  public static void saveFile(String path, byte[] data) {
    //camera.stopPreview();

    //Ausgabepfad erstellen mit Dateinamen
    File saveFile = new File(path);
    if (saveFile == null) {
      return;
    } else {
      try {
        OutputStream out = new FileOutputStream(saveFile);
        out.write(data);
        out.flush();
        out.close();

      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }



  private static void setUpAppOrdner(Activity activity) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      appOrdner = activity.getExternalFilesDir(null).getAbsolutePath()
              + File.separator + "Autonomes Fahrzeug" + File.separator;
    } else {
      appOrdner = Environment.getExternalStorageDirectory()
              + File.separator + "Autonomes Fahrzeug" + File.separator;
    }
  }

  private static File[] getFilesInAppDirectory(Activity activity) {
    setUpAppOrdner(activity);
    File f = new File(appOrdner);
    File[] file = f.listFiles();
    return  file;
  }


  /**
   * Get files in directory of the path.
   *
   * @param path String
   * @return file array
   */
  public static File[] getFilesInDirectory(String path) {
    File f = new File(path);
    File[] file = f.listFiles();
    return  file;
  }

  /**
   * getChosen neuronal network.
   *
   * @param name     String
   * @param activity Activity
   * @return file chossen nn
   */
  public static File getChossenNn(String name, Activity activity) {
    File[] dateien = getFilesInAppDirectory(activity);
    for (File f: dateien) {
      if (f.getName().contains(name)) {
        return f;
      }
    }
    return null;
  }

  /**
   * load File.
   *
   * @param filename STring
   * @return byte array
   */
  public static byte[] loadFile(String filename) {
    byte[] bytes = null;
    FileInputStream fileInputStream;
    try {
      fileInputStream = new FileInputStream(filename);
      InputStream stream = new BufferedInputStream(fileInputStream);
      bytes = IOUtils.toByteArray(stream);
      fileInputStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;
  }

  /**
   * load File.
   *
   * @param filename  String
   * @param directory String
   * @return file file
   */
  public static File loadFile(String filename, String directory) {
    File[] files = getFilesInDirectory(directory);
    for (File f : files) {
      if (f.getName().equals(filename)) {
        return f;
      }
    }
    return null;
  }

  /**
   * Load file cointaing the String.
   *
   * @param filename  String
   * @param directory String
   * @return File file
   */
  public static File loadFileContainingString(String filename, File directory) {
    File[] files = directory.listFiles();
    if (files == null){
      return null;
    }
    for (File f : files) {
      if (f.getName().contains(filename)) {
        return f;
      } else if (f.isDirectory()) {
        File file = loadFileContainingString(filename, f);
        if (file != null) {
          return file;
        }
      }
    }
    return null;
  }

  /**
   * Copies IPYNB-File from the assets to File.
   *
   * @param context Context
   * @param name    String
   * @throws IOException Exceptio
   */
  public static void copyInputStreamToFile(Context context, String name)
          throws IOException {

    try (FileOutputStream outputStream = new FileOutputStream(string_FILENAME + name)) {

      int read;
      byte[] bytes = new byte[1024];
      while ((read = context.getAssets().open("training.ipynb").read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
    }
  }

  /**
   * Load NN.
   *
   * @param activity Activity
   * @return file file
   */
  public static File ladeNNausAssets(Activity activity) {
    String name = "training.ipynb";
    String path = getRootPath(activity) + name;
    try {
      InputStream is = activity.getAssets().open(name);
      byte[] buffer = new byte[is.available()];
      is.read(buffer);

      File targetFile = new File(path);
      OutputStream outStream = new FileOutputStream(targetFile);
      outStream.write(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new File(path);
  }


  /**
   * load Test Bitmaps.
   *
   * @param activity Activity
   * @return ArrayList array list
   */
  public static ArrayList<Bitmap> ladeBitmaps(AppCompatActivity activity) {
    String rootPath = getRootPath(activity);
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    File[] files = new File(rootPath + "Testdatensatz" + File.separator).listFiles();
    for (File f:files) {
      byte[] bytes = loadFile(f.getPath());
      bitmaps.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }
    return bitmaps;
  }

}
