package com.media.music.injector.component;

import com.media.music.injector.module.ArtistInfoModule;
import com.media.music.injector.scope.PerActivity;
import com.media.music.ui.adapter.ArtistAdapter;
import com.media.music.ui.fragment.ArtistDetailFragment;

import dagger.Component;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ArtistInfoModule.class)
public interface ArtistInfoComponent {

  void injectForAdapter(ArtistAdapter artistAdapter);

  void injectForFragment(ArtistDetailFragment fragment);
}
