package com.example.wearch.youtube.video.Fetch;

import com.android.volley.VolleyError;
import com.example.wearch.youtube.caption.Caption;
import com.example.wearch.youtube.video.YouTubeVideo;

import java.util.ArrayList;

public interface ResponseListener {
  void successful(YouTubeVideo videoInfo);
  void unsuccessful(VolleyError error);
}
