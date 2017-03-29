package com.media.music.injector.module;

import com.media.music.mvp.contract.PlayRankingContract;
import com.media.music.mvp.presenter.PlayRankingPresenter;
import com.media.music.mvp.usecase.GetSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/9.
 */
@Module
public class PlayRankingModule {

  @Provides
  GetSongs getSongsUsecase(Repository repository) {
    return new GetSongs(repository);
  }

  @Provides
  PlayRankingContract.Presenter getPlayRankingPresenter(GetSongs getSongs) {
    return new PlayRankingPresenter(getSongs);
  }
}
