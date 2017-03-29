package com.media.music.injector.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.media.music.Constants;
import com.media.music.MediaPlayerApp;
import com.media.music.injector.scope.PerApplication;
import com.media.music.respository.RepositoryImpl;
import com.media.music.respository.interfaces.Repository;
import com.media.music.util.FileUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hoang Hiep on 2016/11/1.
 */
@Module
public class NetworkModule {
  private final MediaPlayerApp mMediaPlayerApp;

  public NetworkModule(MediaPlayerApp mediaPlayerApp) {
    this.mMediaPlayerApp = mediaPlayerApp;
  }

  @Provides
  @PerApplication
  Repository provideRepository(@Named("kugou") Retrofit kugou, @Named("lastfm") Retrofit lastfm) {
    return new RepositoryImpl(mMediaPlayerApp, kugou, lastfm);
  }

  @Provides
  @Named("lastfm")
  @PerApplication
  Retrofit provideLastFMRetrofit() {
    String endpointUrl = Constants.BASE_API_URL_LASTFM;
    Gson gson = new GsonBuilder().create();
    GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder()
      .cache(new Cache(FileUtil.getHttpCacheDir(mMediaPlayerApp.getApplicationContext()), Constants.HTTP_CACHE_SIZE))
      .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
      .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
      .build();
//        OkHttpClient newClient = client.newBuilder().addInterceptor(loggingInterceptor).build();

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(endpointUrl)
      .client(client)
      .addConverterFactory(gsonConverterFactory)
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .build();
    return retrofit;
  }

  @Provides
  @Named("kugou")
  @PerApplication
  Retrofit provideKuGouRetrofit() {
    String endpointUrl = Constants.BASE_API_URL_KUGOU;
    Gson gson = new GsonBuilder().create();
    GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder()
      .cache(new Cache(FileUtil.getHttpCacheDir(mMediaPlayerApp.getApplicationContext()), Constants.HTTP_CACHE_SIZE))
      .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
      .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
      .build();
//        OkHttpClient newClient = client.newBuilder().addInterceptor(loggingInterceptor).build();

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(endpointUrl)
      .client(client)
      .addConverterFactory(gsonConverterFactory)
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .build();
    return retrofit;
  }

}
