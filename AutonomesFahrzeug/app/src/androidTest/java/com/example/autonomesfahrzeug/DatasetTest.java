package com.example.autonomesfahrzeug;

import android.graphics.Bitmap;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
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
  public void save() {
  }
}