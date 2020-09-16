package com.example.autonomesfahrzeug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

/**
 * The type Dialog settings name.
 */
public class DialogSettingsName {
  private AppCompatActivity activity;
  private AlertDialog dialog;
  private String name;
  private CurrentSettings currentSettings;


  /**
   * Instantiates a new Dialog settings name.
   *
   * @param activity        the activity
   * @param currentSettings the current settings
   */
  public DialogSettingsName(AppCompatActivity activity, CurrentSettings currentSettings) {
    this.activity = activity;
    this.currentSettings = currentSettings;
  }

  /**
   * Start dialog.
   */
  void startDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

    View customLayout = activity.getLayoutInflater().inflate(R.layout.settings_name_dialog, null);
    builder.setView(customLayout);
    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String s = ((com.google.android.material.textfield.TextInputEditText)customLayout.findViewById(R.id.settingsName)).getText().toString();
        Snackbar.make(activity.getWindow().getDecorView().findViewById(android.R.id.content), s + "!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        currentSettings.saveSettings();
        String user = currentSettings.getServerUser();
        String password = currentSettings.getServerPassword();
        String ip = currentSettings.getServerIpAdress();
        int port = currentSettings.getServerPort();
        String localSettingsPath = currentSettings.getSettingsPath() + File.separator + "Settings.json";
        String serverSettingsPath = currentSettings.getServerHomePath() + File.separator + "ASV" + File.separator + "Settings" + File.separator + s;
        new AsyncTask<Integer, Void, Void>(){
          @Override
          protected Void doInBackground(Integer... params) {
            try {

              SshManager.uploadFile(
                      user,
                      password,
                      ip,
                      port,
                      localSettingsPath,
                      serverSettingsPath);
              activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Snackbar.make(activity.getWindow().getDecorView().findViewById(android.R.id.content), "Settings uploaded!", Snackbar.LENGTH_LONG)
                          .setAction("Action", null).show();
                }
              });
              ActivitySettings.updateServerSettingsSpinner(user, password, ip, port, activity);
            } catch (Exception e) {
              activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Snackbar.make(activity.getWindow().getDecorView().findViewById(android.R.id.content), "Settings not uploaded!", Snackbar.LENGTH_LONG)
                          .setAction("Action", null).show();
                }
              });
              e.printStackTrace();
            }
            return null;
          }
        }.execute(1);

      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

      }
    });
    dialog = builder.create();
    dialog.show();
  }
}
