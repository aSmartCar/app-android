package com.example.autonomesfahrzeug;

import android.os.Build;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFile {


  /**
   * Unzips a File.
   * @param zipFile File
   * @param targetDirectory File
   * @throws IOException Exception
   */
  public static void unzip(File zipFile, File targetDirectory) throws IOException {
    ZipInputStream zis = new ZipInputStream(
            new BufferedInputStream(new FileInputStream(zipFile)));
    try {
      ZipEntry ze;
      int count;
      byte[] buffer = new byte[8192];
      while ((ze = zis.getNextEntry()) != null) {
        File file = new File(targetDirectory, ze.getName());
        File dir = ze.isDirectory() ? file : file.getParentFile();
        if (!dir.isDirectory() && !dir.mkdirs()) {
          throw new FileNotFoundException("Failed to ensure directory: "
                  + dir.getAbsolutePath());
        }
        if (ze.isDirectory()) {
          continue;
        }
        FileOutputStream fout = new FileOutputStream(file);
        try {
          while ((count = zis.read(buffer)) != -1) {
            fout.write(buffer, 0, count);
          }
        } finally {
          fout.close();
        }
      }
    } finally {
      zis.close();
    }
  }

  /**
   * Zips a Directory.
   * @param sourcePath String
   * @param toLocation String
   * @return boolean
   */
  public static boolean zipFileAtPath(String sourcePath, String toLocation) {
    final int buffer = 2048;

    File sourceFile = new File(sourcePath);
    try {
      BufferedInputStream origin;
      FileOutputStream dest = new FileOutputStream(toLocation);
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
              dest));
      if (sourceFile.isDirectory()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          zipSubFolder(out, sourceFile, Objects.requireNonNull(sourceFile.getParent()).length());
        }
      } else {
        FileInputStream fi = new FileInputStream(sourcePath);
        origin = new BufferedInputStream(fi, buffer);
        ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
        entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
        out.putNextEntry(entry);
        int count;
        byte[] data = new byte[buffer];
        while ((count = origin.read(data, 0, buffer)) != -1) {
          out.write(data, 0, count);
        }
      }
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    //DeleteRecursive(new File(sourcePath));
    return true;
  }

  private static void zipSubFolder(ZipOutputStream out, File folder,
                                   int basePathLength) throws IOException {

    final int buffer = 2048;

    File[] fileList = folder.listFiles();
    BufferedInputStream origin;
    if (fileList != null) {
      for (File file : fileList) {
        if (file.isDirectory()) {
          zipSubFolder(out, file, basePathLength);
        } else {
          String unmodifiedFilePath = file.getPath();
          String relativePath = unmodifiedFilePath
                  .substring(basePathLength);
          FileInputStream fi = new FileInputStream(unmodifiedFilePath);
          origin = new BufferedInputStream(fi, buffer);
          ZipEntry entry = new ZipEntry(relativePath);
          entry.setTime(file.lastModified()); // to keep modification time after unzipping
          out.putNextEntry(entry);
          int count;
          byte[] data = new byte[buffer];
          while ((count = origin.read(data, 0, buffer)) != -1) {
            out.write(data, 0, count);
          }
          origin.close();
        }
      }
    }
  }

  private static String getLastPathComponent(String filePath) {
    String[] segments = filePath.split("/");
    if (segments.length == 0) {
      return "";
    }
    return segments[segments.length - 1];
  }
}
