package com.example.autonomesfahrzeug;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ZipFileTest {



  @Test
  public void unzip() throws IOException {
    File dir = new File("tmp");
    dir.mkdirs();
    File newDir = new File("newDir");
    ZipFile.zipFileAtPath(dir.getAbsolutePath(), dir.getAbsolutePath() + ".zip");
    File zipFile = new File("tmp.zip");
    Assert.assertEquals(false,  newDir.exists());
    ZipFile.unzip(zipFile, newDir);
    Assert.assertEquals(true, newDir.exists());
    //TODO
  }

  @Test
  public void zipFileAtPath() {
    File file = new File("tmp");
    file.mkdirs();
    File zipFile = new File("tmp.zip");
    if(zipFile.exists()) {
      zipFile.delete();
    }
    Assert.assertEquals(false, new File(file.getAbsoluteFile() + ".zip").exists());
    ZipFile.zipFileAtPath(file.getAbsolutePath(), file.getAbsolutePath() + ".zip");
    Assert.assertEquals(true, new File(file.getAbsoluteFile() + ".zip").exists());
  }
}