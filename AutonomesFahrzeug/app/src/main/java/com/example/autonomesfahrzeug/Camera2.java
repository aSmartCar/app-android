package com.example.autonomesfahrzeug;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;


/**
 * The type Camera 2.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2 {

  AppCompatActivity activity;

  private String camera2Id;
  protected CameraDevice camera2Device;
  protected CameraCaptureSession camera2CaptureSessions;
  protected CaptureRequest.Builder camera2CaptureRequestBuilder;
  private Size imageSize;

  private static final int REQUEST_CAMERA_PERMISSION = 200;
  private Handler backgroundHandler;
  private HandlerThread backgroundThread;
  private TextureView textureView;

  float fps = 30;
  private int pictureCounter = 0;
  private Range<Integer> framerateRange;

  static SimpleDateFormat datumsformat = new SimpleDateFormat("ddMMyyyy-HHmmss-SSS");


  boolean started = false;
  float lenkwinkel;
  float geschwindigkeit;

  private Mode mode;
  private Interpreter tflite;

  private long timeNewPicture;

  private Logger logger;

  /**
   * Sets picture counter.
   *
   * @param pictureCounter the picture counter
   */
  public void setPictureCounter(int pictureCounter) {
    this.pictureCounter = pictureCounter;
  }


  /**
   * Instantiates a new Camera 2.
   *
   * @param activity the activity
   * @param mode     the mode
   */
  public Camera2(AppCompatActivity activity, Mode mode) {
    //this(activity);
    this.activity = activity;
    this.mode = mode;
    //init();
  }

  /**
   * Instantiates a new Camera 2.
   *
   * @param activity       the activity
   * @param mode           the mode
   * @param size           the size
   * @param camera2Id      the camera 2 id
   * @param framerateRange the framerate range
   */
  public Camera2(AppCompatActivity activity, Mode mode, Size size, String camera2Id, int framerateRange) {
    this.activity = activity;
    this.mode = mode;
    this.imageSize = size;
    this.camera2Id = camera2Id;
    this.framerateRange = new Range<Integer>(framerateRange, framerateRange);
    if (camera2Id.equals("")) {

      CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
      try {
        this.camera2Id = manager.getCameraIdList()[0];
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
    }
    init(mode);

  }

  /**
   * Instantiates a new Camera 2.
   *
   * @param activity       the activity
   * @param mode           the mode
   * @param size           the size
   * @param camera2Id      the camera 2 id
   * @param tflite         the tflite
   * @param framerateRange the framerate range
   */
  public Camera2(AppCompatActivity activity, Mode mode,
                 Size size, String camera2Id, Interpreter tflite, int framerateRange) {
    this.activity = activity;
    this.mode = mode;
    this.imageSize = size;
    this.camera2Id = camera2Id;
    if (camera2Id.equals("")) {
      CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
      try {
        this.camera2Id = manager.getCameraIdList()[0];
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
    }
    this.framerateRange = new Range<Integer>(framerateRange, framerateRange);
    this.tflite = tflite;
    init(mode);
  }


  /**
   * Init.
   *
   * @param mode the mode
   */
  public void init(Mode mode) {
    textureView = (TextureView) activity.findViewById(R.id.textureView);
    assert textureView != null;
    if (mode.equals(Mode.auto)) {
      textureView.setSurfaceTextureListener(textureListenerAutonom);
      logger = new Logger(CurrentSettings.getCurrentSettings(activity), datumsformat.format(new Date()));
    } else if (mode.equals(Mode.manual)) {
      textureView.setSurfaceTextureListener(textureListenerManual);
      logger = new Logger(CurrentSettings.getCurrentSettings(activity), ActivityManualMode.datasetFolderName + "-camera");
    } else if (mode.equals(Mode.stop)){
      textureView.setSurfaceTextureListener(textureListener);
    }
  }

  public void setLogger(Logger logger) {
    this.logger = logger;
  }
  /**
   * Take picture bitmap.
   *
   * @return the bitmap
   */
  public Bitmap takePicture() {
    return textureView.getBitmap();
  }


  /**
   * Camera 2 create camera preview.
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  protected void camera2CreateCameraPreview() {
    try {
      SurfaceTexture texture = textureView.getSurfaceTexture();
      assert texture != null;
      texture.setDefaultBufferSize(imageSize.getWidth(), imageSize.getHeight());
      Surface surface = new Surface(texture);

      camera2CaptureRequestBuilder = camera2Device
              .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      camera2CaptureRequestBuilder.addTarget(surface);
      camera2Device.createCaptureSession(Arrays.asList(surface),
              new CameraCaptureSession.StateCallback() {
          @Override
          public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            //The camera is already closed
            if (null == camera2Device) {
              return;
            }
            // When the session is ready, we start displaying the preview.
            camera2CaptureSessions = cameraCaptureSession;
            camera2updatePreview();

          }

          @Override
          public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            Toast.makeText(activity, "Configuration change", Toast.LENGTH_SHORT).show();
          }
          }, null);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * Camera 2 update preview.
   */
  protected void camera2updatePreview() {
    camera2CaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, framerateRange);

    try {
      camera2CaptureSessions.setRepeatingRequest(camera2CaptureRequestBuilder.build(),
              null, backgroundHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * Camera 2 open camera.
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void camera2OpenCamera() {
    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try {
      if (ActivityCompat.checkSelfPermission(activity,
              Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(activity,
              Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
        return;
      }
      manager.openCamera(camera2Id, stateCallback, null);
      started = true;
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
    @Override
    public void onOpened(CameraDevice camera) {
      //This is called when the camera is open
      camera2Device = camera;
      camera2CreateCameraPreview();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDisconnected(CameraDevice camera) {
      camera2Device.close();
    }

    @Override
    public void onError(CameraDevice camera, int error) {
      camera2Device.close();
      camera2Device = null;
    }
  };

  /**
   * The Texture listener.
   */
  TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
      //open your camera here
      camera2OpenCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
      // Transform you image captured size according to the surface width and height
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
      timeNewPicture = System.nanoTime();
    }
  };

  /**
   * The Texture listener manual.
   */
  TextureView.SurfaceTextureListener textureListenerManual = new TextureView.SurfaceTextureListener() {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
      //open your camera here
      camera2OpenCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
      Date date = new Date();
      if(ActivityManualMode.sessionRunning) {
        if (ActivityManualMode.setupFinished && ActivityManualMode.saveDataset) {
          timeNewPicture = System.nanoTime();
          //Date date = new Date();
          long startTimeTakePicture = System.nanoTime();
          Bitmap bmp = takePicture();
          long endTimeTakePicture = System.nanoTime();

          long startTimeGetValues = System.nanoTime();
          ArrayList<JsonEntry> entries = new ArrayList<JsonEntry>() { {
            add(new JsonEntry<Float>(JsonKey.steering, ActivityManualMode.steering));
            add(new JsonEntry<Float>(JsonKey.throttle, ActivityManualMode.throttle));
            add(new JsonEntry<Long>(JsonKey.timestamp, date.getTime()));
            add(new JsonEntry<String>(JsonKey.image, File.separator + "content" + File.separator + "data" + File.separator + ActivityManualMode.datasetFolder.getName() + File.separator + "Picture" + File.separator + "train-" + datumsformat.format(date) + ".jpg"));
          }};
          JSONObject jsonObject = JsonFile.createJsonObject(entries);
          long endTimeGetValues = System.nanoTime();

          long startTimesaveInList = System.nanoTime();
          Dataset dataset = new Dataset(bmp, jsonObject, "train-" + datumsformat.format(date), pictureCounter);
          ActivityManualMode.datasets.add(dataset);
          long endTimeSaveInList = System.nanoTime();
          float deltaTakePicture = (float)(endTimeTakePicture - startTimeTakePicture) / 1000000f;
          float deltaGetValues = (float)(endTimeGetValues - startTimeGetValues) / 1000000f;
          float deltaSaveInList = (float)(endTimeSaveInList - startTimesaveInList) / 1000000f;
          logger.addLog(new String[] {"" + timeNewPicture,
                                      "" + startTimeTakePicture, "" + endTimeTakePicture, "" + deltaTakePicture,
                                      "" + startTimeGetValues, "" + endTimeGetValues, "" + deltaGetValues,
                                      "" + startTimesaveInList, "" + endTimeSaveInList, "" + deltaSaveInList});
          if (!ActivityManualMode.saveInBackgroundRunning && ActivityManualMode.datasets.size() > 0) {
            ActivityManualMode.saveInBackgroundRunning = true;
            new Thread(new Runnable() {
              @Override
              public void run() {
                ActivityManualMode.saveDatasetsInBackground();
              }
            }).start();
            pictureCounter++;
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                ((TextView)activity.findViewById(R.id.textView_anzahlBilder)).setText(activity.getString(R.string.picturesTaken) + ": " + pictureCounter);
              }
            });
          }
        }
        ActivityManualMode.sendControlValues();
      } else {
        ActivityManualMode.stop();
      }
    }
  };

  /**
   * The Texture listener autonom.
   */
  TextureView.SurfaceTextureListener textureListenerAutonom = new TextureView.SurfaceTextureListener() {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
      //open your camera here
      camera2OpenCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
      // Transform you image captured size according to the surface width and height
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
      if (ActivityAutoMode.running) {
        timeNewPicture = System.nanoTime();
        Bitmap bmp = takePicture();
        bmp = ImageEditing.getSmallBitmap(bmp, ActivityAutoMode.loLeft, ActivityAutoMode.loTop, ActivityAutoMode.ruLeft, ActivityAutoMode.ruTop);
                /*
                currentSettings.getLoLeftMargin(),
                currentSettings.getLoTopMargin(),
                currentSettings.getRuLeftMargin(),
                currentSettings.getRuTopMargin());
                 */
        bmp = Bitmap.createScaledBitmap(bmp, 160, 50, false);
        float[][][][] input = ImageEditing.bitmapToMatrix(bmp);
        float[][] output = new float[1][15];
        tflite.run(input, output);
        float steering = ActivityAutoMode.interpretValues(output[0]);
        pictureCounter++;
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            ((TextView)activity.findViewById(R.id.textView_bilder_die_sekunde)).setText(activity.getString(R.string.picturesTaken) + ": " + pictureCounter);
            //ActivityAutoMode.setSteering(steering);
          }
        });
        ActivityAutoMode.sendControlValues();
      } else {
        ActivityAutoMode.stop();
      }
    }
  };

  /**
   * Get back cameras string [ ].
   *
   * @return the string [ ]
   */
  public String[] getBackCameras() {
    ArrayList<String> ids = new ArrayList<>();
    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try {
      String[] cameraIDs = manager.getCameraIdList();
      for (String s: cameraIDs) {
        if (manager.getCameraCharacteristics(s).get(CameraCharacteristics.LENS_FACING)
                == CameraCharacteristics.LENS_FACING_BACK) {
          ids.add(s);
        }
      }
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
    String[] id = new String[ids.size()];
    for (int i = 0; i < id.length; i++) {
      id[i] = ids.get(i);
    }
    return id;
  }

  /**
   * Get supported camera sizes size [ ].
   *
   * @param id the id
   * @return the size [ ]
   */
  public Size[] getSupportedCameraSizes(String id) {
    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    CameraCharacteristics cameraCharacteristics = null;
    Size[] sizes = null;
    try {
      cameraCharacteristics = manager.getCameraCharacteristics(id);
      StreamConfigurationMap streamConfigurationMap
              = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
    return sizes;
  }
}
