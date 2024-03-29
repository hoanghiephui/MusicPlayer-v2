package com.media.music.api;

import com.media.music.api.model.KuGouRawLyric;
import com.media.music.api.model.KuGouSearchLyricResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Hoang Hiep on 2017/1/20.
 */

public interface KuGouApiService {

  @GET("search?ver=1&man=yes&client=pc")
  Observable<KuGouSearchLyricResult> searchLyric(@Query("keyword") String songName, @Query("duration") String duration);

  @GET("download?ver=1&client=pc&fmt=lrc&charset=utf8")
  Observable<KuGouRawLyric> getRawLyric(@Query("id") String id, @Query("accesskey") String accesskey);
}
