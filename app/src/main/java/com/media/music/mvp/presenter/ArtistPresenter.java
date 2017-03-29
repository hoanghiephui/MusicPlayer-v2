package com.media.music.mvp.presenter;


import com.media.music.mvp.contract.ArtistContract;
import com.media.music.mvp.model.Artist;
import com.media.music.mvp.usecase.GetArtists;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hoang Hiep on 2016/11/13.
 */

public class ArtistPresenter implements ArtistContract.Presenter {

  private GetArtists mUsecase;
  private ArtistContract.View mView;
  private CompositeSubscription mCompositeSubscription;

  public ArtistPresenter(GetArtists getArtists) {
    mUsecase = getArtists;
  }

  @Override
  public void attachView(ArtistContract.View view) {
    mView = view;
    mCompositeSubscription = new CompositeSubscription();
  }

  @Override
  public void subscribe() {
  }

  @Override
  public void unsubscribe() {
    mCompositeSubscription.clear();
  }

  @Override
  public void loadArtists(String action) {
    mCompositeSubscription.clear();
    Subscription subscription = mUsecase.execute(new GetArtists.RequestValues(action))
      .getArtistList()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Action1<List<Artist>>() {
        @Override
        public void call(List<Artist> artists) {
          if (artists == null || artists.size() == 0) {
            mView.showEmptyView();
          } else {
            mView.showArtists(artists);
          }
        }
      });
    mCompositeSubscription.add(subscription);
  }
}
