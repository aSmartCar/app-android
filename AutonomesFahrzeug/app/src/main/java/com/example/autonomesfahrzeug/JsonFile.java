package com.example.autonomesfahrzeug;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The type Json file.
 */
public class JsonFile {

  /**
   * Loads Settings Json-File.
   *
   * @param directory String
   * @param fileName  String
   * @return JsonElement json element
   */
  public static JsonElement loadSettingsJsonElement(String directory, String fileName) {
    JsonObject json = null;
    byte[] jsonArr = SaveManagement.loadFile(directory + File.separator + fileName);
    try {
      JsonParser parser = new JsonParser();
      JSONObject jsonObject = new JSONObject(new String(jsonArr));
      json = (JsonObject) parser.parse(jsonObject.toString());
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
      return null;
    }
    return json;
  }

  public static JSONObject stringToJSONObject(String s) {
    try {
      return new JSONObject(new String(s.getBytes()));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * Creates JSONObject out of ArrayList.
   *
   * @param jsonEntries ArrayList
   * @return JSONObject json object
   */
  public static JSONObject createJsonObject(ArrayList<JsonEntry> jsonEntries) {
    JSONObject jsonObject = new JSONObject();
    for (JsonEntry entry: jsonEntries) {
      try {
        jsonObject.put(entry.getKey(), entry.getData());
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return jsonObject;
  }
}
