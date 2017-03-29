package com.media.music.injector.module;

import com.media.music.mvp.contract.ArtistContract;
import com.media.music.mvp.presenter.ArtistPresenter;
import com.media.music.mvp.usecase.GetArtists;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */
@Module
public class ArtistsModule {

  @Provides
  ArtistContract.Presenter getArtistPresenter(GetArtists getArtists) {
    return new ArtistPresenter(getArtists);
  }

  @Provides
  GetArtists getArtistsUsecase(Repository repository) {
    return new GetArtists(repository);
  }
}
