package com.media.music.injector.module;

import com.media.music.mvp.contract.QuickControlsContract;
import com.media.music.mvp.presenter.QuickControlsPresenter;
import com.media.music.mvp.usecase.GetLyric;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hoanghiep on 5/3/17.
 */

@Module
public class PlayerModule {
  @Provides
  QuickControlsContract.Presenter getPlayerPresenter(GetLyric getLyric) {
    return new QuickControlsPresenter(getLyric);
  }

  @Provides
  GetLyric getLyricUsecase(Repository repository) {
    return new GetLyric(repository);
  }
}
