package com.media.music.injector.module;

import android.app.Application;

import com.media.music.MediaPlayerApp;
import com.media.music.injector.scope.PerApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/1.
 */
@Module
public class ApplicationModule {
  private final MediaPlayerApp mMediaPlayerApp;

  public ApplicationModule(MediaPlayerApp mediaPlayerApp) {
    this.mMediaPlayerApp = mediaPlayerApp;
  }

  @Provides
  @PerApplication
  public MediaPlayerApp provideListenerApp() {
    return mMediaPlayerApp;
  }

  @Provides
  @PerApplication
  public Application provideApplication() {
    return mMediaPlayerApp;
  }
}
