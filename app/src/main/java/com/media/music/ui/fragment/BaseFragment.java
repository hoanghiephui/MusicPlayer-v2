package com.media.music.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hoanghiep on 5/3/17.
 */

public abstract class BaseFragment extends Fragment {
  protected Unbinder mBinder;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(getViewLayout(), container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    injectViews(view);
  }

  private void injectViews(View view) {
    mBinder = ButterKnife.bind(this, view);
  }

  protected abstract int getViewLayout();

  @Override
  public void onDetach() {
    super.onDetach();
    if (mBinder != null) {
      mBinder.unbind();
    }
  }

  protected void startFragment(int fragmentID, Fragment fragment) {
    FragmentManager fm = getChildFragmentManager();
    Fragment f = fm.findFragmentById(fragmentID);

    if (null == f) {
      fm.beginTransaction()
              .add(fragmentID, fragment)
              .commitAllowingStateLoss();
    } else {
      fm.beginTransaction()
              .replace(fragmentID, fragment)
              .commitAllowingStateLoss();
    }
  }

}
