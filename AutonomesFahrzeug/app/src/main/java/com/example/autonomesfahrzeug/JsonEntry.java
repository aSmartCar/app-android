package com.example.autonomesfahrzeug;

import androidx.annotation.NonNull;

/**
 * The type Json entry.
 *
 * @param <T> the type parameter
 */
public class JsonEntry<T> {

  private String key;
  private T data;

  /**
   * Instantiates a new Json entry.
   *
   * @param key  the key
   * @param data the data
   */
  public JsonEntry(String key, T data) {
    this.key = key;
    this.data = data;
  }

  /**
   * Gets data.
   *
   * @return the data
   */
  public T getData() {
    return data;
  }

  /**
   * Gets key.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * Sets data.
   *
   * @param data the data
   */
  public void setData(T data) {
    this.data = data;
  }

  @NonNull
  @Override
  public String toString() {
    return "Key: " + key + ", Data: " + data;
  }
}
