package com.media.music.widget.timely.model;


import com.media.music.widget.timely.model.number.Eight;
import com.media.music.widget.timely.model.number.Five;
import com.media.music.widget.timely.model.number.Four;
import com.media.music.widget.timely.model.number.Nine;
import com.media.music.widget.timely.model.number.Null;
import com.media.music.widget.timely.model.number.One;
import com.media.music.widget.timely.model.number.Seven;
import com.media.music.widget.timely.model.number.Six;
import com.media.music.widget.timely.model.number.Three;
import com.media.music.widget.timely.model.number.Two;
import com.media.music.widget.timely.model.number.Zero;

import java.security.InvalidParameterException;

public class NumberUtils {

  public static float[][] getControlPointsFor(int start) {
    switch (start) {
      case (-1):
        return Null.getInstance().getControlPoints();
      case 0:
        return Zero.getInstance().getControlPoints();
      case 1:
        return One.getInstance().getControlPoints();
      case 2:
        return Two.getInstance().getControlPoints();
      case 3:
        return Three.getInstance().getControlPoints();
      case 4:
        return Four.getInstance().getControlPoints();
      case 5:
        return Five.getInstance().getControlPoints();
      case 6:
        return Six.getInstance().getControlPoints();
      case 7:
        return Seven.getInstance().getControlPoints();
      case 8:
        return Eight.getInstance().getControlPoints();
      case 9:
        return Nine.getInstance().getControlPoints();
      default:
        throw new InvalidParameterException("Unsupported number requested");
    }
  }
}
