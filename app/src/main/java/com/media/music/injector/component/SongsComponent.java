package com.media.music.injector.component;

import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.SongsModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.SongsFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/12.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, SongsModule.class})
public interface SongsComponent {

  void inject(SongsFragment songsFragment);
}
