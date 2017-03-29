package com.media.music.injector.component;

import com.media.music.injector.module.ArtistSongModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.ArtistMusicFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/25.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ArtistSongModule.class)
public interface ArtistSongsComponent {

  void inject(ArtistMusicFragment artistMusicFragment);
}
