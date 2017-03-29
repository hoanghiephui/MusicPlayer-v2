package com.media.music.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hoang Hiep on 2016/11/3.
 */

public class ArtistInfo {

  private static final String ARTIST = "artist";

  @Expose
  @SerializedName(ARTIST)
  public LastfmArtist mArtist;
}
