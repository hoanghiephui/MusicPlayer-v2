package com.media.music.injector.component;

import com.media.music.injector.module.FolderSongsModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.FolderSongsFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/12.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = FolderSongsModule.class)
public interface FolderSongsComponent {

  void inject(FolderSongsFragment folderSongsFragment);
}
