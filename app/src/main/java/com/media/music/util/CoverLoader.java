package com.media.music.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.media.music.MusicPlayer;
import com.media.music.R;

/**
 * Created by hoanghiep on 5/1/17.
 */

public class CoverLoader {
  private static final String KEY_NULL = "null";
  /**
   * 缩略图缓存，用于音乐列表
   */
  private LruCache<String, Bitmap> mThumbnailCache;
  /**
   * 高斯模糊图缓存，用于播放页背景
   */
  private LruCache<String, Bitmap> mBlurCache;
  /**
   * 圆形图缓存，用于播放页CD
   */
  private LruCache<String, Bitmap> mRoundCache;

  private CoverLoader() {
    // 获取当前进程的可用内存（单位KB）
    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    // 缓存大小为当前进程可用内存的1/8
    int cacheSize = maxMemory / 8;
    mThumbnailCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(String key, Bitmap bitmap) {
        return sizeOfBitmap(bitmap);
      }
    };
    mBlurCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(String key, Bitmap bitmap) {
        return sizeOfBitmap(bitmap);
      }
    };
    mRoundCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(String key, Bitmap bitmap) {
        return sizeOfBitmap(bitmap);
      }
    };
  }

  /**
   * 获取bitmap内存，单位KB
   */
  private int sizeOfBitmap(Bitmap bitmap) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      return bitmap.getAllocationByteCount() / 1024;
    } else {
      return bitmap.getByteCount() / 1024;
    }
  }

  public static CoverLoader getInstance() {
    return SingletonHolder.instance;
  }

  private static class SingletonHolder {
    private static CoverLoader instance = new CoverLoader();
  }

  public Bitmap loadThumbnail(Context context) {
    Bitmap bitmap;
    String path = ListenerUtil.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString();
    if (TextUtils.isEmpty(path)) {
      bitmap = mThumbnailCache.get(KEY_NULL);
      if (bitmap == null) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover);
        mThumbnailCache.put(KEY_NULL, bitmap);
      }
    } else {
      bitmap = mThumbnailCache.get(path);
      if (bitmap == null) {
        bitmap = loadBitmap(path, ScreenUtils.getScreenWidth(context) / 10);
        if (bitmap == null) {
          bitmap = loadThumbnail(context);
        }
        mThumbnailCache.put(path, bitmap);
      }
    }
    return bitmap;
  }

  public Bitmap loadBlur(Context context, String path) {
    Bitmap bitmap;
    if (TextUtils.isEmpty(path)) {
      bitmap = mBlurCache.get(KEY_NULL);
      if (bitmap == null) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_page_default_bg);
        mBlurCache.put(KEY_NULL, bitmap);
      }
    } else {
      bitmap = mBlurCache.get(path);
      if (bitmap == null) {
        bitmap = loadBitmap(path, ScreenUtils.getScreenWidth(context) / 2);
        bitmap = ImageUtil.blur(bitmap);
        if (bitmap == null) {
          bitmap = loadBlur(context, null);
        }
        mBlurCache.put(path, bitmap);
      }
    }
    return bitmap;
  }

  public Bitmap loadRound(Context context, String path) {
    Bitmap bitmap;
    if (TextUtils.isEmpty(path)) {
      bitmap = mRoundCache.get(KEY_NULL);
      if (bitmap == null) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_page_default_cover);
        bitmap = ImageUtil.resizeImage(bitmap, ScreenUtils.getScreenWidth(context) / 2, ScreenUtils.getScreenWidth(context) / 2);
        mRoundCache.put(KEY_NULL, bitmap);
      }
    } else {
      bitmap = loadBitmap(path, ScreenUtils.getScreenWidth(context) / 2);
      if (bitmap == null) {
        bitmap = loadRound(context, null);
        mRoundCache.put(path, bitmap);
      } else {
        bitmap = ImageUtil.resizeImage(bitmap, ScreenUtils.getScreenWidth(context) / 2, ScreenUtils.getScreenWidth(context) / 2);
        bitmap = ImageUtil.createCircleImage(bitmap);
      }
    }
    return bitmap;
  }

  /**
   * 获得指定大小的bitmap
   */
  private Bitmap loadBitmap(String path, int length) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    // 仅获取大小
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    int maxLength = Math.max(options.outWidth, options.outHeight);
    // 压缩尺寸，避免卡顿
    int inSampleSize = maxLength / length;
    if (inSampleSize < 1) {
      inSampleSize = 1;
    }
    options.inSampleSize = inSampleSize;
    // 获取bitmap
    options.inJustDecodeBounds = false;
    options.inPreferredConfig = Bitmap.Config.RGB_565;
    return BitmapFactory.decodeFile(path, options);
  }

  public void removeCache(){
    mRoundCache.remove(KEY_NULL);
  }
}
