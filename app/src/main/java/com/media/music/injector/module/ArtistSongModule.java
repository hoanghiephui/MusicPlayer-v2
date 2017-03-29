package com.media.music.injector.module;

import com.media.music.mvp.contract.ArtistSongContract;
import com.media.music.mvp.presenter.ArtistSongPresenter;
import com.media.music.mvp.usecase.GetArtistSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/25.
 */
@Module
public class ArtistSongModule {

  @Provides
  GetArtistSongs getArtistSongsUsecase(Repository repository) {
    return new GetArtistSongs(repository);
  }

  @Provides
  ArtistSongContract.Presenter getArtistSongPresenter(GetArtistSongs getArtistSongs) {
    return new ArtistSongPresenter(getArtistSongs);
  }
}
