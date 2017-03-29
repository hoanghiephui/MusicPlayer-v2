package com.media.music.injector.module;

import com.media.music.mvp.contract.AlbumsContract;
import com.media.music.mvp.presenter.AlbumsPresenter;
import com.media.music.mvp.usecase.GetAlbums;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2016/11/12.
 */
@Module
public class AlbumsModule {

  @Provides
  AlbumsContract.Presenter getAlbumsPresenter(GetAlbums getAlbums) {
    return new AlbumsPresenter(getAlbums);
  }

  @Provides
  GetAlbums getAlbumsUsecase(Repository repository) {
    return new GetAlbums(repository);
  }
}
