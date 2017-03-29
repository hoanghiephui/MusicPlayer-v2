package com.media.music.injector.component;

import com.media.music.injector.module.FolderModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.FoldersFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/11.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = FolderModule.class)
public interface FoldersComponent {

  void inject(FoldersFragment foldersFragment);
}
