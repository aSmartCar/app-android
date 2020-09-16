package com.example.autonomesfahrzeug;

import android.view.InputDevice;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * The type Controller connection.
 */
public class ControllerConnection {

  private static Thread thread;

  static boolean controllerConnected = false;
  static boolean shown = false;
  private boolean running = true;
  private AppCompatActivity activity;

  /**
   * Instantiates a new Controller connection.
   *
   * @param activity the activity
   */
  public ControllerConnection(AppCompatActivity activity) {
    this.activity = activity;
  }

  /**
   * Checks if the smartphone is connected to the XBoxController.
   */
  public void isConnectedToController() {
    thread = new Thread(new Runnable() {
      @Override
      public void run() {

        while (running) {
          controllerConnected = checkConnection();
          if (!controllerConnected && !shown) {
            DialogControllerConnection dialog = new DialogControllerConnection(activity);
            dialog.setCancelable(false);
            DialogControllerConnection.title = activity.getString(R.string.noContoller);
            DialogControllerConnection.text = activity.getString(R.string.connectController);
            AudioPlayer.playAudio(activity, R.raw.dland_hint);
            shown = true;
            dialog.show(activity.getSupportFragmentManager(), "");
          }
        }
      }
    });
    thread.start();
  }

  /**
   * stops to check the connection
   */
  public void stop() {
    running = false;
  }

  /**
   * starts to check the connection
   */
  public void start() {
    running = true;
    isConnectedToController();
  }

  /**
   * Gets all GameController.
   *
   * @return ArrayList game controller
   */
  public static ArrayList<InputDevice> getGameController() {
    ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
    int[] deviceIds = InputDevice.getDeviceIds();
    for (int deviceId : deviceIds) {
      InputDevice dev = InputDevice.getDevice(deviceId);
      int sources = dev.getSources();
      // Verify that the device has gamepad buttons, control sticks, or both.
      if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
              || ((sources & InputDevice.SOURCE_JOYSTICK)
              == InputDevice.SOURCE_JOYSTICK)) {
        // This device is a game controller. Store its device ID.
        if (!gameControllerDeviceIds.contains(deviceId)) {
          gameControllerDeviceIds.add(deviceId);
        }
      }
    }
    ArrayList<InputDevice> devices = new ArrayList<>();
    for (Integer i : gameControllerDeviceIds) {
      devices.add(InputDevice.getDevice(i));
    }
    return devices;
  }

  /**
   * checks if connected.
   *
   * @return boolean boolean
   */
  public static boolean checkConnection() {
    for (InputDevice device : getGameController()) {
      if (device.getName().toLowerCase().contains("controller")) {
        return true;
      }
    }
    return false;
  }
}
