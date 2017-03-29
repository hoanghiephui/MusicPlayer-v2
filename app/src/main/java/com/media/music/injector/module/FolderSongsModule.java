package com.media.music.injector.module;

import com.media.music.mvp.contract.FolderSongsContract;
import com.media.music.mvp.presenter.FolderSongsPresenter;
import com.media.music.mvp.usecase.GetFolderSongs;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/12.
 */
@Module
public class FolderSongsModule {

  @Provides
  GetFolderSongs getFolderSongsUsecase(Repository repository) {
    return new GetFolderSongs(repository);
  }

  @Provides
  FolderSongsContract.Presenter getFolderSongsPresenter(GetFolderSongs getFolderSongs) {
    return new FolderSongsPresenter(getFolderSongs);
  }
}
