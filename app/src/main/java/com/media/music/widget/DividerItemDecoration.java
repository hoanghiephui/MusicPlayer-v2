package com.media.music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.media.music.R;
import com.media.music.util.ATEUtil;
import com.media.music.util.DensityUtil;


public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
  public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
  private static final int[] ATTRS = new int[]{
    android.R.attr.listDivider
  };
  private Drawable mDivider;

  private int mOrientation;
  private boolean withHeader;

  public DividerItemDecoration(Context context, int orientation, boolean withHeader) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    if (ATEUtil.getATEKey(context).equals("light_theme")) {
      mDivider = ContextCompat.getDrawable(context, R.drawable.item_divider_black);
    } else {
      mDivider = ContextCompat.getDrawable(context, R.drawable.item_divider_white);
    }
    this.withHeader = withHeader;
    a.recycle();
    setOrientation(orientation);
  }

  public DividerItemDecoration(Context context, int orientation, int resId) {
    mDivider = ContextCompat.getDrawable(context, resId);
    setOrientation(orientation);
  }

  private void setOrientation(int orientation) {
    if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
      throw new IllegalArgumentException("invalid orientation");
    }
    mOrientation = orientation;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent) {
    if (mOrientation == VERTICAL_LIST) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  private void drawVertical(Canvas c, RecyclerView parent) {
    int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount - 1; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
        .getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + mDivider.getIntrinsicHeight();
      if (i != 0 || (!withHeader)) {
        left = parent.getPaddingLeft() + DensityUtil.dip2px(parent.getContext(), 72);
      }
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  private void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
        .getLayoutParams();
      final int left = child.getRight() + params.rightMargin;
      final int right = left + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
    if (mOrientation == VERTICAL_LIST) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }
}