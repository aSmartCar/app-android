package com.example.autonomesfahrzeug;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;


/**
 * The type Audio player.
 */
public class AudioPlayer {

  /**
   * Play audio.
   *
   * @param context the context
   * @param i       the
   */
  public static void playAudio(Context context, int i) {
    MediaPlayer player = MediaPlayer.create(context, i);
    player.start();
  }
}
