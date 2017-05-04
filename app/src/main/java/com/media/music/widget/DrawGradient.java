package com.media.music.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by hoanghiep on 5/1/17.
 */

public class DrawGradient extends BitmapTransformation {
  private static Transformation instance;

  public DrawGradient(Context context) {
    super(context);
  }

  public static Transformation getInstance(Context context) {
    if (instance == null) {
      instance = new DrawGradient(context);
    }
    return instance;
  }



  @Override
  public String getId() {
    return "gradient()";
  }

  @Override
  protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
    Bitmap overlay = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(overlay);

    canvas.drawBitmap(toTransform, 0, 0, null);
    toTransform.recycle();

    Paint paint = new Paint();
    float gradientHeight = outHeight / 2f;
    LinearGradient shader = new LinearGradient(0, 0, 0, outHeight, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
    paint.setShader(shader);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    canvas.drawRect(0, 0, outWidth, outHeight, paint);
    return overlay;
  }
}

