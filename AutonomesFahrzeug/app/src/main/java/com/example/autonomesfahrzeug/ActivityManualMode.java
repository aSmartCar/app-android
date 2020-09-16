package com.example.autonomesfahrzeug;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Size;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONObject;



public class ActivityManualMode extends AppCompatActivity {

  final Mode mode = Mode.manual;

  //Strings
  public static String datasetFolderName;

  //Integer
  // Limits to crop out a small image
  private static int upperLeftLeftLimit;
  private static int upperLeftTopLimit;
  private static int bottomRightLeftLimit;
  private static int bottomRightTopLimit;

  //Floats
  public static float steering = 0;
  public static float throttle = 0;
  private float joystickXPos;
  private float joystickYPos;


  //Boolean
  public static boolean sessionRunning = false;
  public static boolean saveInBackgroundRunning = false;
  private boolean checkingCarConnection = true;
  public static boolean saveDataset = true;
  public static boolean setupFinished = false;

  //Files
  public static File datasetFolder;
  private static File datasetPicturesFolder;
  private static File datasetJsonFolder;
  public static File datasetZip;

  //ArrayLists
  public static ArrayList<Dataset> datasets = new ArrayList<>();
  private static ArrayList<JsonEntry> jsonEntriesStop = new ArrayList<>();
  public static ArrayList<JsonEntry> jsonEntriesSend = new ArrayList<>();


  //JsonEntries
  JsonEntry<Float> steeringStopJsonEntry = new JsonEntry<>(JsonKey.steering, new Float(0.0f));
  JsonEntry<Float> throttleStopJsonEntry = new JsonEntry<>(JsonKey.throttle, new Float(0.0f));
  public static JsonEntry<Float> steeringJsonEntry = new JsonEntry<>(JsonKey.steering, new Float(0.0f));
  public static JsonEntry<Float> throttleJsonEntry = new JsonEntry<>(JsonKey.throttle, new Float(0.0f));
  public static JsonEntry<String> modeJsonEntry = new JsonEntry<>(JsonKey.mode, Mode.manual.toString());
  public static JsonEntry<Long> t1JsonEntry = new JsonEntry<>(JsonKey.t1, 0L);
  public static JsonEntry<Integer> ts_recJsonEntry = new JsonEntry<>(JsonKey.ts_rec, 0);

  //Formatter
  static SimpleDateFormat datumsformat = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");
  private static DecimalFormat df = new DecimalFormat("0.00");

  // UI Elements
  private TextView steeringText;
  private TextView throttleText;
  private TextView steeringDirectionText;
  private TextView framerateText;
  private TextView resolutionText;
  private TextView numberOfPicturesText;
  private TextView connectedWithCarText;
  private com.google.android.material.textfield.TextInputEditText lenseNameEditText;

  private Switch saveSwitch;
  private Switch objectSwitch;

  private Button capturePicturesButton;


  private Camera2 camera2;

  private static AppCompatActivity activity;
  private CurrentSettings currentSettings;
  public static UdpClient udpClient;
  private ControllerConnection controllerConnection;
  private DialogLoading dialogLoading;

  private static Logger logger;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manual_mode);
    getSupportActionBar().setTitle(getString(R.string.btn_manual));
    setBackButton(true);
    currentSettings = CurrentSettings.getCurrentSettings(this);
    upperLeftLeftLimit = currentSettings.getLoLeftMargin();
    upperLeftTopLimit = currentSettings.getLoTopMargin();
    bottomRightLeftLimit = currentSettings.getRuLeftMargin();
    bottomRightTopLimit = currentSettings.getRuTopMargin();

    udpClient = new UdpClient(currentSettings.getAppPort(),
            currentSettings.getBoardPort(), //"192.168.11.34");
            currentSettings.getIpAdress());
    activity = this;

    jsonEntriesStop.add(steeringStopJsonEntry);
    jsonEntriesStop.add(throttleStopJsonEntry);
    jsonEntriesStop.add(modeJsonEntry);
    jsonEntriesStop.add(t1JsonEntry);

    jsonEntriesSend = new ArrayList<>();
    jsonEntriesSend.add(steeringJsonEntry);
    jsonEntriesSend.add(throttleJsonEntry);
    jsonEntriesSend.add(modeJsonEntry);
    jsonEntriesSend.add(t1JsonEntry);

    initUi();


    camera2 = new Camera2(this, mode, new Size(currentSettings.getWidth(),
            currentSettings.getHeight()), currentSettings.getCameraID(), currentSettings.getPicturesPerSecond());
    camera2.fps = currentSettings.getPicturesPerSecond();


    framerateText.setText(activity.getString(R.string.framerate) + ": " + currentSettings.getPicturesPerSecond() + "\t");
    resolutionText.setText(activity.getString(R.string.resolution) + ": " + currentSettings.getWidth() + "x"
            + currentSettings.getHeight());

    controllerConnection = new ControllerConnection(this);
    //controllerConnection.start();
    sendControlValues();

    new Thread(new Runnable() {
      @Override
      public void run() {
        checkConnectionWithCar();
      }
    }).start();

    capturePicturesButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startStop();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    currentSettings.setManualMode(true);
    if(!checkingCarConnection) {
      checkingCarConnection = true;
      new Thread(new Runnable() {
        @Override
        public void run() {
          checkConnectionWithCar();
        }
      }).start();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    //controllerConnection.start();
  }

  @Override
  protected void onStop() {
    super.onStop();
    currentSettings.setManualMode(false);
  }

  @Override
  protected void onPause() {
    super.onPause();
    checkingCarConnection = false;
    udpClient.close();
    controllerConnection.stop();
  }

  /**
   * Sets the status of the back button in the ui
   * @param buttonStatus status
   */
  private void setBackButton(boolean buttonStatus) {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(buttonStatus);
  }


  /**
   * Initialize all UI Elements and set Listeners
   */
  private void initUi() {
    steeringText = findViewById(R.id.textView_Lenkwinkel_fern);
    steeringText.setText(activity.getString(R.string.steering) + ": " + 0);
    throttleText = findViewById(R.id.textView_geschwindigkeit_fern);
    throttleText.setText(activity.getString(R.string.throttle) + ": " + 0);
    steeringDirectionText = findViewById(R.id.textView_lenkrichtung_fern);
    steeringDirectionText.setText(activity.getString(R.string.steeringDirection) + ": " + 0);
    capturePicturesButton = findViewById(R.id.bilderAufnehmen);
    framerateText = findViewById(R.id.textView_fps);
    resolutionText = findViewById(R.id.textViewAuflösung);
    numberOfPicturesText = findViewById(R.id.textView_anzahlBilder);
    numberOfPicturesText.setText(activity.getString(R.string.picturesTaken) + ": " + 0);
    connectedWithCarText = findViewById(R.id.textView_connected);

    lenseNameEditText = findViewById(R.id.lenseName);
    lenseNameEditText.addTextChangedListener(lenseNameTextWatcher);

    saveSwitch = findViewById(R.id.switch_save);
    objectSwitch = findViewById(R.id.switch_objectiv);

    // Lense name edittext only visible when switch is active
    if (objectSwitch.isChecked()) {
      lenseNameEditText.setVisibility(View.VISIBLE);
    } else {
      lenseNameEditText.setVisibility(View.INVISIBLE);
    }

    // set Listener
    saveSwitch.setOnCheckedChangeListener(saveSwitchListener());
    objectSwitch.setOnCheckedChangeListener(objectiveSwitchListener());
  }

  private void startStop() {
    sessionRunning = !sessionRunning;
    if (sessionRunning) {
      setBackButton(false);
      capturePicturesButton.setText(R.string.btn_stop_text);
      if (saveDataset) {
        camera2.setPictureCounter(0);
        setupDatasetFolder();
        datasets = new ArrayList<>();
        saveInBackgroundRunning = false;
      }
      setupFinished = true;
    } else {
      stop();
      udpClient.setLogger(null);
      if (saveDataset) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            dialogLoading = new DialogLoading(activity);
            dialogLoading.startLoadingDialog();
          }
        });
        while (saveInBackgroundRunning) {;}

        SaveManagement.saveFile(datasetFolder.getPath() + File.separator
                + "index.json", currentSettings.toJsonObject().toString().getBytes());
        SaveManagement.saveFile(datasetFolder.getPath() + File.separator
                + "configurationPicture.jpg", ImageEditing.bitmapToByteArray(camera2.takePicture()));

        ZipFile.zipFileAtPath(datasetFolder.getPath(), datasetFolder.getPath() + ".zip");
        datasetZip = new File(datasetFolder.getPath() + ".zip");
        DeleteDirectory.deleteRecursive(datasetFolder);

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            dialogLoading.dismissDialog();
          }
        });
        dialogLoading.dismissDialog();
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Dataset all Datasets are saved!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
      setupFinished = false;
      setBackButton(true);
      capturePicturesButton.setText(R.string.btn_start_text);

    }
  }


  /**
   * checks the controllerConnection to the ESP32 of the car and presents the status on the display with a text.
   */
  private void checkConnectionWithCar() {
    checkingCarConnection = true;
    boolean status;
    while (checkingCarConnection) {
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

  /**
   * Saves all datasets in the ArrayList @link{#datasets} to the smartphone in a specific @link{datasetFolder}
   */
  public static void saveDatasetsInBackground() {
    while (datasets.size() > 0) {
      int size = datasets.size();
      long startSaveTime = System.nanoTime();
      Dataset dataset = datasets.get(0);
      datasets.remove(dataset);
      new Thread(new Runnable() {
        @Override
        public void run() {
          final long startSave = startSaveTime;
          Bitmap bmp = dataset.getBmp();
          Bitmap tmpBmp = ImageEditing.getSmallBitmap(bmp, upperLeftLeftLimit, upperLeftTopLimit, bottomRightLeftLimit, bottomRightTopLimit);
          dataset.setBmp(Bitmap.createScaledBitmap(tmpBmp, 160, 50, false));
          dataset.save();
          long endSaveTime = System.nanoTime();
          float deltaTime = ((float) endSaveTime - (float) startSave);
          float deltaTimeMillis = deltaTime / 1000000f;
          logger.addLog(new String[] {"" + dataset.getNumber(), "" + startSave, "" + endSaveTime, "" + deltaTimeMillis, "" + size});
        }
      }).start();
    }
    saveInBackgroundRunning = false;
  }

  /**
   * Creates a parent folder,
   * the child folder {@link #datasetPicturesFolder}
   * and another child folder {@link #datasetJsonFolder}
   * for a new dataset
   */
  private void setupDatasetFolder() {
    datasetFolder = new File(currentSettings.getFileDatasets(), datumsformat.format(new Date()));
    datasetFolder.mkdirs();
    datasetFolderName = datasetFolder.getName();
    datasetPicturesFolder = new File(datasetFolder, "Picture");
    datasetPicturesFolder.mkdirs();
    datasetJsonFolder = new File(datasetFolder, "Json");
    datasetJsonFolder.mkdirs();
    logger = new Logger(currentSettings, datasetFolderName + "-save");
    logger.addLog(new String[] {"Counter", "startSave", "EndSave", "DeltaMillis", "DatasetSize"});
    Logger camera2Logger = new Logger(currentSettings, datasetFolderName + "-camera");
    camera2Logger.addLog(new String[] {"timeNewPicture",
            "startTimeTakePicture", "endTimeTakePicture", "deltaTakePicture",
            "startTimeGetValues", "endTimeGetValues","deltaGetValues",
            "startTimesaveInList", "endTimeSaveInList", "deltaSaveInList"});
    camera2.setLogger(camera2Logger);
    Logger udpLogger = new Logger(currentSettings, datasetFolderName + "-udp");
    udpLogger.addLog(new String[] {"counter", "t1", "t2", "t3", "t4"});
    udpClient.setLogger(udpLogger);
  }

  /**
   * Sets the new Controller values to the ArrayList {@link #jsonEntriesSend}
   * and sends these values with the method {@link UdpClient#send(JSONObject)} to the ESP32
   *
   */
  public static void sendControlValues() {
    steeringJsonEntry.setData(-steering);
    throttleJsonEntry.setData(throttle);
    t1JsonEntry.setData(System.nanoTime());

    modeJsonEntry.setData("manual");
    JSONObject jsonObject = JsonFile.createJsonObject(jsonEntriesSend);
    new Thread(new Runnable() {
      @Override
      public void run() {
        udpClient.send(jsonObject);
      }
    }).start();
  }


  /**
   * Set Text Number Of Pictures.
   * @param counter pictures
   */
  public void setNumberPictures(int counter) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        numberOfPicturesText.setText(activity.getString(R.string.picturesTaken) + ": " + counter);
      }
    });
  }

  /**
   * Sends stop values to the ESP32 with the method {@link UdpClient#send(JSONObject)}
   */
  public static void stop() {
    t1JsonEntry.setData(System.nanoTime());
    JSONObject json = JsonFile.createJsonObject(jsonEntriesStop);
    new Thread(new Runnable() {
      @Override
      public void run() {
        udpClient.send(json);
      }
    }).start();
  }

  public static File getDatasetPicturesFolder() {
    return datasetPicturesFolder;
  }

  public static void setDatasetPicturesFolder(File datasetPicturesFolder) {
    ActivityManualMode.datasetPicturesFolder = datasetPicturesFolder;
  }

  public static File getDatasetJsonFolder() {
    return datasetJsonFolder;
  }

  public static void setDatasetJsonFolder(File datasetJsonFolder) {
    ActivityManualMode.datasetJsonFolder = datasetJsonFolder;
  }


  /**
   * Wertet das Drücken der Button aus.
   * DPAD_UP: Vorwärts
   * DPAD_DOWN: Rückwärts
   * B: Aufnahme stoppen oder wenn aufnahme gestoppt zurück zum Hauptmenu
   * A: Aufnahme beginnen
   * Y: Mode wechseln solange keine Aufnahme am laufen ist
   *
   * @param keyCode Welcher Button wurde gedrückt
   * @param event   Was wurde mit dem Button gemacht
   * @return
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_BUTTON_B: //stop capture
        if (sessionRunning) {
          startStop();
        } else {
          Intent intent = new Intent(this, ActivityMainMenu.class);
          startActivity(intent);
        }
        break;
      case KeyEvent.KEYCODE_BUTTON_A: //start capture
        if (!sessionRunning) {
          startStop();
        }
        break;
      case KeyEvent.KEYCODE_BUTTON_Y: //change mode
        if (!sessionRunning) {
          Intent intent = new Intent(this, ActivityAutoMode.class);
          startActivity(intent);
        }

        break;
      default:
        break;
    }
    return true;
  }

  /**
   * Processes the movement of a joystick
   * @param event movement of a joystick
   * @return old position of the joystick is different to the current position or not
   */
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

  /**
   * Checks if the joystick is in the zero point.
   * It is not always equals to (0,0)
   * @param event Event
   * @param device Device
   * @param axis axis of movement
   * @param historyPos previous Position
   * @return movement
   */
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

  /**
   * Get new values. All values are between -1 and 1.
   * @param event      event
   * @param historyPos old position
   */
  private void processJoystickInput(MotionEvent event,
                                    int historyPos) {
    InputDevice inputDevice = event.getDevice();
    // Calculate the horizontal distance to move by
    // using the input value from one of these physical controls:
    // the left control stick, hat axis, or the right control stick.
    joystickYPos = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos);
    joystickXPos = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos);

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        steeringControl(joystickXPos);
        throttleControl(joystickYPos);
      }
    });
  }

  /**
   * Changes UI values for steering
   *
   * @param steering steering value
   */
  private void steeringControl(float steering) {
    steeringToDirection(steering);
    camera2.lenkwinkel = steering;
    this.steering = steering;
    steeringText.setText(getString(R.string.steering) + ": " + df.format(this.steering));
  }


  /**
   * Changes UI values for steering
   *
   * @param throttle value of the controller for throttle
   */
  private void throttleControl(float throttle) {
    camera2.geschwindigkeit = -throttle;
    this.throttle = -throttle;
    throttleText.setText(getString(R.string.throttle) + ": " + df.format(this.throttle));
  }

  /**
   * TODO
   * Interpretiert in welche Richtung der Nutzer mit dem aktuellen Lenkwinkel lenkt.
   * x <= -0.1 Links
   * -0.1 < x < 0.1 Mitte
   * 0.1 < x Rechts
   *
   * @param x Werte des Lenkwinkels
   */
  private void steeringToDirection(float x) {
    if (x <= -0.1) {
      steeringDirectionText.setText(getString(R.string.steeringDirection) + ": " + getString(R.string.left));
    } else if (x < 0.1 && x > -0.1) {
      steeringDirectionText.setText(getString(R.string.steeringDirection) + ": " + getString(R.string.straight));
    } else if (x >= 0.1) {
      steeringDirectionText.setText(getString(R.string.steeringDirection) + ": " + getString(R.string.right));
    }
  }


  private TextWatcher lenseNameTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setLenseName(s.toString());
    }
  };

  private Switch.OnCheckedChangeListener saveSwitchListener() {
    return new Switch.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        saveDataset = !isChecked;
        if (isChecked) {
          objectSwitch.setClickable(false);
          lenseNameEditText.setVisibility(View.INVISIBLE);
        } else {
          objectSwitch.setClickable(true);
          if (objectSwitch.isChecked()) {
            lenseNameEditText.setVisibility(View.VISIBLE);
          } else {
            lenseNameEditText.setVisibility(View.INVISIBLE);
          }

        }
      }
    };
  }

  private Switch.OnCheckedChangeListener objectiveSwitchListener() {
    return new Switch.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        currentSettings.setWithLense(isChecked);
        if (isChecked) {
          lenseNameEditText.setVisibility(View.VISIBLE);
        } else {
          lenseNameEditText.setVisibility(View.INVISIBLE);
        }

      }
    };
  }
}
