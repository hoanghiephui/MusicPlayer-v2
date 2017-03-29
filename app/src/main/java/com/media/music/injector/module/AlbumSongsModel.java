package com.media.music.injector.module;

import com.media.music.mvp.contract.AlbumDetailContract;
import com.media.music.mvp.presenter.AlbumDetailPresenter;
import com.media.music.mvp.usecase.GetAlbumSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/3.
 */
@Module
public class AlbumSongsModel {

  @Provides
  GetAlbumSongs getAlbumSongUsecase(Repository repository) {
    return new GetAlbumSongs(repository);
  }

  @Provides
  AlbumDetailContract.Presenter getAlbumDetailPresenter(GetAlbumSongs getAlbumSongs) {
    return new AlbumDetailPresenter(getAlbumSongs);
  }
}
