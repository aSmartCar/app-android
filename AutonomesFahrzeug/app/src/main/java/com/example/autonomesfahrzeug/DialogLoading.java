package com.example.autonomesfahrzeug;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

/**
 * The type Dialog loading.
 */
public class DialogLoading {

  private Activity activity;
  private AlertDialog dialog;

  /**
   * Instantiates a new Dialog loading.
   *
   * @param activity the activity
   */
  public DialogLoading(Activity activity) {
    this.activity = activity;
  }

  /**
   * Start loading dialog.
   */
  void startLoadingDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

    LayoutInflater inflater = activity.getLayoutInflater();
    builder.setView(inflater.inflate(R.layout.custom_dialog, null));
    builder.setCancelable(false);
    dialog = builder.create();
    dialog.show();
  }

  /**
   * Dismiss dialog.
   */
  void dismissDialog() {
    dialog.dismiss();
  }
}
