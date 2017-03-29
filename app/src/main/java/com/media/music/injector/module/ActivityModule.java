package com.media.music.injector.module;

import android.app.Activity;
import android.content.Context;

import com.media.music.injector.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/1.
 */
@Module
public class ActivityModule {
  private final Activity mActivity;

  public ActivityModule(Activity activity) {
    this.mActivity = activity;
  }

  @Provides
  @PerActivity
  public Context provideContext() {
    return mActivity;
  }
}
