package com.media.music.injector.module;

import com.media.music.mvp.contract.PlaylistDetailContract;
import com.media.music.mvp.presenter.PlaylistDetailPresenter;
import com.media.music.mvp.usecase.GetPlaylistSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/6.
 */
@Module
public class PlaylistSongModule {

  @Provides
  GetPlaylistSongs getPlaylistSongsUsecase(Repository repository) {
    return new GetPlaylistSongs(repository);
  }

  @Provides
  PlaylistDetailContract.Presenter getPlaylistDetailPresenter(GetPlaylistSongs getPlaylistSongs) {
    return new PlaylistDetailPresenter(getPlaylistSongs);
  }
}
