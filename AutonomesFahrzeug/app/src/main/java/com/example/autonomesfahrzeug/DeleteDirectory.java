package com.example.autonomesfahrzeug;

import java.io.File;

/**
 * The type Delete directory.
 */
public class DeleteDirectory {


  /**
   * Deletes a directory.
   *
   * @param dir directory that should be deleted
   */
  public static void deleteRecursive(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        File temp = new File(dir, children[i]);
        if (temp.isDirectory()) {
          deleteRecursive(temp);
        } else {
          boolean b = temp.delete();
        }
      }

    }
    dir.delete();
  }
}
