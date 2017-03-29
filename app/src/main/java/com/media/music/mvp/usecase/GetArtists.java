package com.media.music.mvp.usecase;

import com.media.music.Constants;
import com.media.music.mvp.model.Artist;
import com.media.music.respository.interfaces.Repository;

import java.util.List;

import rx.Observable;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */

public class GetArtists extends UseCase<GetArtists.RequestValues, GetArtists.ResponseValue> {

  private final Repository mRepository;

  public GetArtists(Repository repository) {
    mRepository = repository;
  }

  @Override
  public ResponseValue execute(RequestValues requestValues) {
    String action = requestValues.getAction();
    switch (action) {
      case Constants.NAVIGATE_ALLSONG:
        return new ResponseValue(mRepository.getAllArtists());
      case Constants.NAVIGATE_PLAYLIST_RECENTADD:
        return new ResponseValue(mRepository.getRecentlyAddedArtists());
      case Constants.NAVIGATE_PLAYLIST_RECENTPLAY:
        return new ResponseValue(mRepository.getRecentlyPlayedArtist());
      case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
        return new ResponseValue(mRepository.getFavourateArtist());
      default:
        throw new RuntimeException("wrong action type");
    }
  }


  public static final class RequestValues implements UseCase.RequestValues {

    private String action;

    public RequestValues(String action) {
      this.action = action;
    }

    public String getAction() {
      return action;
    }
  }

  public static final class ResponseValue implements UseCase.ResponseValue {

    private final Observable<List<Artist>> mListObservable;

    public ResponseValue(Observable<List<Artist>> listObservable) {
      mListObservable = listObservable;
    }

    public Observable<List<Artist>> getArtistList() {
      return mListObservable;
    }
  }
}
