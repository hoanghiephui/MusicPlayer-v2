package com.media.music.mvp.usecase;


import android.support.annotation.NonNull;

import com.media.music.respository.interfaces.Repository;
import com.media.music.util.LyricUtil;

import java.io.File;

import rx.Observable;

/**
 * Created by Hoang Hiep on 2016/11/7.
 */

public class GetLyric extends UseCase<GetLyric.RequestValues, GetLyric.ResponseValue> {

  private Repository mRepository;

  public GetLyric(Repository repository) {
    this.mRepository = repository;
  }

  @Override
  public ResponseValue execute(RequestValues requestValues) {
    String title = requestValues.getTitle();
    String artist = requestValues.getArtist();
    long duration = requestValues.getDuration();
    if (LyricUtil.isLrcFileExist(title, artist)) {
      return new ResponseValue(LyricUtil.getLocalLyricFile(title, artist));
    } else {
      return new ResponseValue(mRepository.downloadLrcFile(title, artist, duration));
    }
  }

  public static final class RequestValues implements UseCase.RequestValues {

    private final String mTitle;
    private final String mArtist;
    private final long mDuration;

    public RequestValues(@NonNull String title, @NonNull String artist, long duration) {
      mTitle = title;
      mArtist = artist;
      mDuration = duration;
    }

    public String getTitle() {
      return mTitle;
    }

    public String getArtist() {
      return mArtist;
    }

    public long getDuration() {
      return mDuration;
    }
  }

  public static final class ResponseValue implements UseCase.ResponseValue {

    private final Observable<File> mLyricFile;

    public ResponseValue(Observable<File> lyricFile) {
      mLyricFile = lyricFile;
    }

    public Observable<File> getLyricFile() {
      return mLyricFile;
    }
  }
}
