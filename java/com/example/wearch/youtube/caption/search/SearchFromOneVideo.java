package com.example.wearch.youtube.caption.search;

import com.example.wearch.lib.STATIC;
import com.example.wearch.youtube.caption.Caption;
import com.example.wearch.youtube.video.CorrectVideo;
import com.example.wearch.youtube.video.YouTubeVideo;

import java.util.ArrayList;

class SearchFromOneVideo {

  private final YouTubeVideo _videoInfo;
  private final String _searchParams;

  private boolean _isCorrect = false;
  private CorrectVideo _correctVideoInfo;

  SearchFromOneVideo(String searchParams, YouTubeVideo yt) {
    _videoInfo = yt;
    _searchParams = searchParams.replaceAll("\\p{Punct}", "");
    startSearch();
  }


  boolean getIsCorrect() {
    return _isCorrect;
  }

  private void startSearch() {
    ArrayList<Caption> captions = _videoInfo.captions;
    for (int i = 0; i < captions.size(); i++) {

      String cp = captions.get(i).text.replaceAll("\\p{Punct}", "");
      int wordIndex = cp.indexOf(_searchParams);
      if (wordIndex > 0 && cp.length() > wordIndex + _searchParams.length() + 1) {
        String wordAfter = cp.substring(
                wordIndex + _searchParams.length(),
                wordIndex + _searchParams.length() + 1
        );
        String wordBefore = cp.substring(
                wordIndex - 1,
                wordIndex
        );
        if (wordAfter.equals(" ") || wordAfter.equals("") &&
                wordBefore.equals(" ") || wordBefore.equals("")
        ) {
          _correctVideoInfo = new CorrectVideo(
                  _videoInfo,
                  i,
                  wordIndex,
                  wordIndex + _searchParams.length());
          _isCorrect = true;
        }
      }
    }
  }

  CorrectVideo getCorrectVideo() {
    return _correctVideoInfo;
  }
}

