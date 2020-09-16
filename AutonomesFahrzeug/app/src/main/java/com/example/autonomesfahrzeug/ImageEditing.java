package com.example.autonomesfahrzeug;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import java.io.ByteArrayOutputStream;

/**
 * The type Image editing.
 */
public class ImageEditing {

  /**
   * Bitmap to byte array byte [ ].
   *
   * @param bmp the bmp
   * @return the byte [ ]
   */
  public static byte[] bitmapToByteArray(Bitmap bmp) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    return stream.toByteArray();
  }

  /**
   * Change alpha bitmap.
   *
   * @param originalBitmap the original bitmap
   * @param alpha          the alpha
   * @return the bitmap
   */
  public static Bitmap changeAlpha(Bitmap originalBitmap, int alpha) {
    Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
            originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    // create a canvas where we can draw on
    Canvas canvas = new Canvas(newBitmap);
    // create a paint instance with alpha
    Paint alphaPaint = new Paint();
    alphaPaint.setAlpha(alpha);
    // now lets draw using alphaPaint instance
    canvas.drawBitmap(originalBitmap, 0, 0, alphaPaint);
    return newBitmap;
  }


  /**
   * cut out bitmap.
   *
   * @param origialBitmap the origial bitmap
   * @param upperRow     the zeile oben
   * @param bottomRow    the zeile unten
   * @param rightCol  the spalte rechts
   * @param leftCol   the spalte links
   * @return the bitmap
   */
  public static Bitmap zuschneiden(Bitmap origialBitmap, int upperRow, int bottomRow,
                                   int rightCol, int leftCol) {


    assert (upperRow >= 0 && upperRow < bottomRow
            && bottomRow <= origialBitmap.getWidth());
    assert (leftCol >= 0 && leftCol < rightCol
            && rightCol <= origialBitmap.getHeight());

    Bitmap cutBitmap = Bitmap.createBitmap(origialBitmap.getWidth() / 2,
            origialBitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(cutBitmap);
    Rect desRect = new Rect(0, 0, origialBitmap.getWidth() / 2,
            origialBitmap.getHeight() / 2);
    Rect srcRect = new Rect(upperRow, rightCol, bottomRow, leftCol);
    canvas.drawBitmap(origialBitmap, srcRect, desRect, null);
    return cutBitmap;
  }

  /**
   * Gets small bitmap.
   *
   * @param bmp    the bmp
   * @param left   the left
   * @param top    the top
   * @param right  the right
   * @param bottom the bottom
   * @return the small bitmap
   */
  public static Bitmap getSmallBitmap(Bitmap bmp, int left, int top, int right, int bottom) {
    return Bitmap.createBitmap(bmp, left, top, right - left, bottom - top);
  }


  /**
   * Bitmap to matrix float [ ] [ ] [ ] [ ].
   *
   * @param bitmap the bitmap
   * @return the float [ ] [ ] [ ] [ ]
   */
  public static float[][][][] bitmapToMatrix(Bitmap bitmap) {
    float[][][][] matrix = new float[1][bitmap.getHeight()][bitmap.getWidth()][3];
    for (int h = 0; h < bitmap.getHeight(); h++) {
      for (int w = 0; w < bitmap.getWidth(); w++) {
        matrix[0][h][w][1] = Color.red(bitmap.getPixel(w, h));
        matrix[0][h][w][2] = Color.green(bitmap.getPixel(w, h));
        matrix[0][h][w][0] = Color.blue(bitmap.getPixel(w, h));
      }
    }
    return matrix;
  }

}
