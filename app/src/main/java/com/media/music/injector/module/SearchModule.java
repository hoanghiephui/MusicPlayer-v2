package com.media.music.injector.module;

import com.media.music.mvp.contract.SearchContract;
import com.media.music.mvp.presenter.SearchPresenter;
import com.media.music.mvp.usecase.GetSearchResult;
import com.media.music.respository.interfaces.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hoang Hiep on 2017/1/3.
 */
@Module
public class SearchModule {

  @Provides
  SearchContract.Presenter getSearchPresenter(GetSearchResult getSearchResult) {
    return new SearchPresenter(getSearchResult);
  }

  @Provides
  GetSearchResult getSearchResultUsecase(Repository repository) {
    return new GetSearchResult(repository);
  }
}
