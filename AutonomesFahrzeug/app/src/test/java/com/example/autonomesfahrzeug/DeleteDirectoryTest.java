package com.example.autonomesfahrzeug;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DeleteDirectoryTest {

  File dir;
  File subDir;
  File file;
  @Before
  public void setUp() throws Exception {
    dir = new File("dir");
    subDir = new File("dir" + File.separator + "subDir");
    file = new File("dir" + File.separator + "subDir" + File.separator + "file");
    dir.mkdirs();
    subDir.mkdirs();
    file.createNewFile();
  }

  @Test
  public void deleteRecursive() {
    Assert.assertEquals(true, dir.isDirectory());
    Assert.assertEquals(true, subDir.isDirectory());
    Assert.assertEquals(true, file.isFile());
    DeleteDirectory.deleteRecursive(dir);
    Assert.assertEquals(false, dir.exists());
    Assert.assertEquals(false, subDir.exists());
    Assert.assertEquals(false, file.exists());
  }
}