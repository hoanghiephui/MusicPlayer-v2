package com.media.music.injector.module;

import com.media.music.mvp.contract.SongsContract;
import com.media.music.mvp.presenter.SongsPresenter;
import com.media.music.mvp.usecase.GetSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/12.
 */
@Module
public class SongsModule {

  @Provides
  SongsContract.Presenter getSongsPresenter(GetSongs getSongs) {
    return new SongsPresenter(getSongs);
  }

  @Provides
  GetSongs getSongsUsecase(Repository repository) {
    return new GetSongs(repository);
  }
}
