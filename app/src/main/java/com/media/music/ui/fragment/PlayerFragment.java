package com.media.music.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.media.music.MediaPlayerApp;
import com.media.music.MusicPlayer;
import com.media.music.MusicService;
import com.media.music.R;
import com.media.music.RxBus;
import com.media.music.event.FavourateSongEvent;
import com.media.music.event.MetaChangedEvent;
import com.media.music.injector.component.ApplicationComponent;
import com.media.music.injector.component.DaggerPlayerComponent;
import com.media.music.injector.component.PlayerComponent;
import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.PlayerModule;
import com.media.music.mvp.contract.QuickControlsContract;
import com.media.music.provider.FavoriteSong;
import com.media.music.ui.adapter.PlayPagerAdapter;
import com.media.music.ui.dialogs.PlayqueueDialog;
import com.media.music.util.CoverLoader;
import com.media.music.util.ListenerUtil;
import com.media.music.widget.AlbumCoverView;
import com.media.music.widget.IndicatorLayout;
import com.media.music.widget.LrcView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by hoanghiep on 5/3/17.
 */

public class PlayerFragment extends BaseFragment implements ViewPager.OnPageChangeListener, QuickControlsContract.View {
  @BindView(R.id.ll_content)
  LinearLayout llContent;
  @BindView(R.id.iv_play_page_bg)
  ImageView ivPlayingBg;
  @BindView(R.id.iv_back)
  ImageView ivBack;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.tv_artist)
  TextView tvArtist;
  @BindView(R.id.vp_play_page)
  ViewPager vpPlay;
  @BindView(R.id.il_indicator)
  IndicatorLayout ilIndicator;
  @BindView(R.id.sb_progress)
  SeekBar sbProgress;
  @BindView(R.id.tv_current_time)
  TextView tvCurrentTime;
  @BindView(R.id.tv_total_time)
  TextView tvTotalTime;
  @BindView(R.id.iv_mode)
  ImageView ivMode;
  @BindView(R.id.iv_play)
  ImageView ivPlay;
  @BindView(R.id.iv_next)
  ImageView ivNext;
  @BindView(R.id.iv_prev)
  ImageView ivPrev;
  private AlbumCoverView mAlbumCoverView;
  private LrcView mLrcViewSingle;
  private LrcView mLrcViewFull;
  private List<View> mViewPagerContent;
  private int mLastProgress;
  private PlayqueueDialog.PlayMode mPlayMode;
  private boolean mIsFavorite = false;

  @Inject
  QuickControlsContract.Presenter mPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependences();
    mPresenter.attachView(this);
  }

  @Override
  protected int getViewLayout() {
    return R.layout.fragment_player;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initViewPager();
    subscribeFavourateSongEvent();
    mPresenter.updateNowPlayingCard();
    mPresenter.loadLyric();
    subscribeMetaChangedEvent();
    initViewPlayMode();
    setSeekBarListener();
  }

  private void setSeekBarListener() {
    if (sbProgress != null)
      sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          if (fromUser) {
            /*if (songElapsedTime.getVisibility() == View.GONE) {
              songElapsedTime.setVisibility(View.VISIBLE);
            }*/
            sbProgress.removeCallbacks(mUpdateProgress);
          }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
          //songElapsedTime.setVisibility(View.GONE);
          MusicPlayer.seek((long) seekBar.getProgress());
          sbProgress.postDelayed(mUpdateProgress, 10);
          int progress = seekBar.getProgress();
          mLrcViewFull.onDrag(progress);
          mLrcViewSingle.onDrag(progress);
        }
      });
  }

  private void injectDependences() {
    ApplicationComponent applicationComponent = ((MediaPlayerApp) getActivity().getApplication())
            .getApplicationComponent();
    PlayerComponent playerComponent = DaggerPlayerComponent.builder()
            .applicationComponent(applicationComponent)
            .activityModule(new ActivityModule(getActivity()))
            .playerModule(new PlayerModule())
            .build();
    playerComponent.inject(this);
  }

  private void subscribeFavourateSongEvent() {
    Subscription subscription = RxBus.getInstance()
            .toObservable(FavourateSongEvent.class)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<FavourateSongEvent>() {
              @Override
              public void call(FavourateSongEvent event) {
                mIsFavorite = FavoriteSong.getInstance(getContext()).isFavorite(MusicPlayer.getCurrentAudioId());
                if (mIsFavorite) {
                  //favorite.setColor(Color.parseColor("#E97767"));
                } else {
                  //favorite.setColor(blackWhiteColor);
                }
              }
            }, new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {

              }
            });
    RxBus.getInstance().addSubscription(this, subscription);
  }

  private void subscribeMetaChangedEvent() {
    Subscription subscription = RxBus.getInstance()
            .toObservable(MetaChangedEvent.class)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<MetaChangedEvent>() {
              @Override
              public void call(MetaChangedEvent event) {
                mPresenter.updateNowPlayingCard();
                mPresenter.loadLyric();
              }
            }, new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {
                Log.d("AA", "call: " + throwable.getMessage());
              }
            });
    RxBus.getInstance().addSubscription(this, subscription);
  }

  private void initViewPlayMode() {
    int shuffleMode = MusicPlayer.getShuffleMode();
    int repeatMode = MusicPlayer.getRepeatMode();
    if (shuffleMode == MusicService.SHUFFLE_NONE) {
      if (repeatMode == MusicService.REPEAT_CURRENT) {
        mPlayMode = PlayqueueDialog.PlayMode.CURRENT;
      } else {
        mPlayMode = PlayqueueDialog.PlayMode.REPEATALL;
      }
    } else if (shuffleMode == MusicService.SHUFFLE_NORMAL || shuffleMode == MusicService.SHUFFLE_AUTO) {
      mPlayMode = PlayqueueDialog.PlayMode.SHUFFLE;
    }
    initPlayMode();
  }

  private void initViewPager() {
    View coverView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_cover, new LinearLayout(getContext()), false);
    View lrcView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_lrc, new LinearLayout(getContext()), false);
    mAlbumCoverView = (AlbumCoverView) coverView.findViewById(R.id.album_cover_view);
    mLrcViewSingle = (LrcView) coverView.findViewById(R.id.lrc_view_single);
    mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
    mAlbumCoverView.initNeedle(MusicPlayer.isPlaying());

    mViewPagerContent = new ArrayList<>(3);
    mViewPagerContent.add(lrcView);
    mViewPagerContent.add(coverView);
    mViewPagerContent.add(lrcView);
    vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));
    vpPlay.addOnPageChangeListener(this);
    vpPlay.setCurrentItem(1);
    ilIndicator.create(mViewPagerContent.size());
    ilIndicator.setCurrent(1);
  }

  @OnClick(R.id.iv_play)
  public void onPlayPauseClick() {
    mPresenter.onPlayPauseClick();
  }

  @OnClick(R.id.iv_mode)
  public void switchPlayMode() {
    switch (mPlayMode) {
      case REPEATALL:
        MusicPlayer.setShuffleMode(MusicService.SHUFFLE_NONE);
        MusicPlayer.setRepeatMode(MusicService.REPEAT_CURRENT);
        mPlayMode = PlayqueueDialog.PlayMode.CURRENT;
        break;
      case CURRENT:
        MusicPlayer.setShuffleMode(MusicService.SHUFFLE_NORMAL);
        MusicPlayer.setRepeatMode(MusicService.REPEAT_ALL);
        mPlayMode = PlayqueueDialog.PlayMode.SHUFFLE;
        break;
      case SHUFFLE:
        MusicPlayer.setShuffleMode(MusicService.SHUFFLE_NONE);
        mPlayMode = PlayqueueDialog.PlayMode.REPEATALL;
        break;
      default:
        break;
    }
    initPlayMode();
  }

  private void initPlayMode() {
    ivMode.setImageLevel(mPlayMode.ordinal());
  }

  public void onPlayerPause() {
    ivPlay.setSelected(false);
    mAlbumCoverView.pause();
  }

  public void onPlayerResume() {
    ivPlay.setSelected(true);
    mAlbumCoverView.start();
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    ilIndicator.setCurrent(position);
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  //update view..................................................
  @Override
  public void setAlbumArt(Bitmap albumArt) {
  }

  @Override
  public void setAlbumArt(Drawable albumArt) {
  }

  @Override
  public void setAlbumArt(String url) {
    mAlbumCoverView.setCoverBitmap(CoverLoader.getInstance().loadRound(getContext(), url));
    ivPlayingBg.setImageBitmap(CoverLoader.getInstance().loadBlur(getContext(), url));
  }

  @Override
  public void setTitle(String title) {
    if (title != null) {
      tvTitle.setText(title);
    }
  }

  @Override
  public void setArtist(String artist) {
    if (artist != null) {
      tvArtist.setText(artist);
    }
  }

  @Override
  public void setPalette(Palette palette) {

  }

  @Override
  public void showLyric(File file) {
    mLrcViewSingle.loadLrc(file);
    mLrcViewFull.loadLrc(file);
  }

  @Override
  public void setPlayPauseButton(boolean isPlaying) {
    if (isPlaying) {
      onPlayerResume();
    } else {
      onPlayerPause();
    }
  }

  @Override
  public boolean getPlayPauseStatus() {
    return ivPlay.isSelected();
  }

  @Override
  public void startUpdateProgress() {
    sbProgress.postDelayed(mUpdateProgress, 10);
  }

  @Override
  public void setProgressMax(int max) {
    sbProgress.setMax(max);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.unsubscribe();
    RxBus.getInstance().unSubscribe(this);
  }

  private Runnable mUpdateProgress = new Runnable() {

    @Override
    public void run() {
      if (getActivity() != null) {
        long position = MusicPlayer.position();
        sbProgress.setProgress((int) position);
        if (mLrcViewSingle.hasLrc()) {
          mLrcViewSingle.updateTime(position);
          mLrcViewFull.updateTime(position);
          Log.d("AA", "run: " + position);
        }
        if (MusicPlayer.isPlaying()) {
          sbProgress.postDelayed(mUpdateProgress, 50);
        } else {
          sbProgress.removeCallbacks(this);
        }
        String time = ListenerUtil.makeShortTimeString(getActivity(), sbProgress.getProgress() / 1000);
        tvCurrentTime.setText(time);
        tvTotalTime.setText(ListenerUtil.makeShortTimeString(getActivity(), MusicPlayer.duration() / 1000));

      }
    }
  };
}
