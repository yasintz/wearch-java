package com.example.wearch.lib.store;

import com.example.wearch.youtube.video.CorrectVideo;
import com.example.wearch.youtube.video.YouTubeVideo;

import java.util.ArrayList;

public class Store {
  public final NState<String> searchedText = new NState<>("");
  public final NState<String> enteredText = new NState<>("");
  public final NState<Boolean> isMenuCreated = new NState<>(false);
  public final NState<Boolean> isLoadingShown = new NState<>(false);
  public final NState<Boolean> isFetchStart = new NState<>(false);
  public final NState<Integer> playingVideoIndex = new NState<>(-1);

  public final AState<CorrectVideo> correctVideos = new AState<>(new ArrayList<CorrectVideo>());
  public final AState<YouTubeVideo> youTubeVideos = new AState<>(new ArrayList<YouTubeVideo>());


  public final YouTubeStore youTubeStore = YouTubeStore.getInstance();


  public void reset() {
    searchedText.reset();
    enteredText.reset();
    isMenuCreated.reset();
    isLoadingShown.reset();
    isFetchStart.reset();
    playingVideoIndex.reset();
    correctVideos.reset();
    youTubeStore.reset();
  }

  private static final Store ourInstance = new Store();

  public static Store getInstance() {
    return ourInstance;
  }
}
