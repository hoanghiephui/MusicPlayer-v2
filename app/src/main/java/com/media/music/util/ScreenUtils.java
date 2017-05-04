package com.media.music.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by hoanghiep on 5/1/17.
 */

public class ScreenUtils {

  public static int dp2px(Context mContext, float dpValue) {
    final float scale = mContext.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static int sp2px(Context context, float spValue) {
    float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  public static int getScreenWidth(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    return wm.getDefaultDisplay().getWidth();
  }
}
