package com.media.music.injector.module;

import com.media.music.mvp.contract.PlayqueueSongContract;
import com.media.music.mvp.presenter.PlayqueueSongPresenter;
import com.media.music.mvp.usecase.GetSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/27.
 */
@Module
public class PlayqueueSongModule {

  @Provides
  GetSongs getSongsUsecase(Repository repository) {
    return new GetSongs(repository);
  }

  @Provides
  PlayqueueSongContract.Presenter getPlayqueueSongUsecase(GetSongs getSongs) {
    return new PlayqueueSongPresenter(getSongs);
  }
}
