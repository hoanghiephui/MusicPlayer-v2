package com.media.music.injector.component;

import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.QuickControlsModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.PlayerFragment;
import com.media.music.ui.fragment.QuickControlsFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/8.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, QuickControlsModule.class})
public interface QuickControlsComponent {

  void inject(QuickControlsFragment quickControlsFragment);

}
