package com.media.music.mvp.presenter;

import com.media.music.mvp.contract.ArtistSongContract;
import com.media.music.mvp.model.Song;
import com.media.music.mvp.usecase.GetArtistSongs;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hoang Hiep on 2016/11/25.
 */

public class ArtistSongPresenter implements ArtistSongContract.Presenter {

  private GetArtistSongs mUsecase;
  private ArtistSongContract.View mView;
  private CompositeSubscription mCompositeSubscription;

  public ArtistSongPresenter(GetArtistSongs getArtistSongs) {
    mUsecase = getArtistSongs;
  }

  @Override
  public void attachView(ArtistSongContract.View view) {
    mView = view;
    mCompositeSubscription = new CompositeSubscription();
  }

  @Override
  public void subscribe() {
    throw new RuntimeException("please to call subscribe(long artistID)");
  }

  @Override
  public void subscribe(long artistID) {
    loadSongs(artistID);
  }

  @Override
  public void unsubscribe() {
    mCompositeSubscription.clear();
  }

  @Override
  public void loadSongs(long artistID) {
    mCompositeSubscription.clear();
    Subscription subscription = mUsecase.execute(new GetArtistSongs.RequestValues(artistID))
      .getSongList()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Subscriber<List<Song>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Song> songList) {
          mView.showSongs(songList);
        }
      });
    mCompositeSubscription.add(subscription);
  }
}
