package com.media.music.injector.module;

import com.media.music.mvp.contract.PlaylistContract;
import com.media.music.mvp.presenter.PlaylistPresenter;
import com.media.music.mvp.usecase.GetPlaylists;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/5.
 */
@Module
public class PlaylistModule {

  @Provides
  GetPlaylists getPlaylistUsecase(Repository repository) {
    return new GetPlaylists(repository);
  }

  @Provides
  PlaylistContract.Presenter getPlaylistPresenter(GetPlaylists getPlaylists) {
    return new PlaylistPresenter(getPlaylists);
  }
}
