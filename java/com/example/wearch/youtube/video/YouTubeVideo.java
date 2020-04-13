package com.example.wearch.youtube.video;

import com.example.wearch.youtube.caption.Caption;

import java.util.ArrayList;

public class YouTubeVideo {
  public final String videoId;
  public final ArrayList<Caption> captions;

  public YouTubeVideo(String videoId, ArrayList<Caption> captions) {
    this.videoId = videoId;
    this.captions = captions;
  }
}
