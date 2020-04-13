package com.example.wearch.youtube.caption.search;

import com.example.wearch.lib.store.Store;
import com.example.wearch.youtube.video.CorrectVideo;
import com.example.wearch.youtube.video.YouTubeVideo;

import java.util.ArrayList;
import java.util.Random;

public class Search {
  private ArrayList<ArrayList<YouTubeVideo>> videoListByPattern = new ArrayList<>();
  private final Store _store = Store.getInstance();

  private void listMixed() {
    ArrayList<Integer> videoIndexList = new ArrayList<>();
    while (true) {
      int subIndexListCount;
      if (videoListByPattern.isEmpty()) {
        subIndexListCount = 5;
      } else {
        subIndexListCount = 3;
      }
      ArrayList<YouTubeVideo> subVideoIdList = new ArrayList<>();
      for (int i = 0; i < subIndexListCount; i++) {
        int random = new Random().nextInt(_store.youTubeVideos.getState().size());
        while (videoIndexList.contains(random)) {
          random = new Random().nextInt(_store.youTubeVideos.getState().size());
        }
        videoIndexList.add(random);
        subVideoIdList.add(_store.youTubeVideos.getState().get(random));
      }
      videoListByPattern.add(subVideoIdList);
      if (videoIndexList.size() == _store.youTubeVideos.getState().size()) return;
    }
  }

  public void againSearch() {
    videoLoop:
    for (YouTubeVideo video : _store.youTubeVideos.getState()) {
      for (CorrectVideo crVideo : _store.correctVideos.getState()) {
        if (crVideo.videoId.equals(video.videoId)) {
          continue videoLoop;
        }
      }
      SearchFromOneVideo searchInVideo = new SearchFromOneVideo(_store.searchedText.getState(), video);
      if (searchInVideo.getIsCorrect()) {
        _store.correctVideos.addState(searchInVideo.getCorrectVideo());
      }
    }
  }
}
