package com.media.music.injector.module;

import com.media.music.mvp.contract.FoldersContract;
import com.media.music.mvp.presenter.FolderPresenter;
import com.media.music.mvp.usecase.GetFolders;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/12/11.
 */
@Module
public class FolderModule {

  @Provides
  GetFolders getFoldersUsecase(Repository repository) {
    return new GetFolders(repository);
  }

  @Provides
  FoldersContract.Presenter getFoldersPresenter(GetFolders getFolders) {
    return new FolderPresenter(getFolders);
  }
}
