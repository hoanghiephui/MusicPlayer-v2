package com.media.music.injector.component;

import com.media.music.injector.module.PlaylistModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.PlaylistFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/5.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = PlaylistModule.class)
public interface PlaylistComponent {

  void inject(PlaylistFragment playlistFragment);
}
