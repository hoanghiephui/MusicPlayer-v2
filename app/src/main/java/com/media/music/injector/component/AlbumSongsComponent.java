package com.media.music.injector.component;

import com.media.music.injector.module.AlbumSongsModel;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.AlbumDetailFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/3.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = AlbumSongsModel.class)
public interface AlbumSongsComponent {

  void inject(AlbumDetailFragment albumDetailFragment);

}
