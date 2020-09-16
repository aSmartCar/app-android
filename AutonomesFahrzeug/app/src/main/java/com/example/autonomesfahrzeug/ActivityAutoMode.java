package com.example.autonomesfahrzeug;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

public class ActivityAutoMode extends AppCompatActivity {

  private int counter = 0;
  public static int loLeft;
  public static int loTop;
  public static int ruLeft;
  public static int ruTop;

  private static float steering;
  private static float throttle;
  private final float secondInMilliseconds = 1000;
  public static final float minValue = -1f;
  public static final float maxValue = 1f;
  float ypos;

  private long frameRate;

  public static boolean running = false;
  private boolean checking = true;

  private static DecimalFormat df = new DecimalFormat("0.00");

  // Camera 2 API
  private Camera2 camera2;


  private Button startStopButton;
  Context context;

  // TensorFlow Lite Modell
  private Interpreter tflite;


  // UI Elements
  private static TextView steeringText;
  private static TextView steeringDirectionTect;
  private TextView throttleText;
  private TextView numberOfPictures;
  private TextView connectedWithCarText;
  private TextView lenseName;
  private Switch enableConfigurationSwitch;
  private ImageView configurationImage;
  private static AppCompatActivity activity;
  private TextureView textureView;

  //ArrayLists
  private static ArrayList<JsonEntry> jsonEntriesStop = new ArrayList<>();
  private static ArrayList<JsonEntry> jsonEntriesSend = new ArrayList<>();
  public ArrayList<String[]> values = new ArrayList<>();

  //JsonEntries
  JsonEntry<Float> steeringNullJsonEntry = new JsonEntry<>(JsonKey.steering, new Float(0.0f));
  JsonEntry<Float> throttleNullJsonEntry = new JsonEntry<>(JsonKey.throttle, new Float(0.0f));
  private static JsonEntry<Float> steeringJsonEntry = new JsonEntry<>(JsonKey.steering, new Float(0.0f));
  private static JsonEntry<Float> throttleJsonEntry = new JsonEntry<>(JsonKey.throttle, new Float(0.0f));
  JsonEntry<String> modeJsonEntry = new JsonEntry<>(JsonKey.mode, Mode.auto.toString());


  private ControllerConnection controllerConnection;
  public static UdpClient udpClient;
  private CurrentSettings currentSettings;
  private Mode mode = Mode.auto;
  private Bitmap calibrationBitmap;

  static SimpleDateFormat datumsformat = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");

  private String valuesToString() {
    String s = "";
    for (String[] stringArr: values) {
      for (int i = 0; i < stringArr.length; i++) {
        if (i == stringArr.length - 1) {
          s += stringArr[i] + "\n";
        } else {
          s += stringArr[i] + ",";
        }
      }
    }
    return s;
  }


  public File getChosenNeuronalNetwork() {
    return SaveManagement.getChossenNn(currentSettings.getChosenNN(), this);
  }

  public void setBackButton(boolean b) {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(b);
  }

  public boolean setNNFile(File folder){
    File[] files = folder.listFiles();
    for (File file: files) {
      if(file.getName().contains(".tflite")) {
        currentSettings.setFileChosenNN(file);
        return true;
      }
      if (file.isDirectory()){
        if (setNNFile(file)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle(getString(R.string.btn_autonom));
    setContentView(R.layout.activity_auto_mode);
    setBackButton(true);

    currentSettings = CurrentSettings.getCurrentSettings(this);
    currentSettings.loadAutoModeSettings();
    setNNFile(currentSettings.getFolderChosenNN());
    frameRate = (long) secondInMilliseconds / (long) currentSettings.getPicturesPerSecond();

    loLeft = currentSettings.getLoLeftMargin();
    loTop = currentSettings.getLoTopMargin();
    ruLeft = currentSettings.getRuLeftMargin();
    ruTop = currentSettings.getRuTopMargin();

    udpClient = new UdpClient(currentSettings.getAppPort(),
            currentSettings.getBoardPort(),
            currentSettings.getIpAdress());


    findViewById(R.id.textureView).setVisibility(View.VISIBLE);

    byte[] tmpByte = SaveManagement.loadFile(SaveManagement
            .loadFileContainingString(".jpg", currentSettings.getFolderChosenNN()).toString());
    Bitmap tmpBitmap = BitmapFactory.decodeByteArray(tmpByte, 0, tmpByte.length);
    Matrix matrix = new Matrix();
    //matrix.postRotate(90);
    calibrationBitmap = Bitmap.createBitmap(tmpBitmap, 0,
            0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
    context = this;
    activity = this;


    steeringText = findViewById(R.id.textView_Lenkwinkel);
    steeringDirectionTect = findViewById(R.id.textView_lenkrichtung);
    throttleText = findViewById(R.id.textView_geschwindigkeit);
    enableConfigurationSwitch = findViewById(R.id.switch_configuration);
    configurationImage = findViewById(R.id.imageView_calibrate);
    textureView = findViewById(R.id.textureView);
    numberOfPictures = findViewById(R.id.textView_bilder_die_sekunde);
    connectedWithCarText = findViewById(R.id.textView_connectedWithCar);
    lenseName = findViewById(R.id.lenseNameAuto);

    enableConfigurationSwitch.setOnCheckedChangeListener(onCheckedChangeListener());

    lenseName.setText(getString(R.string.lenseName) + ": " + currentSettings.getLenseName());
    numberOfPictures.setText(getString(R.string.picturesTaken) + ": " + counter);

    if (currentSettings.getFileChosenNN() != null) {
      configurationImage.setVisibility(View.VISIBLE);
    } else {
      configurationImage.setVisibility(View.INVISIBLE);
    }

    jsonEntriesStop.add(steeringNullJsonEntry);
    jsonEntriesStop.add(throttleNullJsonEntry);
    jsonEntriesStop.add(modeJsonEntry);

    jsonEntriesSend = new ArrayList<>();
    jsonEntriesSend.add(steeringJsonEntry);
    jsonEntriesSend.add(throttleJsonEntry);
    jsonEntriesSend.add(modeJsonEntry);

    startStopButton = findViewById(R.id.buttonStartStop);
    startStopButton.setText("Start");
    try {
      tflite = new Interpreter(currentSettings.getFileChosenNN());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    camera2 = new Camera2(this, mode, new Size(currentSettings.getWidth(),
            currentSettings.getHeight()), currentSettings.getCameraID(), tflite, currentSettings.getPicturesPerSecond());


    new Thread(new Runnable() {
      @Override
      public void run() {
        checkConnectionWithCar();
      }
    }).start();

    startStopButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startenStoppen();
      }
    });
    configurationImage.setImageBitmap(camera2.takePicture());
    controllerConnection = new ControllerConnection(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if(!checking) {
      checking = true;
      new Thread(new Runnable() {
        @Override
        public void run() {
          checkConnectionWithCar();
        }
      }).start();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    udpClient.close();
  }

  @Override
  protected void onResume() {
    super.onResume();
    controllerConnection.start();
    udpClient.open();
  }

  @Override
  protected void onPause() {
    super.onPause();
    checking = false;
    controllerConnection.stop();
    udpClient.close();
  }



  private Switch.OnCheckedChangeListener onCheckedChangeListener() {
    return new Switch.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          Drawable d = new BitmapDrawable(getResources(),
                  ImageEditing.changeAlpha(calibrationBitmap, 125));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            configurationImage.setForeground(d);
          }
        } else {
          Drawable d = new BitmapDrawable(getResources(), camera2.takePicture());
          d.setAlpha(0);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            configurationImage.setForeground(d);
          }
        }
      }
    };
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_BUTTON_B: // stop capture
        if (running) {
          startenStoppen();
        } else {
          Intent intent = new Intent(this, ActivityMainMenu.class);
          startActivity(intent);
        }
        break;
      case KeyEvent.KEYCODE_BUTTON_A: // start capture
        if (!running) {
          startenStoppen();
        }
        break;
      case KeyEvent.KEYCODE_BUTTON_Y: // change mode
        Intent intent = new Intent(this, ActivityManualMode.class);
        startActivity(intent);
        break;
      default:
        break;
    }
    return true;
  }



  public static void stop() {
    JSONObject json = JsonFile.createJsonObject(jsonEntriesStop);
    new Thread(new Runnable() {
      @Override
      public void run() {
        udpClient.send(json);
      }
    }).start();
  }

  private void startenStoppen() {
    running = !running;
    if (running && tflite != null) {
      camera2.setPictureCounter(0);
      enableConfigurationSwitch.setChecked(false);
      setBackButton(false);
      startStopButton.setText(R.string.btn_stop_text);
    } else if (running && tflite == null) {
      running = false;
      Snackbar.make(activity.getWindow().getCurrentFocus(), getString(R.string.noNeuronalNetwork), Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    } else {
      setBackButton(true);
      startStopButton.setText(R.string.btn_start_text);
      stop();
    }
  }


  public static float interpretValues(float[] werte) {
    float max = 0.0f;
    int index = 0;
    for (int counter = 0; counter < werte.length; counter++) {
      if (Float.compare(werte[counter], max) > 0) {
        index = counter;
        max = werte[counter];
      }
    }
    float wert = 0;
    float diff = maxValue - minValue;
    wert = minValue + ((diff / (werte.length - 1)) * index);
    return wert;
  }

  public static JSONObject createJSONObject() {
    steeringJsonEntry.setData(-steering);
    throttleJsonEntry.setData(throttle);
    return JsonFile.createJsonObject(jsonEntriesSend);
  }

  public static void sendControlValues() {
    JSONObject jsonObject = createJSONObject();
    new Thread(new Runnable() {
      @Override
      public void run() {
        udpClient.send(jsonObject);
      }
    }).start();
  }


  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {

    // Check that the event came from a game controller
    if ((event.getSource() & InputDevice.SOURCE_JOYSTICK)
            == InputDevice.SOURCE_JOYSTICK
            && event.getAction() == MotionEvent.ACTION_MOVE) {

      // Process all historical movement samples in the batch
      final int historySize = event.getHistorySize();

      // Process the movements starting from the
      // earliest historical position in the batch
      for (int i = 0; i < historySize; i++) {
        // Process the event at historical position i
        processJoystickInput(event, i);
      }

      // Process the current movement sample in the batch (position -1)
      processJoystickInput(event, -1);
      return true;
    }
    return super.onGenericMotionEvent(event);
  }




  private void processJoystickInput(MotionEvent event,
                                    int historyPos) {
    InputDevice inputDevice = event.getDevice();

    // Calculate the horizontal distance to move by
    // using the input value from one of these physical controls:
    // the left control stick, hat axis, or the right control stick.
    ypos = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos);

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        setThrottle(ypos);
      }
    });
    setText();
  }

  private void setThrottle(float y) {
    throttle = -y;
    throttleText.setText(activity.getString(R.string.throttle) + ": " + df.format(throttle));
  }

  public static  void steeringToDirection(float x) {
    if (x <= -0.1) {
      steeringDirectionTect.setText(activity.getString(R.string.steeringDirection) + ": " + activity.getString(R.string.left));
    } else if (x < 0.1 && x > -0.1) {
      steeringDirectionTect.setText(activity.getString(R.string.steeringDirection) + ": " + activity.getString(R.string.straight));
    } else if (x >= 0.1) {
      steeringDirectionTect.setText(activity.getString(R.string.steeringDirection) + ": " + activity.getString(R.string.right));
    }
  }

  private static float getCenteredAxis(MotionEvent event,
                                       InputDevice device, int axis, int historyPos) {
    final InputDevice.MotionRange range =
            device.getMotionRange(axis, event.getSource());

    // A joystick at rest does not always report an absolute position of
    // (0,0). Use the getFlat() method to determine the range of values
    // bounding the joystick axis center.
    if (range != null) {
      final float flat = range.getFlat();
      final float value =
              historyPos < 0 ? event.getAxisValue(axis) :
                      event.getHistoricalAxisValue(axis, historyPos);

      // Ignore axis values that are within the 'flat' region of the
      // joystick axis center.
      if (Math.abs(value) > flat) {
        return value;
      }
    }
    return 0;
  }


  private void setText() {
    runOnUiThread(() -> updateValues());
  }

  private void updateValues() {
    steeringText.setText(getString(R.string.steering) + ": " + steering);
    throttleText.setText(getString(R.string.throttle) + ": " + df.format(throttle));
    steeringToDirection(steering);
  }

  private void checkConnectionWithCar() {
    checking = true;
    boolean status;
    while (checking) {
      status = udpClient.checkConnection();
      if (!status) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            connectedWithCarText.setText(getString(R.string.notConnectedESP));
            connectedWithCarText.setTextColor(Color.RED);
          }
        });
      } else {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            connectedWithCarText.setText(getString(R.string.connectedESP));
            connectedWithCarText.setTextColor(Color.GREEN);
          }
        });
      }
    }
  }
}
