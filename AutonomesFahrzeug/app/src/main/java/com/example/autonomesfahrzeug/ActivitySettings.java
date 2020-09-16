package com.example.autonomesfahrzeug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Range;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Vector;


public class ActivitySettings extends AppCompatActivity {

  String serverHomePath = "";
  String serverNeuronalNetworksPath = "";
  String serverSettingsPath = "";
  Spinner serverNnSpinner;
  String serverIp = "192.168.11.34";
  String serverUser = "asv_user";
  String serverPassword = "12345678";
  int serverPort = 22;
  //KONSTANTEN____________________________________________________________________________________

  private final int squareSize = 40;
  private final int standardMargin = 0;

  private final Mode mode = Mode.stop;


  //VARIABLEN_____________________________________________________________________________________
  private static DecimalFormat df = new DecimalFormat("0.00");
  private ActivitySettings activitySettings;
  private Context context;

  //UI Elemente
  private TextView resolutionTextview;
  private TextView connectionTextview;
  private Spinner cameraSizesSpinner;
  private Spinner neuronalNetworksSpinner;
  private Spinner trainDatasetsSpinner;
  private Spinner cameraSpinner;
  private Spinner framerateSpinner;
  private static Spinner settingsSpinner;
  private CustomScrollView customScrollView;
  private Button uploadButton;
  private com.google.android.material.textfield.TextInputEditText boardPortEditText;
  private com.google.android.material.textfield.TextInputEditText appPortEditText;
  private com.google.android.material.textfield.TextInputEditText ipAdresseEditText;
  private com.google.android.material.textfield.TextInputEditText serverUserEditText;
  private com.google.android.material.textfield.TextInputEditText serverPasswordEditText;
  private com.google.android.material.textfield.TextInputEditText serverIpAdressEditText;
  private com.google.android.material.textfield.TextInputEditText serverPortEditText;
  private com.google.android.material.textfield.TextInputEditText neuronalNetworkPathEditText;
  private ImageView buttonConfigurationImageView;
  private ImageView upperLeftSquareImageView;
  private ImageView bottomLeftSquareImageView;
  private ImageView bottomRightSquareImageView;
  private ImageView upperRightSquareImageView;
  private ImageView backgroundRectangleImageView;
  private ImageView liveImageImageView;
  private Switch standardDirectoriesSwitch;

  //Area of interest variables
  private int xdelta;
  private int ydelta;
  private int xnew;
  private int ynew;
  private int maxLeftMargin = 480 - squareSize;
  private int maxTopMargin = 640 - squareSize;
  private float xfactor;
  private float yfactor;

  private boolean statusSftpConnection;


  private ArrayList<Integer> supportedFramerates;
  private String[] chosenResolution = new String[2];
  private String[] supportedCameraSizes;
  private String[] possibleTrainDatasets;
  private String[] possibleCameras;
  private File[] traindatasets;
  private File chosenTraindataset;

  private Range<Integer>[] supportedFrameratesRange;

  private Camera2 camera2;

  private ControllerConnection controllerConnection;

  String chosenNeuronalNetworkName = "";

  private static CurrentSettings currentSettings;

  private DialogSettingsName dialogSettingsName;
  private DialogLoading dialogLoading;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    getSupportActionBar().setTitle(getString(R.string.btn_settings));

    currentSettings = CurrentSettings.getCurrentSettings(this);
    camera2 = new Camera2(this, mode, new Size(currentSettings.getWidth(),
            currentSettings.getHeight()), currentSettings.getCameraID(), currentSettings.getPicturesPerSecond());

    context = this;
    activitySettings = this;
    dialogSettingsName = new DialogSettingsName(activitySettings, currentSettings);
    initializeAllUiVariables();


    maxLeftMargin = liveImageImageView.getLayoutParams().width - squareSize;
    maxTopMargin = liveImageImageView.getLayoutParams().height - squareSize;

    updateCameraSizesSpinner();

    updateDatasetSpinner();

    updateCameraSpinner();

    updateNeuronalNetworksSpinner();

    updateFramerateSpinner();

    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          updateServerSettingsSpinner(serverUser, serverPassword, serverIp, serverPort, activitySettings);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);


    setAllListeners();

    setBildbereichLayout();

    updateSettings();

    controllerConnection = new ControllerConnection(this);
    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          checkConnectionToSftpServer();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);

  }

  @Override
  protected void onStart() {
    super.onStart();
    currentSettings.setSettings(true);
    checkConnectionToSftpServer();
    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          updateServerSettingsSpinner(serverUser, serverPassword, serverIp, serverPort, activitySettings);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);
  }

  @Override
  protected void onStop() {
    super.onStop();
    RelativeLayout.LayoutParams lo
            = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
    RelativeLayout.LayoutParams ru
            = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();
    currentSettings.setLoLeftMargin(lo.leftMargin);
    currentSettings.setLoTopMargin(lo.topMargin);
    currentSettings.setRuLeftMargin(ru.leftMargin);
    currentSettings.setRuTopMargin(ru.topMargin);
    currentSettings.saveSettings();
    currentSettings.setSettings(false);
  }


  @Override
  protected void onResume() {
    super.onResume();
    checkConnectionToSftpServer();
    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          updateServerSettingsSpinner(serverUser, serverPassword, serverIp, serverPort, activitySettings);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);
  }

  @Override
  protected void onPause() {
    super.onPause();
    controllerConnection.stop();
  }

  private void checkConnectionToSftpServer() {

    statusSftpConnection = SshManager.checkConnection(currentSettings.getServerUser(), currentSettings.getServerPassword(), currentSettings.getServerIpAdress(), currentSettings.getServerPort());
    if (!statusSftpConnection) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          connectionTextview.setText(getString(R.string.notConnectedServer));
          connectionTextview.setTextColor(Color.RED);
        }
      });
    } else {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          connectionTextview.setText(getString(R.string.connectedServer));
          connectionTextview.setTextColor(Color.GREEN);
        }
      });
      //TODO: init directories, upload ipynb
    }
  }

  private void setUpResolutionSpinner() {
    setSupportedCamera2Sizes();
    ArrayAdapter<String> supportedCameraSizes = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, this.supportedCameraSizes);
    //set the spinners adapter to the previously created one.
    cameraSizesSpinner.setAdapter(supportedCameraSizes);
  }

  private void initializeAllUiVariables() {
    resolutionTextview = findViewById(R.id.textView_BIldbereich_Auflösung);
    connectionTextview = findViewById(R.id.textView_connectionServer);
    cameraSizesSpinner = findViewById(R.id.spinner_aufloesung);
    neuronalNetworksSpinner = findViewById(R.id.spinner_neuronaleNetze);
    trainDatasetsSpinner = findViewById(R.id.spinner_trainingsdaten);
    cameraSpinner = findViewById(R.id.spinner_kameraAuswahl);
    serverNnSpinner = findViewById(R.id.serverNnSpinner);
    framerateSpinner = findViewById(R.id.spinnerFramerate);
    settingsSpinner = findViewById(R.id.spinnerLoadSettings);
    customScrollView = findViewById(R.id.customScrollView);
    uploadButton = findViewById(R.id.trainigsdatenHochladen);
    boardPortEditText = findViewById(R.id.boardPort);
    appPortEditText = findViewById(R.id.appPort);
    ipAdresseEditText = findViewById(R.id.ipAdresse);
    buttonConfigurationImageView = findViewById(R.id.imageView_steuerung);
    upperLeftSquareImageView = findViewById(R.id.imageView_lo);
    bottomLeftSquareImageView = findViewById(R.id.imageView_lu);
    bottomRightSquareImageView = findViewById(R.id.imageView_ru);
    upperRightSquareImageView = findViewById(R.id.imageView_ro);
    backgroundRectangleImageView = findViewById(R.id.imageView_Bildbereich);
    liveImageImageView = findViewById(R.id.imageView4);
    serverIpAdressEditText = findViewById(R.id.serverIP);
    serverUserEditText = findViewById(R.id.serverUser);
    serverPasswordEditText = findViewById(R.id.serverPassword);
    serverPortEditText = findViewById(R.id.serverPort);
    standardDirectoriesSwitch = findViewById(R.id.switch_standardDirs);
    neuronalNetworkPathEditText = findViewById(R.id.neuronalNetworkPathEditText);
  }


  private void setAllListeners() {
    upperLeftSquareImageView.setOnTouchListener(onTouchListenerSquares());
    bottomLeftSquareImageView.setOnTouchListener(onTouchListenerSquares());
    bottomRightSquareImageView.setOnTouchListener(onTouchListenerSquares());
    upperRightSquareImageView.setOnTouchListener(onTouchListenerSquares());
    backgroundRectangleImageView.setOnTouchListener(onTouchListenerBackground());
    buttonConfigurationImageView.setOnTouchListener(onTouchListenerControl());
    cameraSizesSpinner.setOnItemSelectedListener(onItemSelectedListener_CameraSizes());
    neuronalNetworksSpinner.setOnItemSelectedListener(onItemSelectedListener_NeuronaleNetze());
    trainDatasetsSpinner.setOnItemSelectedListener(onItemSelectedListener_Trainingsdatensatz());
    cameraSpinner.setOnItemSelectedListener(onItemSelectedListener_KameraAuswahl());
    framerateSpinner.setOnItemSelectedListener(onItemSelectedListener_Framerate());
    standardDirectoriesSwitch.setOnCheckedChangeListener(onCheckedChangeListenerStandarddirectory());
    addTextwatcher();
  }


  private void setBildbereichLayout() {
    upperLeftSquareImageView.getLayoutParams().height = squareSize;
    upperLeftSquareImageView.getLayoutParams().width = squareSize;
    upperLeftSquareImageView.requestLayout();

    bottomLeftSquareImageView.getLayoutParams().height = squareSize;
    bottomLeftSquareImageView.getLayoutParams().width = squareSize;
    bottomLeftSquareImageView.requestLayout();

    bottomRightSquareImageView.getLayoutParams().height = squareSize;
    bottomRightSquareImageView.getLayoutParams().width = squareSize;
    bottomRightSquareImageView.requestLayout();

    upperRightSquareImageView.getLayoutParams().height = squareSize;
    upperRightSquareImageView.getLayoutParams().width = squareSize;
    upperRightSquareImageView.requestLayout();
  }

  public void createServerSubDirectory(String username,String password, String hostname, int port, String subDirectoryName) {
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
      session.setPassword(password);
      // Avoid asking for key confirmation
      Properties prop = new Properties();
      /*
      If this property is set to yes, JSch will never automatically add host keys to the $HOME/.ssh/known_hosts file, and refuses to connect to hosts whose host key has changed. This property forces the user to manually add all new hosts.

      If this property is set to no, JSch will automatically add new host keys to the user known hosts files.

      If this property is set to ask, new host keys will be added to the user known host files only after the user has confirmed that is what they really want to do, and JSch will refuse to connect to hosts whose host key has changed.
      */
      prop.put("StrictHostKeyChecking", "no");
      session.setConfig(prop);
      session.connect();
      // SSH Channel
      ChannelSftp channelssh = null;
      channelssh = (ChannelSftp) session.openChannel("sftp");
      channelssh.connect();
      try {
        serverHomePath = channelssh.getHome();
      } catch (SftpException e) {
        e.printStackTrace();
      }
      String subDir = currentSettings.getServerAppPath() + File.separator + subDirectoryName;
      System.out.println(subDir);
      try {
        channelssh.mkdir(subDir);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      channelssh.disconnect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
  }


  public void createServerAppDirectory(String username,String password, String hostname, int port){
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
      session.setPassword(password);
      // Avoid asking for key confirmation
      Properties prop = new Properties();
      /*
      If this property is set to yes, JSch will never automatically add host keys to the $HOME/.ssh/known_hosts file, and refuses to connect to hosts whose host key has changed. This property forces the user to manually add all new hosts.

      If this property is set to no, JSch will automatically add new host keys to the user known hosts files.

      If this property is set to ask, new host keys will be added to the user known host files only after the user has confirmed that is what they really want to do, and JSch will refuse to connect to hosts whose host key has changed.
      */
      prop.put("StrictHostKeyChecking", "no");
      session.setConfig(prop);
      session.connect();
      // SSH Channel
      ChannelSftp channelssh = null;
      channelssh = (ChannelSftp) session.openChannel("sftp");
      channelssh.connect();
      try {
        serverHomePath = channelssh.getHome();
      } catch (SftpException e) {
        e.printStackTrace();
      }
      currentSettings.setServerHomePath(serverHomePath);
      String serverAppPath = serverHomePath + "/ASV";
      currentSettings.setServerAppPath(serverAppPath);
      serverNeuronalNetworksPath = serverAppPath + "/NN";
      currentSettings.setServerTrainPath(serverAppPath + "/Train");
      serverSettingsPath = serverAppPath + File.separator + "Settings";
      try {
        channelssh.mkdir(serverAppPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      String serverNeuronalNetworksPath = currentSettings.getServerAppPath() + File.separator + "NN";
      try {
        channelssh.mkdir(serverNeuronalNetworksPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      Vector<ChannelSftp.LsEntry> vector = null;
      try {
        vector = channelssh.ls(serverNeuronalNetworksPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      ArrayList<String> entries = new ArrayList<>();
      for ( int i = 0; i < vector.size(); i++){
        String fileName = vector.get(i).getFilename();
        if(fileName.contains(".zip")) {
          entries.add(fileName);
        }
      }
      Collections.sort(entries);
      String[] serverNeuronalNetworks = new String[entries.size()];
      for ( int i = 0; i < entries.size(); i++){
        serverNeuronalNetworks[i] = entries.get(i);
      }
      final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
              this, android.R.layout.simple_spinner_item, serverNeuronalNetworks);
      runOnUiThread(new Thread(new Runnable() {
        @Override
        public void run() {
          serverNnSpinner.setAdapter(spinnerArrayAdapter);
        }
      }));
      try {
        channelssh.mkdir(currentSettings.getServerTrainPath());
      } catch (SftpException e) {
        e.printStackTrace();
      }
      try {
        channelssh.mkdir(serverSettingsPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      channelssh.disconnect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
  }

  public void updateServerNeuronalNetworkSpinner(String username,String password, String hostname, int port) {
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
      session.setPassword(password);
      // Avoid asking for key confirmation
      Properties prop = new Properties();
      /*
      If this property is set to yes, JSch will never automatically add host keys to the $HOME/.ssh/known_hosts file, and refuses to connect to hosts whose host key has changed. This property forces the user to manually add all new hosts.

      If this property is set to no, JSch will automatically add new host keys to the user known hosts files.

      If this property is set to ask, new host keys will be added to the user known host files only after the user has confirmed that is what they really want to do, and JSch will refuse to connect to hosts whose host key has changed.
      */
      prop.put("StrictHostKeyChecking", "no");
      session.setConfig(prop);
      session.connect();
      // SSH Channel
      ChannelSftp channelssh = null;
      channelssh = (ChannelSftp) session.openChannel("sftp");
      channelssh.connect();

      String serverNeuronalNetworksPath = currentSettings.getServerAppPath() + File.separator + "NN";

      Vector<ChannelSftp.LsEntry> vector = null;
      try {
        vector = channelssh.ls(serverNeuronalNetworksPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      ArrayList<String> entries = new ArrayList<>();
      for ( int i = 0; i < vector.size(); i++){
        String fileName = vector.get(i).getFilename();
        if(fileName.contains(".zip")) {
          entries.add(fileName);
        }
      }
      Collections.sort(entries);
      String[] serverNeuronalNetworks = new String[entries.size()];
      for ( int i = 0; i < entries.size(); i++){
        serverNeuronalNetworks[i] = entries.get(i);
      }
      final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
              this, android.R.layout.simple_spinner_item, serverNeuronalNetworks);
      runOnUiThread(new Thread(new Runnable() {
        @Override
        public void run() {
          serverNnSpinner.setAdapter(spinnerArrayAdapter);
        }
      }));
      try {
        channelssh.mkdir(currentSettings.getServerTrainPath());
      } catch (SftpException e) {
        e.printStackTrace();
      }
      try {
        channelssh.mkdir(serverSettingsPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      channelssh.disconnect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
  }


  public static void updateServerSettingsSpinner(String username, String password, String hostname, int port, AppCompatActivity activity) {
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
      session.setPassword(password);
      // Avoid asking for key confirmation
      Properties prop = new Properties();
      /*
      If this property is set to yes, JSch will never automatically add host keys to the $HOME/.ssh/known_hosts file, and refuses to connect to hosts whose host key has changed. This property forces the user to manually add all new hosts.

      If this property is set to no, JSch will automatically add new host keys to the user known hosts files.

      If this property is set to ask, new host keys will be added to the user known host files only after the user has confirmed that is what they really want to do, and JSch will refuse to connect to hosts whose host key has changed.
      */
      prop.put("StrictHostKeyChecking", "no");
      session.setConfig(prop);
      session.connect();
      // SSH Channel
      ChannelSftp channelssh = null;
      channelssh = (ChannelSftp) session.openChannel("sftp");
      channelssh.connect();

      String serverSettingsPath = currentSettings.getServerAppPath() + File.separator + "Settings";
      try {
        channelssh.mkdir(serverSettingsPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      Vector<ChannelSftp.LsEntry> vector = null;
      try {
        vector = channelssh.ls(serverSettingsPath);
      } catch (SftpException e) {
        e.printStackTrace();
      }
      ArrayList<String> entries = new ArrayList<>();
      for ( int i = 0; i < vector.size(); i++){
        String fileName = vector.get(i).getFilename();
        if(fileName.contains(".json")) {
          entries.add(fileName);
        }
      }
      Collections.sort(entries);
      String[] serverSettings = new String[entries.size()];
      for ( int i = 0; i < entries.size(); i++){
        serverSettings[i] = entries.get(i);
      }
      final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
              activity, android.R.layout.simple_spinner_item, serverSettings);
      activity.runOnUiThread(new Thread(new Runnable() {
        @Override
        public void run() {
          settingsSpinner.setAdapter(spinnerArrayAdapter);
        }
      }));

      channelssh.disconnect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
  }


  public void buttonUploadDataset(View view) {
    if (!statusSftpConnection) {
      Snackbar.make(view, getString(R.string.noSftpConnection), Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
      return;
    }
    if (SaveManagement.getFilesInDirectory(currentSettings.getDatasetsPath()).length > 0) {
      final String src = currentSettings.getDatasetsPath() + File.separator + trainDatasetsSpinner.getSelectedItem().toString();
      final String des = currentSettings.getServerTrainPath() + File.separator + trainDatasetsSpinner.getSelectedItem().toString();
      new AsyncTask<Integer, Void, Void>(){
        @Override
        protected Void doInBackground(Integer... params) {
          try {

            activitySettings.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                dialogLoading = new DialogLoading(activitySettings);
                dialogLoading.startLoadingDialog();
              }
            });
            createServerSubDirectory(serverUser, serverPassword, serverIp, serverPort, "Train");
            SshManager.uploadFile(serverUser, serverPassword, serverIp, serverPort, src, des);
            Snackbar.make(view, "Dataset uploaded!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            activitySettings.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                dialogLoading.dismissDialog();
              }
            });
            //updateLocalNNSpinner();
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        }
      }.execute(1);
    } else {
      Snackbar.make(view, "No dataset on the smartphone!", Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    }

  }

  public void buttonDownloadNn(View view) {
    if (!statusSftpConnection) {
      Snackbar.make(view, getString(R.string.noSftpConnection), Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
      return;
    }
    String src;
    if (standardDirectoriesSwitch.isChecked()) {
      src = serverNeuronalNetworksPath + File.separator + serverNnSpinner.getSelectedItem().toString();
    } else {
      src = neuronalNetworkPathEditText.getText().toString();
    }
    String[] srcPath = src.split(File.separator);
    final String des = currentSettings.getNeuronalNetworksPath() + File.separator + srcPath[srcPath.length - 1];
    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          SshManager.downloadFile(serverUser, serverPassword, serverIp, serverPort, src, des);
          Snackbar.make(view, "Neuronal network downloaded!", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
          updateNeuronalNetworksSpinner();
          //updateLocalNNSpinner();
        } catch (SftpException e) {
          Snackbar.make(view, "Neuronal network not downloaded. Check your path!", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);

  }

  public void uploadIpynbSftp(View view) {
    if (!statusSftpConnection) {
      Snackbar.make(view, getString(R.string.noSftpConnection), Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
      return;
    }
    File nn = SaveManagement.ladeNNausAssets(activitySettings);
    final String src = nn.getAbsolutePath();
    final String des = currentSettings.getServerHomePath() + File.separator + "ASV" + File.separator + "Train.ipynb";
    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          createServerAppDirectory(serverUser, serverPassword, serverIp, serverPort);
          SshManager.uploadFile(serverUser, serverPassword, serverIp, serverPort, src, des);
          Snackbar.make(view, "Training notebook uploaded!", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
          //updateLocalNNSpinner();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);
  }

  private void updateCameraSizesSpinner() {
    Size[] sizes = camera2.getSupportedCameraSizes(currentSettings.getCameraID());
    supportedCameraSizes = new String[sizes.length];
    int pos = 0;
    System.out.println("Current Size: " + currentSettings.getWidth() + "x" + currentSettings.getHeight());
    for (int i = 0; i < sizes.length; i++) {
      supportedCameraSizes[i] = sizes[i].getWidth() + "x" + sizes[i].getHeight();

    }

    ArrayAdapter<String> cameraSizes = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, supportedCameraSizes);
    for (int i = 0; i < cameraSizes.getCount(); i++) {
      String[] s = cameraSizes.getItem(i).split("x");
      int width = Integer.parseInt(s[0]);
      int height = Integer.parseInt(s[1]);
      if (width == currentSettings.getWidth() && height == currentSettings.getHeight()) {
        pos = i;
      }
    }
    //set the spinners adapter to the previously created one.
    cameraSizesSpinner.setAdapter(cameraSizes);
    cameraSizesSpinner.setSelection(pos);
  }

  private void updateDatasetSpinner() {
    traindatasets = SaveManagement.getFilesInDirectory(currentSettings.getDatasetsPath());
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < traindatasets.length; i++) {
      if (traindatasets[i].getName().contains(".zip")) {
        list.add(traindatasets[i].getName());
      }
    }
    Collections.sort(list);
    possibleTrainDatasets = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      possibleTrainDatasets[i] = list.get(i);
    }
    ArrayAdapter<String> traindataAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, possibleTrainDatasets);
    trainDatasetsSpinner.setAdapter(traindataAdapter);

  }

  private void updateCameraSpinner() {
    String [] backCameras = camera2.getBackCameras();
    possibleCameras = new String[backCameras.length];
    int pos = 0;
    for (int i = 0; i < possibleCameras.length; i++) {
      int k = i + 1;
      possibleCameras[i] = getString(R.string.backcamera) + " " + k;
      if (backCameras[i].equals(currentSettings.getCameraID())) {
        pos = i;
      }
    }
    if (possibleCameras.length == 1) {
      cameraSpinner.setVisibility(View.GONE);
    }

    ArrayAdapter<String> choosableCamerasAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, possibleCameras);
    cameraSpinner.setAdapter(choosableCamerasAdapter);
    cameraSpinner.setSelection(pos);
  }

  private void updateNeuronalNetworksSpinner() {
    File[] dateien = SaveManagement.getFilesInDirectory(currentSettings.getNeuronalNetworksPath());
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < dateien.length; i++) {
      if (dateien[i].getName().contains(".zip")) {
        list.add(dateien[i].getName().replace(".zip", ""));
      } else {
        list.add(dateien[i].getName());
      }
    }
    Collections.sort(list);
    String[] serverNeuronalNetworkNames = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      serverNeuronalNetworkNames[i] = list.get(i);
    }
    ArrayAdapter<String> neuronalNetworksAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, serverNeuronalNetworkNames);//serverNeuronalNetworks);
    neuronalNetworksSpinner.setAdapter(neuronalNetworksAdapter);
  }

  private void updateFramerateSpinner() {
    supportedFrameratesRange = new Range[1];
    int pos = 0;
    try {
      CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
      supportedFrameratesRange = manager.getCameraCharacteristics(currentSettings.getCameraID()).get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
    supportedFramerates = new ArrayList();
    for (Range r:supportedFrameratesRange) {
      if (r.getLower().equals(r.getUpper()) && ((Integer) r.getLower()) <= 30 && ((Integer) r.getLower()) >= 10) {
        supportedFramerates.add((Integer) r.getLower());
      }
    }
    for (int i = 0; i < supportedFramerates.size(); i++) {
      if (supportedFramerates.get(i).equals(currentSettings.getPicturesPerSecond())) {
        pos = i;
      }
    }
    String[] supportedFrameratesStrings = new String[supportedFramerates.size()];
    for (int i = 0; i < supportedFramerates.size(); i++) {
      supportedFrameratesStrings[i] = "" + supportedFramerates.get(i) + " " + getString(R.string.framesPerSecondString);
    }
    ArrayAdapter<String> framerateAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, supportedFrameratesStrings);//serverNeuronalNetworks);
    framerateSpinner.setAdapter(framerateAdapter);
    framerateSpinner.setSelection(pos);
  }

  /**
   * Set supported Camera sizes for spinner
   */
  private void setSupportedCamera2Sizes() {
    Size[] sizes = camera2.getSupportedCameraSizes(currentSettings.getCameraID());
    supportedCameraSizes = new String[sizes.length];

    for (int i = 0; i < sizes.length; i++) {
      supportedCameraSizes[i] = sizes[i].getWidth() + "x" + sizes[i].getHeight();
    }
  }


  //OnTouchListener area of interest
  private View.OnTouchListener onTouchListenerSquares() {
    return new View.OnTouchListener() {

      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

          case MotionEvent.ACTION_DOWN:
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)
                    view.getLayoutParams();

            xdelta = x - layoutParams1.leftMargin;
            ydelta = y - layoutParams1.topMargin;
            customScrollView.setEnableScrolling(false); // disable scrolling
            break;

          case MotionEvent.ACTION_UP:
            customScrollView.setEnableScrolling(true); // disable scrolling
            break;

          case MotionEvent.ACTION_MOVE:
            xnew = x - xdelta;
            if (xnew < standardMargin) {
              xnew = standardMargin;
            } else if (xnew > maxLeftMargin) {
              xnew = maxLeftMargin;
            }

            ynew = y - ydelta;
            if (ynew < standardMargin) {
              ynew = standardMargin;
            } else if (ynew > maxTopMargin) {
              ynew = maxTopMargin;
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                    .getLayoutParams();
            layoutParams.leftMargin = xnew;
            layoutParams.topMargin = ynew;
            layoutParams.rightMargin = standardMargin;
            layoutParams.bottomMargin = standardMargin;

            setPartnerPositionsSquares(context.getResources()
                    .getResourceEntryName(view.getId()), xnew, ynew);

            view.setLayoutParams(layoutParams);
            updateAreaOfInterestText();
            break;
          default:
            break;
        }
        return true;
      }
    };
  }

  //OnTouchListener move area of interest
  private View.OnTouchListener onTouchListenerBackground() {
    return new View.OnTouchListener() {

      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

          case MotionEvent.ACTION_DOWN:
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)
                    view.getLayoutParams();

            xdelta = x - layoutParams1.leftMargin;
            ydelta = y - layoutParams1.topMargin;
            customScrollView.setEnableScrolling(false); // disable scrolling
            break;

          case MotionEvent.ACTION_UP:
            customScrollView.setEnableScrolling(true); // disable scrolling
            break;

          case MotionEvent.ACTION_MOVE:
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                    .getLayoutParams();
            xnew = x - xdelta;
            if (xnew < standardMargin) {
              xnew = standardMargin;
            } else if (xnew + layoutParams.width > maxLeftMargin + squareSize) {
              xnew = maxLeftMargin - layoutParams.width + squareSize;
            }
            ynew = y - ydelta;
            if (ynew < standardMargin) {
              ynew = standardMargin;
            } else if (ynew + layoutParams.height > maxTopMargin + squareSize) {
              ynew = maxTopMargin - layoutParams.height + squareSize;
            }
            layoutParams.leftMargin = xnew;
            layoutParams.topMargin = ynew;
            layoutParams.rightMargin = standardMargin;
            layoutParams.bottomMargin = standardMargin;

            setSquarePositions(layoutParams);

            view.setLayoutParams(layoutParams);
            break;
          default:
            break;
        }
        return true;
      }
    };
  }

  private View.OnTouchListener onTouchListenerControl() {
    return new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            Intent intent = new Intent(activitySettings, ActivityButtonConfiguration.class);
            startActivity(intent);
            break;
          case MotionEvent.ACTION_CANCEL:
            break;
          default:
            break;
        }
        return false;
      }
    };
  }

  private Switch.OnCheckedChangeListener onCheckedChangeListenerStandarddirectory() {
    return new Switch.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          new AsyncTask<Integer, Void, Void>(){
            @Override
            protected Void doInBackground(Integer... params) {
              try {
                activitySettings.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    neuronalNetworkPathEditText.setVisibility(View.INVISIBLE);
                  }
                });
                createServerAppDirectory(serverUser,serverPassword,serverIp,serverPort);
                createServerSubDirectory(serverUser,serverPassword,serverIp,serverPort, "NN");
                updateServerNeuronalNetworkSpinner(serverUser,serverPassword,serverIp,serverPort);
                activitySettings.recreate();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            }
          }.execute(1);
        } else {
          activitySettings.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              neuronalNetworkPathEditText.setVisibility(View.VISIBLE);
              serverNnSpinner.setVisibility(View.INVISIBLE);
            }
          });
        }

      }
    };
  }

  private AdapterView.OnItemSelectedListener onItemSelectedListener_CameraSizes() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenResolution = parent.getItemAtPosition(position).toString().split("x");
        currentSettings.setWidth(Integer.parseInt(chosenResolution[1]));
        currentSettings.setHeight(Integer.parseInt(chosenResolution[0]));
        xfactor = ((float) currentSettings.getWidth() / (float) maxLeftMargin);
        yfactor = ((float) currentSettings.getHeight() / (float) maxTopMargin);
        currentSettings.setResolutionPosition(position);
        updateAreaOfInterestText();
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    };
  }

  private AdapterView.OnItemSelectedListener onItemSelectedListener_Framerate() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int framerate = Integer.parseInt(parent.getItemAtPosition(position).toString().split(" ")[0]);
        currentSettings.setPicturesPerSecond(framerate);
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    };
  }

  private void loadNeuronalNetwork() {
    File[] files = SaveManagement.getFilesInDirectory(currentSettings.getNeuronalNetworksPath());
    for (File file : files) {
      if (file.getName().contains(currentSettings.getChosenNN() + ".zip")) {
        try {
          ZipFile.unzip(new File(currentSettings.getNeuronalNetworksPath()
                          + File.separator + currentSettings.getChosenNN() + ".zip"),
                  new File(currentSettings.getNeuronalNetworksPath()
                          + File.separator + currentSettings.getChosenNN()));
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          try {
            Files.deleteIfExists(Paths.get(currentSettings.getNeuronalNetworksPath()
                    + File.separator + currentSettings.getChosenNN() + ".zip"));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }



  private AdapterView.OnItemSelectedListener onItemSelectedListener_NeuronaleNetze() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentSettings.setChosenNN(parent.getItemAtPosition(position).toString());
        loadNeuronalNetwork();
        currentSettings.setFolderChosenNN(
                new File(currentSettings.getNeuronalNetworksPath()
                        + File.separator + currentSettings.getChosenNN()
                        + File.separator));

        currentSettings.setFileChosenNN(new File(currentSettings.getNeuronalNetworksPath()
                + File.separator + currentSettings.getChosenNN()));
        chosenNeuronalNetworkName = parent.getItemAtPosition(position).toString();

      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    };
  }

  private AdapterView.OnItemSelectedListener onItemSelectedListener_Trainingsdatensatz() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String chosen = possibleTrainDatasets[position];
        for (File f : traindatasets) {
          if (f.getName().equals(chosen)) {
            chosenTraindataset = f;
            break;
          }
        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    };
  }

  private AdapterView.OnItemSelectedListener onItemSelectedListener_KameraAuswahl() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //TODO: Auswahl ausführen
        String[] ids = camera2.getBackCameras();
        currentSettings.setCameraID(ids[position]);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            setUpResolutionSpinner();
          }
        });
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    };
  }

  private TextWatcher appPortTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setAppPort(Integer.parseInt(s.toString()));
    }
  };

  private TextWatcher boardPortTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setBoardPort(Integer.parseInt(s.toString()));
    }
  };

  private TextWatcher ipAdressTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setIpAdress(s.toString());
    }
  };

  private TextWatcher serverIpTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setServerIpAdress(s.toString());
      serverIp = s.toString();
      new AsyncTask<Integer, Void, Void>(){
        @Override
        protected Void doInBackground(Integer... params) {
          try {
            checkConnectionToSftpServer();
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        }
      }.execute(1);
    }
  };

  private TextWatcher serverUserTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setServerUser(s.toString());
      serverUser = s.toString();
      new AsyncTask<Integer, Void, Void>(){
        @Override
        protected Void doInBackground(Integer... params) {
          try {
            checkConnectionToSftpServer();
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        }
      }.execute(1);
    }
  };

  private TextWatcher serverPasswordTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setServerPassword(s.toString());
      serverPassword = s.toString();
      new AsyncTask<Integer, Void, Void>(){
        @Override
        protected Void doInBackground(Integer... params) {
          try {
            checkConnectionToSftpServer();
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        }
      }.execute(1);
    }
  };

  private TextWatcher serverPortTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      currentSettings.setServerPort(Integer.parseInt(s.toString()));
      serverPort = Integer.parseInt(s.toString());
      new AsyncTask<Integer, Void, Void>(){
        @Override
        protected Void doInBackground(Integer... params) {
          try {
            checkConnectionToSftpServer();
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        }
      }.execute(1);
    }
  };

  private TextWatcher serverNeuronalNetworkTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };


  private void addTextwatcher() {
    appPortEditText.addTextChangedListener(appPortTextWatcher);
    boardPortEditText.addTextChangedListener(boardPortTextWatcher);
    ipAdresseEditText.addTextChangedListener(ipAdressTextWatcher);

    serverIpAdressEditText.addTextChangedListener(serverIpTextWatcher);
    serverUserEditText.addTextChangedListener(serverUserTextWatcher);
    serverPasswordEditText.addTextChangedListener(serverPasswordTextWatcher);
    serverPortEditText.addTextChangedListener(serverPortTextWatcher);
  }

  public void setSquarePositions(RelativeLayout.LayoutParams params) {
    RelativeLayout.LayoutParams lpUL
            = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
    lpUL.leftMargin = params.leftMargin;
    lpUL.topMargin = params.topMargin;

    RelativeLayout.LayoutParams lpBL
            = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();
    lpBL.leftMargin = params.leftMargin;
    lpBL.topMargin = params.topMargin + params.height - squareSize;

    RelativeLayout.LayoutParams lpBR
            = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();
    lpBR.topMargin = params.topMargin + params.height - squareSize;
    lpBR.leftMargin = params.leftMargin + params.width - squareSize;

    RelativeLayout.LayoutParams lpUR
            = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();
    lpUR.topMargin = params.topMargin;
    lpUR.leftMargin = params.leftMargin + params.width - squareSize;

    upperLeftSquareImageView.setLayoutParams(lpUL);
    bottomLeftSquareImageView.setLayoutParams(lpBL);
    bottomRightSquareImageView.setLayoutParams(lpBR);
    upperRightSquareImageView.setLayoutParams(lpUR);
  }

  public void updateAreaOfInterestText() {
    RelativeLayout.LayoutParams lpLO
            = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
    RelativeLayout.LayoutParams lpLU
            = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();
    RelativeLayout.LayoutParams lpRO
            = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();
    xfactor = (Float.parseFloat(chosenResolution[1]) / (float) maxLeftMargin);
    yfactor = (Float.parseFloat(chosenResolution[0]) / (float) maxTopMargin);
    int x = (int) ((lpRO.leftMargin - lpLO.leftMargin) * xfactor);
    ;
    int y = (int) ((lpLU.topMargin - lpLO.topMargin) * yfactor);

    resolutionTextview
            .setText(getString(R.string.areaOfInterestText) + " " + y + "x" + x + " pixels");
  }

  public void setPartnerPositionsSquares(String name, int left, int top) {
    RelativeLayout.LayoutParams lp1;
    RelativeLayout.LayoutParams lp2;
    RelativeLayout.LayoutParams lpH
            = (RelativeLayout.LayoutParams) backgroundRectangleImageView.getLayoutParams();

    switch (name) {
      case "imageView_lo":
        lp1 = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();

        setRectangleBackgroundCameraView();

        lp1.topMargin = top;
        lp2.leftMargin = left;
        upperRightSquareImageView.setLayoutParams(lp1);
        bottomLeftSquareImageView.setLayoutParams(lp2);
        backgroundRectangleImageView.setLayoutParams(lpH);
        break;
      case "imageView_lu":
        lp1 = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();

        setRectangleBackgroundCameraView();

        lp1.leftMargin = left;
        lp2.topMargin = top;
        upperLeftSquareImageView.setLayoutParams(lp1);
        bottomRightSquareImageView.setLayoutParams(lp2);
        break;
      case "imageView_ru":
        lp1 = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();

        setRectangleBackgroundCameraView();


        lp1.topMargin = top;
        lp2.leftMargin = left;
        bottomLeftSquareImageView.setLayoutParams(lp1);
        upperRightSquareImageView.setLayoutParams(lp2);
        break;
      case "imageView_ro":
        lp1 = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();

        setRectangleBackgroundCameraView();

        lp1.topMargin = top;
        lp2.leftMargin = left;
        upperLeftSquareImageView.setLayoutParams(lp1);
        bottomRightSquareImageView.setLayoutParams(lp2);
        break;
      default:
        break;
    }
    partnerDistanceControlSquares(name);
  }

  public void setRectangleBackgroundCameraView() {
    RelativeLayout.LayoutParams lpUL;
    RelativeLayout.LayoutParams lpBR;
    RelativeLayout.LayoutParams lpBackground
            = (RelativeLayout.LayoutParams) backgroundRectangleImageView.getLayoutParams();

    lpUL = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
    lpBR = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();

    lpBackground.topMargin = lpUL.topMargin;
    lpBackground.leftMargin = lpUL.leftMargin;
    lpBackground.height = lpBR.topMargin - lpUL.topMargin + squareSize;
    lpBackground.width = lpBR.leftMargin - lpUL.leftMargin + squareSize;
    backgroundRectangleImageView.setLayoutParams(lpBackground);
  }

  public void partnerDistanceControlSquares(String name) {
    RelativeLayout.LayoutParams lp;
    RelativeLayout.LayoutParams lp1;
    RelativeLayout.LayoutParams lp2;

    switch (name) {
      case "imageView_lo":
        lp = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
        lp1 = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();

        if (lp2.topMargin - lp.topMargin < squareSize) {
          lp.topMargin = lp2.topMargin - squareSize;
          lp1.topMargin = lp2.topMargin - squareSize;
        }
        if (lp1.leftMargin - lp.leftMargin < squareSize) {
          lp.leftMargin = lp1.leftMargin - squareSize;
          lp2.leftMargin = lp1.leftMargin - squareSize;
        }

        upperLeftSquareImageView.setLayoutParams(lp);
        upperRightSquareImageView.setLayoutParams(lp1);
        bottomLeftSquareImageView.setLayoutParams(lp2);
        break;
      case "imageView_lu":
        lp = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();
        lp1 = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();

        if (lp.topMargin - lp1.topMargin < squareSize) {
          lp.topMargin = lp1.topMargin + squareSize;
          lp2.topMargin = lp.topMargin;
        }

        if (lp2.leftMargin - lp.leftMargin < squareSize) {
          lp.leftMargin = lp2.leftMargin - squareSize;
          lp1.leftMargin = lp.leftMargin;
        }

        bottomLeftSquareImageView.setLayoutParams(lp);
        upperLeftSquareImageView.setLayoutParams(lp1);
        bottomRightSquareImageView.setLayoutParams(lp2);
        break;
      case "imageView_ru":
        lp = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();
        lp1 = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();

        if (lp.topMargin - lp2.topMargin < squareSize) {
          lp.topMargin = lp2.topMargin + squareSize;
          lp1.topMargin = lp.topMargin;
        }

        if (lp.leftMargin - lp1.leftMargin < squareSize) {
          lp.leftMargin = lp1.leftMargin + squareSize;
          lp2.leftMargin = lp.leftMargin;
        }

        bottomRightSquareImageView.setLayoutParams(lp);
        bottomLeftSquareImageView.setLayoutParams(lp1);
        upperRightSquareImageView.setLayoutParams(lp2);
        break;
      case "imageView_ro":
        lp = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();
        lp1 = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
        lp2 = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();

        if (lp2.topMargin - lp.topMargin < squareSize) {
          lp.topMargin = lp2.topMargin - squareSize;
          lp1.topMargin = lp.topMargin;
        }

        if (lp.leftMargin - lp1.leftMargin < squareSize) {
          lp.leftMargin = lp1.leftMargin + squareSize;
          lp2.leftMargin = lp.leftMargin;
        }

        upperRightSquareImageView.setLayoutParams(lp);
        upperLeftSquareImageView.setLayoutParams(lp1);
        bottomRightSquareImageView.setLayoutParams(lp2);
        break;
      default:
        break;
    }
  }

  public void deleteChosenDataset(View v) {
    if (chosenTraindataset != null) {
      String datasetName = chosenTraindataset.getName();
      chosenTraindataset.delete();
      updateDatasetSpinner();
      Snackbar.make(v, "Dataset " + datasetName + "deleted!", Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    } else {
      Snackbar.make(v, "No datasets on the smartphone!", Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    }

  }

  public void deleteNeuronalesNetz(View v) {
    if (currentSettings.getFileChosenNN() != null) {
      currentSettings.getFileChosenNN().delete();
      updateNeuronalNetworksSpinner();
      Snackbar.make(v, "Neuronal network deleted!", Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    } else {
      Snackbar.make(v, "No Neuronal network on the smartphone!", Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    }

  }

  private void updateSettings() {
    cameraSizesSpinner.setSelection(currentSettings.getResolutionPosition());
    chosenResolution = ((String) cameraSizesSpinner.getSelectedItem()).split("x");
    ipAdresseEditText.setText(currentSettings.getIpAdress());
    appPortEditText.setText("" + currentSettings.getAppPort());
    boardPortEditText.setText("" + currentSettings.getBoardPort());

    serverIpAdressEditText.setText(currentSettings.getServerIpAdress());
    serverUserEditText.setText(currentSettings.getServerUser());
    serverPasswordEditText.setText(currentSettings.getServerPassword());
    serverPortEditText.setText("" + currentSettings.getServerPort());

    //Upper Left
    RelativeLayout.LayoutParams lp_lo
            = (RelativeLayout.LayoutParams) upperLeftSquareImageView.getLayoutParams();
    lp_lo.leftMargin = currentSettings.getLoLeftMargin();
    lp_lo.topMargin = currentSettings.getLoTopMargin();
    lp_lo.rightMargin = standardMargin;
    lp_lo.bottomMargin = standardMargin;
    upperLeftSquareImageView.setLayoutParams(lp_lo);

    //Bottom Left
    RelativeLayout.LayoutParams lp_lu
            = (RelativeLayout.LayoutParams) bottomLeftSquareImageView.getLayoutParams();
    lp_lu.leftMargin = currentSettings.getLoLeftMargin();
    lp_lu.topMargin = currentSettings.getRuTopMargin();
    lp_lu.rightMargin = standardMargin;
    lp_lu.bottomMargin = standardMargin;
    bottomLeftSquareImageView.setLayoutParams(lp_lu);

    //Bottom Right
    RelativeLayout.LayoutParams lp_ru
            = (RelativeLayout.LayoutParams) bottomRightSquareImageView.getLayoutParams();
    lp_ru.leftMargin = currentSettings.getRuLeftMargin();
    lp_ru.topMargin = currentSettings.getRuTopMargin();
    lp_ru.rightMargin = standardMargin;
    lp_ru.bottomMargin = standardMargin;
    bottomRightSquareImageView.setLayoutParams(lp_ru);

    //Upper Right
    RelativeLayout.LayoutParams lp_ro
            = (RelativeLayout.LayoutParams) upperRightSquareImageView.getLayoutParams();
    lp_ro.leftMargin = currentSettings.getRuLeftMargin();
    lp_ro.topMargin = currentSettings.getLoTopMargin();
    lp_ro.rightMargin = standardMargin;
    lp_ro.bottomMargin = standardMargin;
    upperRightSquareImageView.setLayoutParams(lp_ro);

    //Background
    RelativeLayout.LayoutParams lp_h
            = (RelativeLayout.LayoutParams) backgroundRectangleImageView.getLayoutParams();
    lp_h.leftMargin = lp_lo.leftMargin;
    lp_h.topMargin = lp_lo.topMargin;
    lp_h.height = lp_ru.topMargin - lp_lo.topMargin + squareSize;
    lp_h.width = lp_ru.leftMargin - lp_lo.leftMargin + squareSize;
    backgroundRectangleImageView.setLayoutParams(lp_h);
  }

  public void startSaveSettingsOnSftpServerDialog(View view) {
    if (!statusSftpConnection) {
      Snackbar.make(view, getString(R.string.noSftpConnection), Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
      return;
    }
    currentSettings.saveSettings();
    new AsyncTask<Integer, Void, Void>(){
      @Override
      protected Void doInBackground(Integer... params) {
        try {
          createServerSubDirectory(serverUser, serverPassword, serverIp, serverPort, "Settings");
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    }.execute(1);
    dialogSettingsName.startDialog();
  }
}
