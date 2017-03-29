package com.media.music.injector.component;

import com.media.music.injector.module.PlaylistSongModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.PlaylistDetailFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/6.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = PlaylistSongModule.class)
public interface PlaylistSongComponent {

  void inject(PlaylistDetailFragment playlistDetailFragment);
}
