package com.media.music.mvp.usecase;

import com.media.music.respository.interfaces.Repository;

import java.util.List;

import rx.Observable;

/**
 * Created by Hoang Hiep on 2017/1/3.
 */

public class GetSearchResult extends UseCase<GetSearchResult.RequestValues, GetSearchResult.ResponseValue> {

  private Repository mRepository;

  public GetSearchResult(Repository repository) {
    this.mRepository = repository;
  }

  @Override
  public ResponseValue execute(RequestValues requestValues) {
    return new ResponseValue(mRepository.getSearchResult(requestValues.getQueryString()));
  }

  public static final class RequestValues implements UseCase.RequestValues {

    private final String queryString;

    public RequestValues(String queryString) {
      this.queryString = queryString;
    }

    public String getQueryString() {
      return queryString;
    }

  }

  public static final class ResponseValue implements UseCase.ResponseValue {

    private final Observable<List<Object>> mListObservable;

    public ResponseValue(Observable<List<Object>> listObservable) {
      mListObservable = listObservable;
    }

    public Observable<List<Object>> getResultList() {
      return mListObservable;
    }
  }
}
