package com.media.music.ui.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
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
import com.media.music.injector.component.DaggerFolderSongsComponent;
import com.media.music.injector.component.FolderSongsComponent;
import com.media.music.injector.module.FolderSongsModule;
import com.media.music.mvp.contract.FolderSongsContract;
import com.media.music.mvp.model.Song;
import com.media.music.ui.adapter.SongsListAdapter;
import com.media.music.util.ATEUtil;
import com.media.music.util.DensityUtil;
import com.media.music.widget.DividerItemDecoration;
import com.media.music.widget.fastscroller.FastScrollRecyclerView;

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
public class FolderSongsFragment extends Fragment implements FolderSongsContract.View {

  @Inject
  FolderSongsContract.Presenter mPresenter;
  @BindView(R.id.recyclerview)
  FastScrollRecyclerView recyclerView;
  @BindView(R.id.toolbar)
  Toolbar toolbar;
  private SongsListAdapter mAdapter;
  private String path;

  public static FolderSongsFragment newInstance(String path) {

    Bundle args = new Bundle();
    args.putString(Constants.FOLDER_PATH, path);

    FolderSongsFragment fragment = new FolderSongsFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectDependences();
    mPresenter.attachView(this);

    if (getArguments() != null) {
      path = getArguments().getString(Constants.FOLDER_PATH);
    }

    mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), null, Constants.NAVIGATE_ALLSONG, true);
  }

  private void injectDependences() {
    ApplicationComponent applicationComponent = ((MediaPlayerApp) getActivity().getApplication()).getApplicationComponent();
    FolderSongsComponent folderSongsComponent = DaggerFolderSongsComponent.builder()
      .applicationComponent(applicationComponent)
      .folderSongsModule(new FolderSongsModule())
      .build();
    folderSongsComponent.inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ATE.apply(this, ATEUtil.getATEKey(getActivity()));

    if (Build.VERSION.SDK_INT < 21 && view.findViewById(R.id.status_bar) != null) {
      view.findViewById(R.id.status_bar).setVisibility(View.GONE);
      if (Build.VERSION.SDK_INT >= 19) {
        int statusBarHeight = DensityUtil.getStatusBarHeight(getContext());
        view.findViewById(R.id.toolbar).setPadding(0, statusBarHeight, 0, 0);
      }
    }

    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setTitle(R.string.folders);

    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(mAdapter);
    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, true));

    mPresenter.loadSongs(path);
    subscribeMetaChangedEvent();
  }

  @Override
  public void onActivityCreated(final Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mPresenter.unsubscribe();
    RxBus.getInstance().unSubscribe(this);
  }

  @Override
  public void showSongs(List<Song> songList) {
    mAdapter.setSongList(songList);
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
          mAdapter.notifyDataSetChanged();
        }
      }, new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {

        }
      });
    RxBus.getInstance().addSubscription(this, subscription);
  }
}
