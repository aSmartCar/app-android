package com.example.autonomesfahrzeug;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


/**
 * The type Activity button configuration.
 */
public class ActivityButtonConfiguration extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_button_configuration);
    getSupportActionBar().setTitle(getString(R.string.button_conf));
  }
}
