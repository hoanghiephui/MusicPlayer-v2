package com.media.music.injector.module;

import com.media.music.mvp.contract.QuickControlsContract;
import com.media.music.mvp.presenter.QuickControlsPresenter;
import com.media.music.mvp.usecase.GetLyric;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/7.
 */
@Module
public class QuickControlsModule {

  @Provides
  QuickControlsContract.Presenter getQuickControlsPresenter(GetLyric getLyric) {
    return new QuickControlsPresenter(getLyric);
  }

  @Provides
  GetLyric getLyricUsecase(Repository repository) {
    return new GetLyric(repository);
  }

}
