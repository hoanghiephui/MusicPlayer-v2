package com.media.music.mvp.presenter;

import com.media.music.mvp.contract.SongsContract;
import com.media.music.mvp.model.Song;
import com.media.music.mvp.usecase.GetSongs;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hoang Hiep on 2016/11/12.
 */

public class SongsPresenter implements SongsContract.Presenter {

  private GetSongs mUsecase;
  private SongsContract.View mView;
  private CompositeSubscription mCompositeSubscription;

  public SongsPresenter(GetSongs getSongs) {
    mUsecase = getSongs;
  }

  @Override
  public void attachView(SongsContract.View view) {
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
  public void loadSongs(String action) {
    mCompositeSubscription.clear();
    Subscription subscription = mUsecase.execute(new GetSongs.RequestValues(action))
      .getSongList()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Action1<List<Song>>() {
        @Override
        public void call(List<Song> songList) {
          if (songList == null || songList.size() == 0) {
            mView.showEmptyView();
          } else {
            mView.showSongs(songList);
          }
        }
      });
    mCompositeSubscription.add(subscription);
  }
}
