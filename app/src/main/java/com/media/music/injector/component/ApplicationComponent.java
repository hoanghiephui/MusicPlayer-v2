package com.media.music.injector.component;

import android.app.Application;

import com.media.music.MediaPlayerApp;
import com.media.music.injector.module.ApplicationModule;
import com.media.music.injector.module.NetworkModule;
import com.media.music.injector.scope.PerApplication;
import com.media.music.respository.interfaces.Repository;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/3.
 */
@PerApplication
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

  Application application();

  MediaPlayerApp listenerApplication();

  Repository repository();
}
