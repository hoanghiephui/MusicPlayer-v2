package com.media.music.injector.component;

import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.PlayerModule;
import com.media.music.injector.module.PlayqueueSongModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.PlayerFragment;

import dagger.Component;

/**
 * Created by hoanghiep on 5/3/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, PlayerModule.class, PlayqueueSongModule.class})
public interface PlayerComponent {
  void inject(PlayerFragment playerFragment);
}
