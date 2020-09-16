package com.example.autonomesfahrzeug;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * The type Dialog controller connection.
 */
public class DialogControllerConnection extends AppCompatDialogFragment {

  /**
   * Instantiates a new Dialog controller connection.
   *
   * @param activity the activity
   */
  public DialogControllerConnection(Activity activity) {
    this.activity = activity;
  }

  private Activity activity;
  static String title = "";
  static String text = "";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(title)
            .setMessage(text)
            .setCancelable(false)
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
              @Override
              public void onDismiss(DialogInterface dialog) {
                ControllerConnection.shown = false;
              }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override
              public void onCancel(DialogInterface dialog) {
                ControllerConnection.shown = false;
              }
            })

            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                ControllerConnection.shown = false;
              }
            });
    Dialog dialog = builder.create();
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(false);
    return dialog;
  }


}
