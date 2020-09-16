package com.example.autonomesfahrzeug;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * The type Custom scroll view.
 */
public class CustomScrollView extends ScrollView {

  private boolean enableScrolling = true;

  /**
   * Is enable scrolling boolean.
   *
   * @return the boolean
   */
  public boolean isEnableScrolling() {
    return enableScrolling;
  }

  /**
   * Sets enable scrolling.
   *
   * @param enableScrolling the enable scrolling
   */
  public void setEnableScrolling(boolean enableScrolling) {
    this.enableScrolling = enableScrolling;
  }

  /**
   * Instantiates a new Custom scroll view.
   *
   * @param context  the context
   * @param attrs    the attrs
   * @param defStyle the def style
   */
  public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  /**
   * Instantiates a new Custom scroll view.
   *
   * @param context the context
   * @param attrs   the attrs
   */
  public CustomScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * Instantiates a new Custom scroll view.
   *
   * @param context the context
   */
  public CustomScrollView(Context context) {
    super(context);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {

    if (isEnableScrolling()) {
      return super.onInterceptTouchEvent(ev);
    } else {
      return false;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (isEnableScrolling()) {
      return super.onTouchEvent(ev);
    } else {
      return false;
    }
  }
}
