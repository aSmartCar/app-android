package com.example.autonomesfahrzeug;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JsonEntryAndJsonFileTest {

  JsonEntry<Float> steering;
  JsonEntry<Float> throttle;
  Float steeringData = 1.0f;
  Float throttleData = 0.5f;
  String steeringKey = "steering";
  String throttleKey = "throttle";
  JSONObject jsonObject;
  ArrayList<JsonEntry> list;
  @Before
  public void setUp() throws Exception {
    steering = new JsonEntry<>(steeringKey, steeringData);
    throttle = new JsonEntry<>(throttleKey, throttleData);
    list = new ArrayList();
    list.add(steering);
    list.add(throttle);
    jsonObject = JsonFile.createJsonObject(list);
  }

  @Test
  public void getDataJsonEntry() {
    assertEquals(throttleData, throttle.getData());
    assertEquals(steeringData, steering.getData());
  }

  @Test
  public void getKeyJsonEntry() {
    assertEquals(steeringKey, steering.getKey());
    assertEquals(throttleKey, throttle.getKey());
  }

  @Test
  public void setDataJsonEntry() {
    steering.setData(0.0f);
    assertEquals(new Float(0.0f), steering.getData());
    throttle.setData(0.0f);
    assertEquals(new Float(0.0f), throttle.getData());
  }

  @Test
  public void testJsonEntrytoString() {
    assertEquals("Key: " + steeringKey + ", Data: " + steeringData, steering.toString());
    assertEquals("Key: " + throttleKey + ", Data: " + throttleData, throttle.toString());
  }

  @Test
  public void createJsonObject() {
    JSONObject json = jsonObject;
    jsonObject = null;
    assertEquals(null, jsonObject);
    jsonObject = JsonFile.createJsonObject(list);
    assertEquals(json.toString(), jsonObject.toString());

  }
}
