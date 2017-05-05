package com.media.music.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.media.music.injector.module.PlayqueueSongModule;
import com.media.music.mvp.contract.PlayqueueSongContract;
import com.media.music.mvp.contract.QuickControlsContract;
import com.media.music.mvp.model.Song;
import com.media.music.provider.FavoriteSong;
import com.media.music.ui.activity.MainActivity;
import com.media.music.ui.adapter.PlayPagerAdapter;
import com.media.music.ui.adapter.PlayqueueSongsAdapter;
import com.media.music.ui.dialogs.PlayqueueDialog;
import com.media.music.util.ATEUtil;
import com.media.music.util.ColorUtil;
import com.media.music.util.CoverLoader;
import com.media.music.util.ListenerUtil;
import com.media.music.util.NavigationUtil;
import com.media.music.widget.AlbumCoverView;
import com.media.music.widget.DividerItemDecoration;
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

public class PlayerFragment extends BaseFragment implements ViewPager.OnPageChangeListener, QuickControlsContract.View,
  PlayqueueSongContract.View {

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
  @BindView(R.id.popup_menu)
  ImageView popupMenu;
  @BindView(R.id.heart)
  ImageView favorite;

  private AlbumCoverView mAlbumCoverView;
  private RecyclerView recyQueuePlay;
  private LrcView mLrcViewSingle;
  private LrcView mLrcViewFull;
  private List<View> mViewPagerContent;
  private PlayqueueDialog.PlayMode mPlayMode;
  private boolean mIsFavorite = false;
  private int blackWhiteColor;
  private Palette.Swatch mSwatch;
  private PlayqueueSongsAdapter mAdapter;

  @Inject
  QuickControlsContract.Presenter mPresenter;
  @Inject
  PlayqueueSongContract.Presenter mPresenterQueue;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependences();
    mPresenter.attachView(this);
    mPresenterQueue.attachView(this);
  }

  @Override
  protected int getViewLayout() {
    return R.layout.fragment_player;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new PlayqueueSongsAdapter((AppCompatActivity) getActivity(), null);
    initViewPager();
    subscribeFavourateSongEvent();
    mPresenter.updateNowPlayingCard();
    mPresenter.loadLyric();
    subscribeMetaChangedEvent();
    initViewPlayMode();
    setSeekBarListener();
    setUpPopupMenu(popupMenu);
    mPresenterQueue.subscribe();
  }

  private void setSeekBarListener() {
    if (sbProgress != null)
      sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          if (fromUser) {
            sbProgress.removeCallbacks(mUpdateProgress);
          }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
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
      .playqueueSongModule(new PlayqueueSongModule())
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
            favorite.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
          } else {
            favorite.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
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
    View playQueue = LayoutInflater.from(getContext()).inflate(R.layout.dialog_playqueue, new LinearLayout(getContext()), false);
    mAlbumCoverView = (AlbumCoverView) coverView.findViewById(R.id.album_cover_view);
    mLrcViewSingle = (LrcView) coverView.findViewById(R.id.lrc_view_single);
    mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
    mAlbumCoverView.initNeedle(MusicPlayer.isPlaying());
    recyQueuePlay = (RecyclerView) playQueue.findViewById(R.id.recycler_view_songs);
    recyQueuePlay.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyQueuePlay.setAdapter(mAdapter);
    recyQueuePlay.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false));

    mViewPagerContent = new ArrayList<>(3);
    mViewPagerContent.add(playQueue);
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

  @OnClick(R.id.iv_back)
  public void onBack() {
    ((MainActivity) getActivity()).onHidePlayer();
  }

  @OnClick(R.id.heart)
  public void onFavoriteClick() {
    if (mIsFavorite) {
      int num = FavoriteSong.getInstance(getContext()).removeFavoriteSong(new long[]{MusicPlayer.getCurrentAudioId()});
      if (num == 1) {
        favorite.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        mIsFavorite = false;
        RxBus.getInstance().post(new FavourateSongEvent());
        Toast.makeText(getContext(), R.string.remove_favorite_success, Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getContext(), R.string.remove_favorite_fail, Toast.LENGTH_SHORT).show();
      }
    } else {
      int num = FavoriteSong.getInstance(getContext()).addFavoriteSong(new long[]{MusicPlayer.getCurrentAudioId()});
      if (num == 1) {
        favorite.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mIsFavorite = true;
        RxBus.getInstance().post(new FavourateSongEvent());
        Toast.makeText(getContext(), R.string.add_favorite_success, Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getContext(), R.string.add_favorite_fail, Toast.LENGTH_SHORT).show();
      }
    }
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
  public void setAlbumArt(Bitmap albumArt) {}

  @Override
  public void setAlbumArt(Drawable albumArt) {}

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
    mSwatch = ColorUtil.getMostPopulousSwatch(palette);
    int paletteColor;
    if (mSwatch != null) {
      paletteColor = mSwatch.getRgb();
      int artistColor = mSwatch.getTitleTextColor();
    } else {
      mSwatch = palette.getMutedSwatch() == null ? palette.getVibrantSwatch() : palette.getMutedSwatch();
      if (mSwatch != null) {
        paletteColor = mSwatch.getRgb();
        int artistColor = mSwatch.getTitleTextColor();
      } else {
        paletteColor = ATEUtil.getThemeAlbumDefaultPaletteColor(getContext());
      }

    }
    blackWhiteColor = ColorUtil.getBlackWhiteColor(paletteColor);
    mIsFavorite = FavoriteSong.getInstance(getContext()).isFavorite(MusicPlayer.getCurrentAudioId());
    if (mIsFavorite) {
      favorite.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
    } else {
      favorite.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
    }
    mAdapter.setPaletteSwatch(mSwatch);
  }

  @Override
  public void showLyric(File file) {
    if (file != null) {
      mLrcViewSingle.loadLrc(file);
      mLrcViewFull.loadLrc(file);
    }
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
  public void onSongNull(boolean isNull) {}

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.unsubscribe();
    mPresenterQueue.unsubscribe();
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

  private void setUpPopupMenu(ImageView popupMenu) {
    popupMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final PopupMenu menu = new PopupMenu(getContext(), v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
              case R.id.popup_song_goto_album:
                ((MainActivity) getActivity()).onHidePlayer();
                NavigationUtil.navigateToAlbum(getActivity(), MusicPlayer.getCurrentAlbumId(),
                  MusicPlayer.getAlbumName(), null);
                break;
              case R.id.popup_song_goto_artist:
                ((MainActivity) getActivity()).onHidePlayer();
                NavigationUtil.navigateToAlbum(getActivity(), MusicPlayer.getCurrentArtistId(),
                  MusicPlayer.getArtistName(), null);
                break;
              case R.id.popup_song_addto_playlist:
                ((MainActivity) getActivity()).onHidePlayer();
                ListenerUtil.showAddPlaylistDialog(getActivity(), new long[]{MusicPlayer.getCurrentAudioId()});
                break;
              case R.id.popup_song_delete:
                long[] deleteIds = {MusicPlayer.getCurrentAudioId()};
                ListenerUtil.showDeleteDialog(getContext(), MusicPlayer.getTrackName(), deleteIds,
                  new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                      ((MainActivity) getActivity()).onHidePlayer();
                    }
                  });
                break;
            }
            return false;
          }
        });
        menu.inflate(R.menu.menu_now_playing);
        menu.show();
      }
    });
  }

  @Override
  public void showSongs(List<Song> songs) {
    mAdapter.setSongList(songs);
  }
}
