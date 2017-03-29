package com.media.music.injector.component;

import com.media.music.injector.module.SearchModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.fragment.SearchFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2017/1/3.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = SearchModule.class)
public interface SearchComponent {

  void inject(SearchFragment searchFragment);
}
