package com.media.music.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATEActivity;
import com.media.music.IMusicPlayerService;
import com.media.music.MusicPlayer;
import com.media.music.MusicService;
import com.media.music.R;
import com.media.music.RxBus;
import com.media.music.event.MetaChangedEvent;
import com.media.music.ui.fragment.QuickControlsFragment;
import com.media.music.util.ATEUtil;
import com.media.music.util.ListenerUtil;
import com.media.music.util.NavigationUtil;

import java.lang.ref.WeakReference;

import static com.media.music.MusicPlayer.mService;

/**
 * Created by Hoang Hiep on 2016/11/7.
 */

public class BaseActivity extends ATEActivity implements ServiceConnection {

  private MusicPlayer.ServiceToken mToken;
  private PlaybackStatus mPlaybackStatus;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mToken = MusicPlayer.bindToService(this, this);

    mPlaybackStatus = new PlaybackStatus(this);

    if (Build.VERSION.SDK_INT >= 21) {
      View decorView = getWindow().getDecorView();
      int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      decorView.setSystemUiVisibility(option);
      getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

  }

  @Override
  protected void onStart() {
    super.onStart();

    final IntentFilter filter = new IntentFilter();
    // Play and pause changes
    filter.addAction(MusicService.PLAYSTATE_CHANGED);
    // Track changes
    filter.addAction(MusicService.META_CHANGED);
    // Update a list, probably the playlist fragment's
    filter.addAction(MusicService.REFRESH);
    // If a playlist has changed, notify us
    filter.addAction(MusicService.PLAYLIST_CHANGED);
    // If there is an error playing a track
    filter.addAction(MusicService.TRACK_ERROR);

    registerReceiver(mPlaybackStatus, filter);

  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    mService = IMusicPlayerService.Stub.asInterface(service);
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    mService = null;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Unbind from the service
    if (mToken != null) {
      MusicPlayer.unbindFromService(mToken);
      mToken = null;
    }

    try {
      unregisterReceiver(mPlaybackStatus);
    } catch (final Throwable e) {
      e.printStackTrace();
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    if (!ListenerUtil.hasEffectsPanel(BaseActivity.this)) {
      menu.removeItem(R.id.action_equalizer);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        super.onBackPressed();
        return true;
      case R.id.action_settings:
        NavigationUtil.navigateToSettings(this);
        return true;
      case R.id.action_equalizer:
        NavigationUtil.navigateToEqualizer(this);
        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  @Nullable
  @Override
  public String getATEKey() {
    return ATEUtil.getATEKey(this);
  }

  private final static class PlaybackStatus extends BroadcastReceiver {

    private final WeakReference<BaseActivity> mReference;


    public PlaybackStatus(final BaseActivity activity) {
      mReference = new WeakReference<BaseActivity>(activity);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
      final String action = intent.getAction();
      BaseActivity baseActivity = mReference.get();
      if (baseActivity != null) {
        if (action.equals(MusicService.META_CHANGED)) {
          MetaChangedEvent metaChangedEvent = new MetaChangedEvent(MusicPlayer.getCurrentAudioId(),
            MusicPlayer.getTrackName(), MusicPlayer.getArtistName());
          RxBus.getInstance().post(metaChangedEvent);
        } else if (action.equals(MusicService.TRACK_ERROR)) {
          final String errorMsg = context.getString(R.string.error_playing_track);
          Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  class initQuickControls extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      QuickControlsFragment fragment1 = new QuickControlsFragment();
      FragmentManager fragmentManager1 = getSupportFragmentManager();
      fragmentManager1.beginTransaction()
        .replace(R.id.quickcontrols_container, fragment1).commitAllowingStateLoss();
      return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {
    }
  }
}
