package com.media.music.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;
import com.media.music.Constants;
import com.media.music.MediaPlayerApp;
import com.media.music.R;
import com.media.music.RxBus;
import com.media.music.event.MetaChangedEvent;
import com.media.music.injector.component.ApplicationComponent;
import com.media.music.injector.component.ArtistSongsComponent;
import com.media.music.injector.component.DaggerArtistSongsComponent;
import com.media.music.injector.module.ArtistSongModule;
import com.media.music.mvp.contract.ArtistSongContract;
import com.media.music.mvp.model.Song;
import com.media.music.ui.adapter.ArtistSongAdapter;
import com.media.music.util.ATEUtil;
import com.media.music.widget.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistMusicFragment extends Fragment implements ArtistSongContract.View {

  @Inject
  ArtistSongContract.Presenter mPresenter;
  @BindView(R.id.recycler_view_songs)
  RecyclerView songsRecyclerview;
  private long artistID = -1;
  ArtistSongAdapter mSongAdapter;

  public static ArtistMusicFragment newInstance(long id) {
    ArtistMusicFragment fragment = new ArtistMusicFragment();
    Bundle args = new Bundle();
    args.putLong(Constants.ARTIST_ID, id);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependences();
    mPresenter.attachView(this);
    if (getArguments() != null) {
      artistID = getArguments().getLong(Constants.ARTIST_ID);
    }
  }

  private void injectDependences() {
    ApplicationComponent applicationComponent = ((MediaPlayerApp) getActivity().getApplication()).getApplicationComponent();
    ArtistSongsComponent artistSongsComponent = DaggerArtistSongsComponent.builder()
      .applicationComponent(applicationComponent)
      .artistSongModule(new ArtistSongModule())
      .build();
    artistSongsComponent.inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_artist_music, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    ATE.apply(this, ATEUtil.getATEKey(getActivity()));

    songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
    songsRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, true));
    mSongAdapter = new ArtistSongAdapter(getActivity(), null, artistID);
    songsRecyclerview.setAdapter(mSongAdapter);

    mPresenter.subscribe(artistID);
    subscribeMetaChangedEvent();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.unsubscribe();
    RxBus.getInstance().unSubscribe(this);
  }


  @Override
  public void showSongs(List<Song> songList) {
//         adding one dummy song to top of arraylist
//        there will be albums header at this position in recyclerview
    songList.add(0, new Song(-1, -1, -1, "dummy", "dummy", "dummy", -1, -1));
    mSongAdapter.setSongList(songList);
  }

  private void subscribeMetaChangedEvent() {
    Subscription subscription = RxBus.getInstance()
      .toObservable(MetaChangedEvent.class)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .distinctUntilChanged()
      .subscribe(new Action1<MetaChangedEvent>() {
        @Override
        public void call(MetaChangedEvent event) {
          mSongAdapter.notifyDataSetChanged();
        }
      }, new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {

        }
      });
    RxBus.getInstance().addSubscription(this, subscription);
  }
}
