package com.example.autonomesfahrzeug;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ActivityMainMenu extends AppCompatActivity {


  private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
  private CurrentSettings currentSettings;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_menu);
    getSupportActionBar().setTitle(getString(R.string.mainMenu));
    currentSettings = CurrentSettings.getCurrentSettings(this);

    //https://droid-lernen.de/android-6-berechtigung-einholen 3.2.20 10:50 Uhr
    ActivityCompat.requestPermissions(this, new String[]{
        Manifest.permission.CAMERA,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE},
        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
  }


  /**
   * Called when the user taps the Einstellungen button.
   */
  public void toSettings(View view) {
    if (checkPermissions()) {
      // Permissions granted
      Intent intent = new Intent(this, ActivitySettings.class);
      startActivity(intent);
    } else {
      requestPermissions();
    }
  }


  /**
   * Called when the user taps the Manual mode button.
   */
  public void toManualMode(View view) {
    if (checkPermissions()) {
      Intent intent = new Intent(this, ActivityManualMode.class);
      startActivity(intent);
    } else {
      requestPermissions();
    }
  }

  /**
   * Called when the user taps the Autonomous mode button.
   */
  public void toAutoMode(View view) {
    if (checkPermissions()) {
      Intent intent = new Intent(this, ActivityAutoMode.class);
      startActivity(intent);
    } else {

    }
  }

  /**
   * Checks if all permissions are granted
   * @return
   */
  public boolean checkPermissions() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
      // Permissions granted
      return true;
    } else {
      // Permissions not granted
      // All permissions are requested
      return false;
    }

  }

  private void requestPermissions() {
    ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},
            ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
  }


  /**
   * processes the request for a permission
   * @param requestCode code of the request
   * @param permissions string array of the requested permissions
   * @param grantResults int array of the results
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case 1: {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
        }
        return;
      }
    }
  }
}
