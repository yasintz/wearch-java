package com.example.wearch.lib.store;

public class YouTubeStore {
  public NState<Boolean> isPlaying = new NState<>(false);
  public NState<Boolean> isReady = new NState<>(false);
  public NState<Boolean> isLoading = new NState<>(false);
  public NState<Boolean> isInit = new NState<>(false);
  public NState<Boolean> hasPrevious = new NState<>(false);
  public NState<Boolean> hasNext = new NState<>(false);
  public NState<Boolean> autoPlay = new NState<>(false);


  public NState<Integer> durationMillis = new NState<>(0);
  public NState<Integer> currentMillis= new NState<>(0);

  public NState<String> playingVideoId = new NState<>("");

  public void reset(){
    isPlaying.reset();
    isReady.reset();
    isLoading.reset();
    isInit.reset();
    hasPrevious.reset();
    hasNext.reset();
    durationMillis.reset();
    currentMillis.reset();
    playingVideoId.reset();
  }
  public void setInitialValue(){
   isPlaying.setInitialValue();
   isReady.setInitialValue();
   isLoading.setInitialValue();
   hasPrevious.setInitialValue();
   durationMillis.setInitialValue();
   currentMillis.setInitialValue();
   playingVideoId.setInitialValue();
  }
  private static final YouTubeStore ourInstance = new YouTubeStore();

  public static YouTubeStore getInstance() {
    return ourInstance;
  }

}
