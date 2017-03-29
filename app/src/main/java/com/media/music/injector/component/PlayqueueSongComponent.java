package com.media.music.injector.component;

import com.media.music.injector.module.PlayqueueSongModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.dialogs.PlayqueueDialog;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/27.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = PlayqueueSongModule.class)
public interface PlayqueueSongComponent {

  void inject(PlayqueueDialog playqueueDialog);
}
