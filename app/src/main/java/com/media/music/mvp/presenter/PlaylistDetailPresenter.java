package com.media.music.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.media.music.mvp.contract.PlaylistDetailContract;
import com.media.music.mvp.model.Song;
import com.media.music.mvp.usecase.GetPlaylistSongs;
import com.media.music.util.ATEUtil;
import com.media.music.util.ListenerUtil;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Hoang Hiep on 2016/12/6.
 */

public class PlaylistDetailPresenter implements PlaylistDetailContract.Presenter {

  private GetPlaylistSongs mUsecase;
  private PlaylistDetailContract.View mView;
  private CompositeSubscription mCompositeSubscription;

  public PlaylistDetailPresenter(GetPlaylistSongs getPlaylistSongs) {
    mUsecase = getPlaylistSongs;
  }

  @Override
  public void loadPlaylistSongs(long playlistID) {
    mCompositeSubscription.clear();
    Subscription subscription = mUsecase.execute(new GetPlaylistSongs.RequestValues(playlistID))
      .getSongList()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Action1<List<Song>>() {
        @Override
        public void call(List<Song> songList) {
          mView.showPlaylistSongs(songList);
        }
      });
    mCompositeSubscription.add(subscription);
  }

  @Override
  public void loadPlaylistArt(long firstAlbumID) {
    Glide.with(mView.getContext())
      .load(ListenerUtil.getAlbumArtUri(firstAlbumID))
      .asBitmap()
      .priority(Priority.IMMEDIATE)
      .error(ATEUtil.getDefaultAlbumDrawable(mView.getContext()))
      .into(new SimpleTarget<Bitmap>() {
        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
          super.onLoadFailed(e, errorDrawable);
          mView.showPlaylistArt(errorDrawable);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
          mView.showPlaylistArt(resource);
        }
      });
  }

  @Override
  public void attachView(PlaylistDetailContract.View view) {
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
}
