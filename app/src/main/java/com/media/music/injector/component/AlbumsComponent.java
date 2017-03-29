package com.media.music.injector.component;

import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.AlbumsModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.AlbumFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/12.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, AlbumsModule.class})
public interface AlbumsComponent {

  void inject(AlbumFragment albumFragment);
}
