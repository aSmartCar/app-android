package com.example.autonomesfahrzeug;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;


/**
 * The type Current settings.
 */
public class CurrentSettings {

  private static CurrentSettings currentSettings;

  private int picturesPerSecond = 20;
  private int boardPort = 10001;
  private int appPort = 10000;
  private int width;
  private int height;
  private int resolutionPosition;
  private int loLeftMargin;
  private int loTopMargin;
  private int ruLeftMargin;
  private int ruTopMargin;
  private int serverPort = 22;

  private boolean uploadAutomatically = false;
  private boolean withLense = false;
  private boolean saveSession = true;
  private boolean withWifi = false;
  private boolean mainMenu = false;
  private boolean manualMode = false;
  private boolean autoMode = false;
  private boolean settings = false;
  private boolean changeWifi = false;

  private String serverIpAdress = "192.168.11.34";
  private String serverUser = "asv_user";
  private String serverPassword = "12345678";
  private String serverHomePath = "";
  private String serverTrainPath = "";
  private String serverAppPath = "";
  private String ipAdress = "192.168.4.1";
  private String cameraID = "0";
  private String chosenNN;
  private String appRootPath;
  private String datasetsPath;
  private String neuronalNetworksPath;
  private String settingsPath;
  private String applicationName = "Praxisprojekt";
  private String wifiSsid = "";
  private String wifiPassword = "";
  private String accessPointSsid = "";
  private String accessPointPassword = "";
  private String wifiMode = "";
  private String lenseName = "";

  private File fileChosenNN;
  private File folderChosenNN;
  private File fileRoot;
  private File fileDatasets;
  private File fileNeuronalNetworks;
  private File fileSettings;


  private CurrentSettings(AppCompatActivity activity) {
    appRootPath = activity.getExternalFilesDir(null).getAbsolutePath() + File.separator
            + "Autonomes Fahrzeug" + File.separator;
    datasetsPath = appRootPath + "Datasets" + File.separator;
    neuronalNetworksPath = appRootPath + "NeuronalNetworks" + File.separator;
    settingsPath = appRootPath + "Settings" + File.separator;
    fileRoot = new File(appRootPath);
    if (!fileRoot.exists()) {
      fileRoot.mkdirs();
    }
    fileDatasets = new File(datasetsPath);
    if (!fileDatasets.exists()) {
      fileDatasets.mkdirs();
    }
    fileNeuronalNetworks = new File(neuronalNetworksPath);
    if (!fileNeuronalNetworks.exists()) {
      fileNeuronalNetworks.mkdirs();
    }
    fileSettings = new File(settingsPath);
    if (!fileSettings.exists()) {
      fileSettings.mkdirs();
    }
    loadSettings();
  }

  /**
   * Gets lense name.
   *
   * @return the lense name
   */
  public String getLenseName() {
    return lenseName;
  }

  /**
   * Sets lense name.
   *
   * @param lenseName the lense name
   */
  public void setLenseName(String lenseName) {
    this.lenseName = lenseName;
  }

  /**
   * Gets wifi mode.
   *
   * @return the wifi mode
   */
  public String getWifiMode() {
    return wifiMode;
  }

  /**
   * Sets wifi mode.
   *
   * @param wifiMode the wifi mode
   */
  public void setWifiMode(String wifiMode) {
    this.wifiMode = wifiMode;
  }

  /**
   * Gets server train path.
   *
   * @return the server train path
   */
  public String getServerTrainPath() {
    return serverTrainPath;
  }

  /**
   * Sets server train path.
   *
   * @param serverTrainPath the server train path
   */
  public void setServerTrainPath(String serverTrainPath) {
    this.serverTrainPath = serverTrainPath;
  }

  /**
   * Gets wifi ssid.
   *
   * @return the wifi ssid
   */
  public String getWifiSsid() {
    return wifiSsid;
  }

  /**
   * Sets wifi ssid.
   *
   * @param wifiSsid the wifi ssid
   */
  public void setWifiSsid(String wifiSsid) {
    this.wifiSsid = wifiSsid;
  }

  /**
   * Gets wifi password.
   *
   * @return the wifi password
   */
  public String getWifiPassword() {
    return wifiPassword;
  }

  /**
   * Sets wifi password.
   *
   * @param wifiPassword the wifi password
   */
  public void setWifiPassword(String wifiPassword) {
    this.wifiPassword = wifiPassword;
  }

  /**
   * Gets access point ssid.
   *
   * @return the access point ssid
   */
  public String getAccessPointSsid() {
    return accessPointSsid;
  }

  /**
   * Sets access point ssid.
   *
   * @param accessPointSsid the access point ssid
   */
  public void setAccessPointSsid(String accessPointSsid) {
    this.accessPointSsid = accessPointSsid;
  }

  /**
   * Gets access point password.
   *
   * @return the access point password
   */
  public String getAccessPointPassword() {
    return accessPointPassword;
  }

  /**
   * Sets access point password.
   *
   * @param accessPointPassword the access point password
   */
  public void setAccessPointPassword(String accessPointPassword) {
    this.accessPointPassword = accessPointPassword;
  }

  /**
   * Get server port int.
   *
   * @return the int
   */
  public int getServerPort(){
    return serverPort;
  }

  /**
   * Sets server port.
   *
   * @param serverPort the server port
   */
  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Gets server home path.
   *
   * @return the server home path
   */
  public String getServerHomePath() {
    return serverHomePath;
  }

  /**
   * Sets server home path.
   *
   * @param serverHomePath the server home path
   */
  public void setServerHomePath(String serverHomePath) {
    this.serverHomePath = serverHomePath;
  }

  /**
   * Gets server app path.
   *
   * @return the server app path
   */
  public String getServerAppPath() {
    return serverAppPath;
  }

  /**
   * Sets server app path.
   *
   * @param serverAppPath the server app path
   */
  public void setServerAppPath(String serverAppPath) {
    this.serverAppPath = serverAppPath;
  }

  /**
   * Gets server ip adress.
   *
   * @return the server ip adress
   */
  public String getServerIpAdress() {
    return serverIpAdress;
  }

  /**
   * Sets server ip adress.
   *
   * @param serverIpAdress the server ip adress
   */
  public void setServerIpAdress(String serverIpAdress) {
    this.serverIpAdress = serverIpAdress;
  }

  /**
   * Gets server user.
   *
   * @return the server user
   */
  public String getServerUser() {
    return serverUser;
  }

  /**
   * Sets server user.
   *
   * @param serverUser the server user
   */
  public void setServerUser(String serverUser) {
    this.serverUser = serverUser;
  }

  /**
   * Gets server password.
   *
   * @return the server password
   */
  public String getServerPassword() {
    return serverPassword;
  }

  /**
   * Sets server password.
   *
   * @param serverPassword the server password
   */
  public void setServerPassword(String serverPassword) {
    this.serverPassword = serverPassword;
  }

  /**
   * Gets folder chosen nn.
   *
   * @return the folder chosen nn
   */
  public File getFolderChosenNN() {
    return folderChosenNN;
  }

  /**
   * Sets folder chosen nn.
   *
   * @param folderChosenNN the folder chosen nn
   */
  public void setFolderChosenNN(File folderChosenNN) {
    this.folderChosenNN = folderChosenNN;
  }

  /**
   * Is change wifi boolean.
   *
   * @return the boolean
   */
  public boolean isChangeWifi() {
    return changeWifi;
  }

  /**
   * Sets change wifi.
   *
   * @param changeWifi the change wifi
   */
  public void setChangeWifi(boolean changeWifi) {
    this.changeWifi = changeWifi;
  }

  /**
   * Gets file datasets.
   *
   * @return the file datasets
   */
  public File getFileDatasets() {
    return fileDatasets;
  }

  /**
   * Sets file datasets.
   *
   * @param fileDatasets the file datasets
   */
  public void setFileDatasets(File fileDatasets) {
    this.fileDatasets = fileDatasets;
  }

  /**
   * Gets neuronal networks path.
   *
   * @return the neuronal networks path
   */
  public String getNeuronalNetworksPath() {
    return neuronalNetworksPath;
  }

  /**
   * Gets datasets path.
   *
   * @return the datasets path
   */
  public String getDatasetsPath() {
    return datasetsPath;
  }

  /**
   * Gets file neuronal networks.
   *
   * @return the file neuronal networks
   */
  public File getFileNeuronalNetworks() {
    return fileNeuronalNetworks;
  }

  /**
   * Sets file neuronal networks.
   *
   * @param fileNeuronalNetworks the file neuronal networks
   */
  public void setFileNeuronalNetworks(File fileNeuronalNetworks) {
    this.fileNeuronalNetworks = fileNeuronalNetworks;
  }

  /**
   * Gets file settings.
   *
   * @return the file settings
   */
  public File getFileSettings() {
    return fileSettings;
  }

  /**
   * Sets file settings.
   *
   * @param fileSettings the file settings
   */
  public void setFileSettings(File fileSettings) {
    this.fileSettings = fileSettings;
  }

  /**
   * returns an instance of CurrentsSettings.
   *
   * @param activity Activity
   * @return instance current settings
   */
  public static CurrentSettings getCurrentSettings(AppCompatActivity activity) {
    if (currentSettings != null) {
      return currentSettings;
    } else {
      currentSettings = new CurrentSettings(activity);
      return currentSettings;
    }
  }

  /**
   * Gets pictures per second.
   *
   * @return the pictures per second
   */
  public int getPicturesPerSecond() {
    return picturesPerSecond;
  }

  /**
   * Sets pictures per second.
   *
   * @param picturesPerSecond the pictures per second
   */
  public void setPicturesPerSecond(int picturesPerSecond) {
    this.picturesPerSecond = picturesPerSecond;
  }

  /**
   * Gets board port.
   *
   * @return the board port
   */
  public int getBoardPort() {
    return boardPort;
  }

  /**
   * Sets board port.
   *
   * @param boardPort the board port
   */
  public void setBoardPort(int boardPort) {
    this.boardPort = boardPort;
  }

  /**
   * Gets app port.
   *
   * @return the app port
   */
  public int getAppPort() {
    return appPort;
  }

  /**
   * Sets app port.
   *
   * @param appPort the app port
   */
  public void setAppPort(int appPort) {
    this.appPort = appPort;
  }

  /**
   * Gets width.
   *
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets width.
   *
   * @param width the width
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Gets height.
   *
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Sets height.
   *
   * @param height the height
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Gets resolution position.
   *
   * @return the resolution position
   */
  public int getResolutionPosition() {
    return resolutionPosition;
  }

  /**
   * Sets resolution position.
   *
   * @param resolutionPosition the resolution position
   */
  public void setResolutionPosition(int resolutionPosition) {
    this.resolutionPosition = resolutionPosition;
  }

  /**
   * Gets lo left margin.
   *
   * @return the lo left margin
   */
  public int getLoLeftMargin() {
    return loLeftMargin;
  }

  /**
   * Sets lo left margin.
   *
   * @param loLeftMargin the lo left margin
   */
  public void setLoLeftMargin(int loLeftMargin) {
    this.loLeftMargin = loLeftMargin;
  }

  /**
   * Gets lo top margin.
   *
   * @return the lo top margin
   */
  public int getLoTopMargin() {
    return loTopMargin;
  }

  /**
   * Sets lo top margin.
   *
   * @param loTopMargin the lo top margin
   */
  public void setLoTopMargin(int loTopMargin) {
    this.loTopMargin = loTopMargin;
  }

  /**
   * Gets ru left margin.
   *
   * @return the ru left margin
   */
  public int getRuLeftMargin() {
    return ruLeftMargin;
  }

  /**
   * Sets ru left margin.
   *
   * @param ruLeftMargin the ru left margin
   */
  public void setRuLeftMargin(int ruLeftMargin) {
    this.ruLeftMargin = ruLeftMargin;
  }

  /**
   * Gets ru top margin.
   *
   * @return the ru top margin
   */
  public int getRuTopMargin() {
    return ruTopMargin;
  }

  /**
   * Sets ru top margin.
   *
   * @param ruTopMargin the ru top margin
   */
  public void setRuTopMargin(int ruTopMargin) {
    this.ruTopMargin = ruTopMargin;
  }

  /**
   * Is upload automatically boolean.
   *
   * @return the boolean
   */
  public boolean isUploadAutomatically() {
    return uploadAutomatically;
  }

  /**
   * Sets upload automatically.
   *
   * @param uploadAutomatically the upload automatically
   */
  public void setUploadAutomatically(boolean uploadAutomatically) {
    this.uploadAutomatically = uploadAutomatically;
  }

  /**
   * Is with lense boolean.
   *
   * @return the boolean
   */
  public boolean isWithLense() {
    return withLense;
  }

  /**
   * Sets with lense.
   *
   * @param withLense the with lense
   */
  public void setWithLense(boolean withLense) {
    this.withLense = withLense;
  }

  /**
   * Is save session boolean.
   *
   * @return the boolean
   */
  public boolean isSaveSession() {
    return saveSession;
  }

  /**
   * Sets save session.
   *
   * @param saveSession the save session
   */
  public void setSaveSession(boolean saveSession) {
    this.saveSession = saveSession;
  }

  /**
   * Is with wifi boolean.
   *
   * @return the boolean
   */
  public boolean isWithWifi() {
    return withWifi;
  }

  /**
   * Sets with wifi.
   *
   * @param withWifi the with wifi
   */
  public void setWithWifi(boolean withWifi) {
    this.withWifi = withWifi;
  }

  /**
   * Gets ip adress.
   *
   * @return the ip adress
   */
  public String getIpAdress() {
    return ipAdress;
  }

  /**
   * Sets ip adress.
   *
   * @param ipAdress the ip adress
   */
  public void setIpAdress(String ipAdress) {
    this.ipAdress = ipAdress;
  }

  /**
   * Gets camera id.
   *
   * @return the camera id
   */
  public String getCameraID() {
    return cameraID;
  }

  /**
   * Sets camera id.
   *
   * @param cameraID the camera id
   */
  public void setCameraID(String cameraID) {
    this.cameraID = cameraID;
  }

  /**
   * Gets chosen nn.
   *
   * @return the chosen nn
   */
  public String getChosenNN() {
    return chosenNN;
  }

  /**
   * Sets chosen nn.
   *
   * @param chosenNN the chosen nn
   */
  public void setChosenNN(String chosenNN) {
    this.chosenNN = chosenNN;
  }

  /**
   * Gets app root path.
   *
   * @return the app root path
   */
  public String getAppRootPath() {
    return appRootPath;
  }

  /**
   * Sets app root path.
   *
   * @param appRootPath the app root path
   */
  public void setAppRootPath(String appRootPath) {
    this.appRootPath = appRootPath;
  }

  /**
   * Gets file chosen nn.
   *
   * @return the file chosen nn
   */
  public File getFileChosenNN() {
    return fileChosenNN;
  }

  /**
   * Sets file chosen nn.
   *
   * @param fileChosenNN the file chosen nn
   */
  public void setFileChosenNN(File fileChosenNN) {
    this.fileChosenNN = fileChosenNN;
  }

  /*
  public GoogleSignInAccount getGoogleAccount() {
    return googleAccount;
  }

  public void setGoogleAccount(GoogleSignInAccount googleAccount) {
    this.googleAccount = googleAccount;
  }
  */

  /**
   * Is main menu boolean.
   *
   * @return the boolean
   */
  public boolean isMainMenu() {
    return mainMenu;
  }

  /**
   * Is set when User is in Main Menu.
   *
   * @param mainMenu is in Main Menu
   */
  public void setMainMenu(boolean mainMenu) {
    this.mainMenu = mainMenu;
    if (!manualMode && !autoMode && !mainMenu && !settings) {
      saveSettings();
    }
  }

  /**
   * Is manual mode boolean.
   *
   * @return the boolean
   */
  public boolean isManualMode() {
    return manualMode;
  }

  /**
   * Is set when User is in Manual Mode.
   *
   * @param manualMode is in Manual Mode
   */
  public void setManualMode(boolean manualMode) {
    this.manualMode = manualMode;
    if (!manualMode && !autoMode && !mainMenu && !settings) {
      saveSettings();
    }
  }

  /**
   * Is auto mode boolean.
   *
   * @return the boolean
   */
  public boolean isAutoMode() {
    return autoMode;
  }

  /**
   * Is set when User is in Auto Mode.
   *
   * @param autoMode is in Auto Mode
   */
  public void setAutoMode(boolean autoMode) {
    this.autoMode = autoMode;
    if (!manualMode && !autoMode && !mainMenu && !settings) {
      saveSettings();
    }
  }

  /**
   * Is settings boolean.
   *
   * @return the boolean
   */
  public boolean isSettings() {
    return settings;
  }

  /**
   * Is set when User is in Settings.
   *
   * @param settings is in Settings
   */
  public void setSettings(boolean settings) {
    this.settings = settings;
    if (!manualMode && !autoMode && !mainMenu && !settings) {
      saveSettings();
    }
  }

  /**
   * Gets settings path.
   *
   * @return the settings path
   */
  public String getSettingsPath() {
    return settingsPath;
  }

  /**
   * Sets settings path.
   *
   * @param settingsPath the settings path
   */
  public void setSettingsPath(String settingsPath) {
    this.settingsPath = settingsPath;
  }


  /*
  public GoogleDriveHelper getGoogleDriveHelper() {
    return googleDriveHelper;
  }

  public void setGoogleDriveHelper(GoogleDriveHelper googleDriveHelper) {
    this.googleDriveHelper = googleDriveHelper;
  }
  */

  /**
   * Returns Settings as JSONObject.
   *
   * @return JSONObject json object
   */
  public JSONObject toJsonObject() {
    ArrayList<JsonEntry> entries = new ArrayList<>();

    // Booleans
    entries.add(new JsonEntry<>(JsonKey.uploadautomatically, uploadAutomatically));
    entries.add(new JsonEntry<>(JsonKey.with_lense, withLense));
    entries.add(new JsonEntry<>(JsonKey.wifi_only, withWifi));
    entries.add(new JsonEntry<>(JsonKey.save_session, saveSession));
    entries.add(new JsonEntry<>(JsonKey.changeWifi, changeWifi));

    //Integer
    entries.add(new JsonEntry<>(JsonKey.appPort, appPort));
    entries.add(new JsonEntry<>(JsonKey.boardPort, boardPort));
    entries.add(new JsonEntry<>(JsonKey.resolutionHeight, height));
    entries.add(new JsonEntry<>(JsonKey.resolutionWidth, width));
    entries.add(new JsonEntry<>(JsonKey.loLeft, loLeftMargin));
    entries.add(new JsonEntry<>(JsonKey.loTop, loTopMargin));
    entries.add(new JsonEntry<>(JsonKey.picturePerSecond, picturesPerSecond));
    entries.add(new JsonEntry<>(JsonKey.resolutionPosition, resolutionPosition));
    entries.add(new JsonEntry<>(JsonKey.ruLeft, ruLeftMargin));
    entries.add(new JsonEntry<>(JsonKey.ruTop, ruTopMargin));
    entries.add(new JsonEntry<>(JsonKey.serverPort, serverPort));

    // Strings
    entries.add(new JsonEntry<>(JsonKey.cameraID, cameraID));
    entries.add(new JsonEntry<>(JsonKey.chosenNN, chosenNN));
    entries.add(new JsonEntry<>(JsonKey.ipAddres, ipAdress));
    entries.add(new JsonEntry<>(JsonKey.serverIpAdress, serverIpAdress));
    entries.add(new JsonEntry<>(JsonKey.serverUser, serverUser));
    entries.add(new JsonEntry<>(JsonKey.serverPassword, serverPassword));
    entries.add(new JsonEntry<>(JsonKey.wifiSsid, wifiSsid));
    entries.add(new JsonEntry<>(JsonKey.wifiPassword, wifiPassword));
    entries.add(new JsonEntry<>(JsonKey.accessPointSsid, accessPointSsid));
    entries.add(new JsonEntry<>(JsonKey.accessPointPassword, accessPointPassword));
    entries.add(new JsonEntry<>(JsonKey.serverTrainPath, serverTrainPath));
    entries.add(new JsonEntry<>(JsonKey.serverAppPath, serverAppPath));
    entries.add(new JsonEntry<>(JsonKey.serverHomePath, serverHomePath));
    entries.add(new JsonEntry<>(JsonKey.wifiMode, wifiMode));
    entries.add(new JsonEntry<>(JsonKey.lenseName, lenseName));


    // Files
    if (fileChosenNN != null) {
      entries.add(new JsonEntry<>(JsonKey.file_chosen_NN, fileChosenNN));
    }
    if (folderChosenNN != null) {
      entries.add(new JsonEntry<>(JsonKey.folder_chosen_NN, folderChosenNN.getPath()));
    }

    return JsonFile.createJsonObject(entries);
  }


  /**
   * loads settings.
   */
  public void loadSettings() {
    JsonElement jsonElement = JsonFile.loadSettingsJsonElement(fileSettings.getPath(),
            "Settings.json");
    if (jsonElement != null) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();

      //Booleans
      if (jsonObject.has(JsonKey.uploadautomatically)) {
        uploadAutomatically = jsonObject.get(JsonKey.uploadautomatically).getAsBoolean();
      }
      if (jsonObject.has(JsonKey.with_lense)) {
        withLense = jsonObject.get(JsonKey.with_lense).getAsBoolean();
      }
      if (jsonObject.has(JsonKey.wifi_only)) {
        withWifi = jsonObject.get(JsonKey.wifi_only).getAsBoolean();
      }
      if (jsonObject.has(JsonKey.save_session)) {
        saveSession = jsonObject.get(JsonKey.save_session).getAsBoolean();
      }
      if (jsonObject.has(JsonKey.changeWifi)) {
        changeWifi = jsonObject.get(JsonKey.changeWifi).getAsBoolean();
      }

      //Integer
      if (jsonObject.has(JsonKey.appPort)) {
        appPort = jsonObject.get(JsonKey.appPort).getAsInt();
      }
      if (jsonObject.has(JsonKey.boardPort)) {
        boardPort = jsonObject.get(JsonKey.boardPort).getAsInt();
      }
      if (jsonObject.has(JsonKey.resolutionHeight)) {
        height = jsonObject.get(JsonKey.resolutionHeight).getAsInt();
      }
      if (jsonObject.has(JsonKey.resolutionWidth)) {
        width = jsonObject.get(JsonKey.resolutionWidth).getAsInt();
      }
      if (jsonObject.has(JsonKey.loLeft)) {
        loLeftMargin = jsonObject.get(JsonKey.loLeft).getAsInt();
      }
      if (jsonObject.has(JsonKey.loTop)) {
        loTopMargin = jsonObject.get(JsonKey.loTop).getAsInt();
      }
      if (jsonObject.has(JsonKey.ruLeft)) {
        ruLeftMargin = jsonObject.get(JsonKey.ruLeft).getAsInt();
      }
      if (jsonObject.has(JsonKey.ruTop)) {
        ruTopMargin = jsonObject.get(JsonKey.ruTop).getAsInt();
      }
      if (jsonObject.has(JsonKey.picturePerSecond)) {
        picturesPerSecond = jsonObject.get(JsonKey.picturePerSecond).getAsInt();
      }
      if (jsonObject.has(JsonKey.resolutionPosition)) {
        resolutionPosition = jsonObject.get(JsonKey.resolutionPosition).getAsInt();
      }
      if (jsonObject.has(JsonKey.serverPort)) {
        serverPort = jsonObject.get(JsonKey.serverPort).getAsInt();
      }

      //Strings
      if (jsonObject.has(JsonKey.cameraID)) {
        cameraID = jsonObject.get(JsonKey.cameraID).getAsString();
      }
      if (jsonObject.has(JsonKey.chosenNN)) {
        chosenNN = jsonObject.get(JsonKey.chosenNN).getAsString();
      }
      if (jsonObject.has(JsonKey.ipAddres)) {
        ipAdress = jsonObject.get(JsonKey.ipAddres).getAsString();
      }
      if (jsonObject.has(JsonKey.serverIpAdress)) {
        serverIpAdress = jsonObject.get(JsonKey.serverIpAdress).getAsString();
      }
      if (jsonObject.has(JsonKey.serverUser)) {
        serverUser = jsonObject.get(JsonKey.serverUser).getAsString();
      }
      if (jsonObject.has(JsonKey.serverPassword)) {
        serverPassword = jsonObject.get(JsonKey.serverPassword).getAsString();
      }
      if (jsonObject.has(JsonKey.wifiSsid)) {
        wifiSsid = jsonObject.get(JsonKey.wifiSsid).getAsString();
      }
      if (jsonObject.has(JsonKey.wifiPassword)) {
        wifiPassword = jsonObject.get(JsonKey.wifiPassword).getAsString();
      }
      if (jsonObject.has(JsonKey.accessPointSsid)) {
        accessPointSsid = jsonObject.get(JsonKey.accessPointSsid).getAsString();
      }
      if (jsonObject.has(JsonKey.accessPointPassword)) {
        accessPointPassword = jsonObject.get(JsonKey.accessPointPassword).getAsString();
      }
      if (jsonObject.has(JsonKey.serverTrainPath)) {
        serverTrainPath = jsonObject.get(JsonKey.serverTrainPath).getAsString();
      }
      if (jsonObject.has(JsonKey.serverAppPath)) {
        serverAppPath = jsonObject.get(JsonKey.serverAppPath).getAsString();
      }
      if (jsonObject.has(JsonKey.serverHomePath)) {
        serverHomePath = jsonObject.get(JsonKey.serverHomePath).getAsString();
      }
      if (jsonObject.has(JsonKey.lenseName)) {
        lenseName = jsonObject.get(JsonKey.lenseName).getAsString();
      }

      //Files
      if (jsonObject.has(JsonKey.file_chosen_NN)) {
        fileChosenNN = new File(jsonObject.get(JsonKey.file_chosen_NN).getAsString());
      }
      if (jsonObject.has(JsonKey.folder_chosen_NN)) {
        folderChosenNN = new File(jsonObject.get(JsonKey.folder_chosen_NN).getAsString());
      }
    } else {
      cameraID = "0";
      picturesPerSecond = 10;
      ruTopMargin = 594;
      ruLeftMargin = 674;
      loTopMargin = 214;
      loLeftMargin = 87;
      width = 240;
      height = 320;
    }
  }


  /**
   * Loads settings for auto mode.
   */
  public void loadAutoModeSettings() {
    JsonElement jsonElement = JsonFile.loadSettingsJsonElement(folderChosenNN.getPath()
            + File.separator + chosenNN + File.separator, "index.json");
    if (jsonElement != null) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();

      //Booleans
      if (jsonObject.has(JsonKey.with_lense)) {
        withLense = jsonObject.get(JsonKey.with_lense).getAsBoolean();
      }

      //Integer
      if (jsonObject.has(JsonKey.resolutionHeight)) {
        height = jsonObject.get(JsonKey.resolutionHeight).getAsInt();
      }
      if (jsonObject.has(JsonKey.resolutionWidth)) {
        width = jsonObject.get(JsonKey.resolutionWidth).getAsInt();
      }
      if (jsonObject.has(JsonKey.loLeft)) {
        loLeftMargin = jsonObject.get(JsonKey.loLeft).getAsInt();
      }
      if (jsonObject.has(JsonKey.loTop)) {
        loTopMargin = jsonObject.get(JsonKey.loTop).getAsInt();
      }
      if (jsonObject.has(JsonKey.ruLeft)) {
        ruLeftMargin = jsonObject.get(JsonKey.ruLeft).getAsInt();
      }
      if (jsonObject.has(JsonKey.ruTop)) {
        ruTopMargin = jsonObject.get(JsonKey.ruTop).getAsInt();
      }
      if (jsonObject.has(JsonKey.picturePerSecond)) {
        picturesPerSecond = jsonObject.get(JsonKey.picturePerSecond).getAsInt();
      }
      if (jsonObject.has(JsonKey.resolutionPosition)) {
        resolutionPosition = jsonObject.get(JsonKey.resolutionPosition).getAsInt();
      }

      //Strings
      if (jsonObject.has(JsonKey.cameraID)) {
        cameraID = jsonObject.get(JsonKey.cameraID).getAsString();
      }
    }
  }

  /**
   * Saves the settings.
   */
  public void saveSettings() {
    SaveManagement.saveFile(settingsPath + "Settings.json", toJsonObject().toString().getBytes());
  }


  @Override
  public String toString() {
    return "CurrentSettings{"
            + "i_picturesPerSecond=" + picturesPerSecond
            + "\n, i_boardPort=" + boardPort
            + "\n, i_appPort=" + appPort
            + "\n, i_width=" + width
            + "\n, i_height=" + height
            + "\n, i_resolutionPosition=" + resolutionPosition
            + "\n, i_loLeftMargin=" + loLeftMargin
            + "\n, i_loTopMargin=" + loTopMargin
            + "\n, i_ruLeftMargin=" + ruLeftMargin
            + "\n, i_ruTopMargin=" + ruTopMargin
            + "\n, b_uploadAutomatically=" + uploadAutomatically
            + "\n, b_withLense=" + withLense
            + "\n, b_saveSession=" + saveSession
            + "\n, b_withWifi=" + withWifi
            + "\n, b_mainMenu=" + mainMenu
            + "\n, b_manualMode=" + manualMode
            + "\n, b_autoMode=" + autoMode
            + "\n, b_settings=" + settings
            + "\n, b_tcp=" + changeWifi
            + "\n, s_ipAdress='" + ipAdress + '\''
            + "\n, s_cameraID='" + cameraID + '\''
            + "\n, s_chosenNN='" + chosenNN + '\''
            + "\n, s_appRootPath='" + appRootPath + '\''
            + "\n, s_datasetsPath='" + datasetsPath + '\''
            + "\n, s_neuronalNetworksPath='" + neuronalNetworksPath + '\''
            + "\n, s_settingsPath='" + settingsPath + '\''
            + "\n, s_applicationName='" + applicationName + '\''
            + "\n, file_chosenNN=" + fileChosenNN
            + "\n, folder_chosenNN=" + folderChosenNN
            + "\n, file_root=" + fileRoot
            + "\n, file_datasets=" + fileDatasets
            + "\n, file_neuronalNetworks=" + fileNeuronalNetworks
            + "\n, file_settings=" + fileSettings
            + '}';
  }
}
