package com.media.music.injector.module;

import com.media.music.mvp.contract.ArtistDetailContract;
import com.media.music.mvp.presenter.ArtistDetailPresenter;
import com.media.music.mvp.usecase.GetArtistInfo;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */
@Module
public class ArtistInfoModule {

  @Provides
  GetArtistInfo getArtistInfoUsecase(Repository repository) {
    return new GetArtistInfo(repository);
  }

  @Provides
  ArtistDetailContract.Presenter getArtistDetailPresenter() {
    return new ArtistDetailPresenter();
  }
}
