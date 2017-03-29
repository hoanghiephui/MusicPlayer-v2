package com.media.music.injector.component;

import com.media.music.injector.module.PlayRankingModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.PlayRankingFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/12/9.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = PlayRankingModule.class)
public interface PlayRankingComponent {

  void inject(PlayRankingFragment playRankingFragment);
}
