package com.media.music.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by hoanghiep on 5/1/17.
 */

public class PlayPagerAdapter extends PagerAdapter {
  private List<View> mViews;

  public PlayPagerAdapter(List<View> views) {
    mViews = views;
  }

  @Override
  public int getCount() {
    return mViews.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    container.addView(mViews.get(position));
    return mViews.get(position);
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView(mViews.get(position));
  }
}
