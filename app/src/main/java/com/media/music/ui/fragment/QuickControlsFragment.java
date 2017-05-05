package com.media.music.ui.fragment;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.media.music.MediaPlayerApp;
import com.media.music.MusicPlayer;
import com.media.music.R;
import com.media.music.RxBus;
import com.media.music.event.MetaChangedEvent;
import com.media.music.injector.component.ApplicationComponent;
import com.media.music.injector.component.DaggerQuickControlsComponent;
import com.media.music.injector.component.QuickControlsComponent;
import com.media.music.injector.module.ActivityModule;
import com.media.music.injector.module.QuickControlsModule;
import com.media.music.listener.PaletteColorChangeListener;
import com.media.music.mvp.contract.QuickControlsContract;
import com.media.music.util.ATEUtil;
import com.media.music.util.ColorUtil;
import com.media.music.util.ScrimUtil;
import com.media.music.widget.ForegroundImageView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuickControlsFragment extends Fragment implements QuickControlsContract.View {

  @Inject
  QuickControlsContract.Presenter mPresenter;
  @BindView(R.id.topContainer)
  public View topContainer;
  @BindView(R.id.song_progress_normal)
  ProgressBar mProgress;
  @BindView(R.id.play_pause)
  ImageView mPlayPauseView;
  @BindView(R.id.title)
  TextView mTitle;
  @BindView(R.id.artist)
  TextView mArtist;
  @BindView(R.id.album_art)
  ForegroundImageView mAlbumArt;
  @BindView(R.id.next)
  ImageView next;

  private static PaletteColorChangeListener sListener;
  private boolean isSongNull = false;

  private Runnable mUpdateProgress = new Runnable() {

    @Override
    public void run() {

      long position = MusicPlayer.position();
      mProgress.setProgress((int) position);
      if (MusicPlayer.isPlaying()) {
        mProgress.postDelayed(mUpdateProgress, 50);
      } else mProgress.removeCallbacks(this);

    }
  };

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependences();
    mPresenter.attachView(this);
  }

  private void injectDependences() {
    ApplicationComponent applicationComponent = ((MediaPlayerApp) getActivity().getApplication())
      .getApplicationComponent();
    QuickControlsComponent quickControlsComponent = DaggerQuickControlsComponent.builder()
      .applicationComponent(applicationComponent)
      .activityModule(new ActivityModule(getActivity()))
      .quickControlsModule(new QuickControlsModule())
      .build();
    quickControlsComponent.inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.include_play_bar, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ATE.apply(this, ATEUtil.getATEKey(getActivity()));

    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mProgress.getLayoutParams();
    mProgress.measure(0, 0);
    mProgress.setLayoutParams(layoutParams);
    ScaleDrawable scaleDrawable = (ScaleDrawable) ((LayerDrawable) mProgress.getProgressDrawable()).findDrawableByLayerId(R.id.progress);
    GradientDrawable gradientDrawable = (GradientDrawable) scaleDrawable.getDrawable();
    int colorAccent = ATEUtil.getThemeAccentColor(getActivity());
    if (gradientDrawable != null) {
      gradientDrawable.setColors(new int[]{colorAccent, colorAccent, colorAccent});
    }
    if (mPlayPauseView != null) {
      if (MusicPlayer.isPlaying())
        mPlayPauseView.setSelected(true);
      else mPlayPauseView.setSelected(false);
    }
    subscribeMetaChangedEvent();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.unsubscribe();
    sListener = null;
    RxBus.getInstance().unSubscribe(this);
  }

  @Override
  public void showLyric(File file) {
  }

  @Override
  public void setPlayPauseButton(boolean isPlaying) {
    if (isPlaying) {
      mPlayPauseView.setSelected(true);
    } else {
      mPlayPauseView.setSelected(false);
    }
  }

  @Override
  public boolean getPlayPauseStatus() {
    return mPlayPauseView.isSelected();
  }

  @Override
  public void startUpdateProgress() {
    mProgress.postDelayed(mUpdateProgress, 10);
  }

  @Override
  public void setProgressMax(int max) {
    mProgress.setMax(max);
  }

  @Override
  public void onSongNull(boolean isNull) {
    isSongNull = isNull;
  }

  @Override
  public void setAlbumArt(Bitmap albumArt) {
    mAlbumArt.setImageBitmap(albumArt);
  }

  @Override
  public void setAlbumArt(Drawable albumArt) {
    mAlbumArt.setImageDrawable(albumArt);
    if (TextUtils.isEmpty(MusicPlayer.getTrackName()) && TextUtils.isEmpty(MusicPlayer.getArtistName())) {
      mAlbumArt.setForeground(null);
      TypedValue paletteColor = new TypedValue();
      getContext().getTheme().resolveAttribute(R.attr.album_default_palette_color, paletteColor, true);
      topContainer.setBackgroundColor(paletteColor.data);
      mPlayPauseView.setColorFilter(ATEUtil.getThemeAccentColor(getActivity()));
      mPlayPauseView.setEnabled(false);
      next.setEnabled(false);
      next.setColorFilter(ATEUtil.getThemeAccentColor(getContext()));
      if (sListener != null) {
        sListener.onPaletteColorChange(paletteColor.data, ATEUtil.getThemeAccentColor(getActivity()));
      }
    }
  }

  @Override
  public void setAlbumArt(String url) {
  }

  @Override
  public void setTitle(String title) {
    mTitle.setText(title);
  }

  @Override
  public void setArtist(String artist) {
    mArtist.setText(artist);
  }

  @Override
  public void setPalette(Palette palette) {
    Palette.Swatch mSwatch = ColorUtil.getMostPopulousSwatch(palette);
    int paletteColor;
    if (mSwatch != null) {
      paletteColor = mSwatch.getRgb();
      int artistColor = mSwatch.getTitleTextColor();
      mTitle.setTextColor(ColorUtil.getOpaqueColor(artistColor));
      mArtist.setTextColor(artistColor);
    } else {
      mSwatch = palette.getMutedSwatch() == null ? palette.getVibrantSwatch() : palette.getMutedSwatch();
      if (mSwatch != null) {
        paletteColor = mSwatch.getRgb();
        int artistColor = mSwatch.getTitleTextColor();
        mTitle.setTextColor(ColorUtil.getOpaqueColor(artistColor));
        mArtist.setTextColor(artistColor);
      } else {
        paletteColor = ATEUtil.getThemeAlbumDefaultPaletteColor(getContext());
        mTitle.setTextColor(ContextCompat.getColor(getContext(), android.R.color.primary_text_light));
        mArtist.setTextColor(ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
      }

    }
    //set icon color
    int blackWhiteColor = ColorUtil.getBlackWhiteColor(paletteColor);
    topContainer.setBackgroundColor(paletteColor);
    mPlayPauseView.setColorFilter(blackWhiteColor);
    mPlayPauseView.setEnabled(true);
    next.setColorFilter(blackWhiteColor);
    //set albumart foreground
    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
      mAlbumArt.setForeground(
        ScrimUtil.makeCubicGradientScrimDrawable(
          paletteColor,
          8,
          Gravity.CENTER_HORIZONTAL));
    }

    if (sListener != null) {
      sListener.onPaletteColorChange(paletteColor, blackWhiteColor);
    }
  }

  @OnClick(R.id.topContainer)
  public void onUpIndicatorClick() {
    if (isSongNull) {
      Snackbar.make(topContainer, getText(R.string.song_null), Snackbar.LENGTH_LONG).show();
    } else {
      Fragment fragment = new PlayerFragment();
      FragmentTransaction transaction = getFragmentManager().beginTransaction();
      transaction.setCustomAnimations(R.anim.fragment_slide_up, 0);
      transaction.replace(android.R.id.content, fragment, PlayerFragment.class.getName()).commit();
    }
  }

  @OnClick(R.id.play_pause)
  public void onPlayPauseClick() {
    if (isSongNull) {
      Snackbar.make(topContainer, getText(R.string.song_null), Snackbar.LENGTH_LONG).show();
    } else {
      mPresenter.onPlayPauseClick();
    }
  }

  @OnClick(R.id.next)
  public void onNextClick() {
    if (isSongNull) {
      Snackbar.make(topContainer, getText(R.string.song_null), Snackbar.LENGTH_LONG).show();
    } else {
      mPresenter.onNextClick();
    }
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

        }
      });
    RxBus.getInstance().addSubscription(this, subscription);
  }

  public static void setPaletteColorChangeListener(PaletteColorChangeListener paletteColorChangeListener) {
    sListener = paletteColorChangeListener;
  }
}
