package com.example.autonomesfahrzeug;

import android.graphics.Bitmap;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatasetTest {

  Dataset dataset;
  Bitmap datasetBmp;
  JSONObject datasetJson;
  String datasetName;
  int datasetNumber;

  @Before
  public void setUp() throws Exception {
    datasetBmp = Bitmap.createBitmap(320, 240, Bitmap.Config.RGB_565);
    datasetJson = new JSONObject();
    datasetJson.put("steering", 1.0);
    datasetJson.put("throttle", 0.5);
    datasetName = "train-1";
    datasetNumber = 1;
    dataset = new Dataset(datasetBmp, datasetJson, datasetName, datasetNumber);
  }

  @Test
  public void getBmp() {
    assertEquals(datasetBmp, dataset.getBmp());
  }

  @Test
  public void setBmp() {
    Bitmap bmp = Bitmap.createBitmap(320, 240, Bitmap.Config.RGB_565);
    dataset.setBmp(bmp);
    assertEquals(bmp, dataset.getBmp());
  }

  @Test
  public void getNumber() {
    assertEquals(datasetNumber, dataset.getNumber());
  }

  @Test
  public void testToString() {
    assertEquals("Dataset{"
            + "json=" + datasetJson.toString()
            + ", name='" + datasetName + '\''
            //+ ", bmp=" + bmp
            + ", number=" + datasetNumber
            + '}', dataset.toString());
  }

}