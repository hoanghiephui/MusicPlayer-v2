package com.media.music;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.afollestad.appthemeengine.ATE;
import com.media.music.dataloader.SongLoader;
import com.media.music.event.MediaUpdateEvent;
import com.media.music.injector.component.ApplicationComponent;
import com.media.music.injector.component.DaggerApplicationComponent;
import com.media.music.injector.module.ApplicationModule;
import com.media.music.injector.module.NetworkModule;
import com.media.music.mvp.model.Song;
import com.media.music.permission.PermissionManager;
import com.media.music.util.ListenerUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Hoang Hiep on 2016/10/4.
 */

public class MediaPlayerApp extends Application {

  private ApplicationComponent mApplicationComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    setupInjector();
    initImageLoader();
    PermissionManager.init(this);
    updataMedia();
    setupATE();
  }

  private void setupInjector() {
    mApplicationComponent = DaggerApplicationComponent.builder()
      .applicationModule(new ApplicationModule(this))
      .networkModule(new NetworkModule(this)).build();
  }

  private void initImageLoader() {
    ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
    ImageLoader.getInstance().init(localImageLoaderConfiguration);
  }

  public ApplicationComponent getApplicationComponent() {
    return mApplicationComponent;
  }

  private void updataMedia() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (ListenerUtil.isMarshmallow() && !PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        return;
      }
      SongLoader.getAllSongs(this)
        .map(new Func1<List<Song>, String[]>() {
          @Override
          public String[] call(List<Song> songList) {
            List<String> folderPath = new ArrayList<String>();
            int i = 0;
            for (Song song : songList) {
              folderPath.add(i, song.path);
              i++;
            }
            return folderPath.toArray(new String[0]);
          }
        })
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<String[]>() {
          @Override
          public void call(String[] paths) {
            MediaScannerConnection.scanFile(getApplicationContext(), paths, null,
              new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                  if (uri == null) {
                    RxBus.getInstance().post(new MediaUpdateEvent());
                  }
                }
              });
          }
        });
    } else {
      sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
        + Environment.getExternalStorageDirectory())));
    }

  }

  private void setupATE() {
    if (!ATE.config(this, "light_theme").isConfigured()) {
      ATE.config(this, "light_theme")
        .activityTheme(R.style.AppThemeLight)
        .coloredNavigationBar(false)
        .usingMaterialDialogs(true)
        .commit();
    }
    if (!ATE.config(this, "dark_theme").isConfigured()) {
      ATE.config(this, "dark_theme")
        .activityTheme(R.style.AppThemeDark)
        .coloredNavigationBar(false)
        .usingMaterialDialogs(true)
        .commit();
    }
  }
}
