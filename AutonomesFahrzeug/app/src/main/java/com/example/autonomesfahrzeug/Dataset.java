package com.example.autonomesfahrzeug;


import android.graphics.Bitmap;
import java.io.File;
import org.json.JSONObject;


/**
 * The type Dataset.
 */
public class Dataset {

  private JSONObject json;
  private String name;
  private Bitmap bmp;
  private int number;


  /**
   * Creates a new Instance of a Dataset.
   *
   * @param bmp    Bitmap of the dataset
   * @param json   JSONObject of the dataset
   * @param name   String name of the Dataset
   * @param number Capture Number of the dataset
   */
  public Dataset(Bitmap bmp, JSONObject json, String name, int number) {
    this.bmp = bmp;
    this.json = json;
    this.name = name;
    this.number = number;
  }

  /**
   * Saves a Dataset to the app directory of the smartphone.
   */
  public void save() {
    SaveManagement.saveFile(ActivityManualMode.getDatasetPicturesFolder().getPath()
            + File.separator + name + ".jpg", ImageEditing.bitmapToByteArray(bmp));
    SaveManagement.saveFile(ActivityManualMode.getDatasetJsonFolder().getPath()
            + File.separator + name + ".json", json.toString().getBytes());
  }

  /**
   * Gets bmp.
   *
   * @return the bmp
   */
  public Bitmap getBmp() {
    return bmp;
  }

  /**
   * Sets bmp.
   *
   * @param bmp the bmp
   */
  public void setBmp(Bitmap bmp) {
    this.bmp = bmp;
  }


  /**
   * Gets number.
   *
   * @return the number
   */
  public int getNumber() {
    return number;
  }

  @Override
  public String toString() {
    return "Dataset{"
            + "json=" + json
            + ", name='" + name + '\''
            + ", number=" + number
            + '}';
  }
}
