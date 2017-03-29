package com.media.music.injector.component;

import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.ArtistsModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.ArtistFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, ArtistsModule.class})
public interface ArtistComponent {

  void inject(ArtistFragment artistFragment);
}
