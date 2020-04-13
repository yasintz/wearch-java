package com.example.wearch.youtube.video;

import com.example.wearch.lib.eventbus.EventBus;
import com.example.wearch.lib.store.IEventHandler;
import com.example.wearch.lib.store.Store;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

public class YouTubeController implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.OnFullscreenListener {


  private YouTubePlayer _youTubePlayer;
  private Store _store = Store.getInstance();
  private EventBus _eventBus = EventBus.getInstance();

  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
    if (!wasRestored) {
      _youTubePlayer = player;
      _youTubePlayer.setOnFullscreenListener(this);
      _youTubePlayer.setPlaybackEventListener(this);
      _youTubePlayer.setPlayerStateChangeListener(this);
      _youTubePlayer.setShowFullscreenButton(false);
      _store.youTubeStore.isInit.setState(true);
      _eventBus.emit("youtube-player-initialization-success");

    }
  }

  @Override
  public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

  }

  public YouTubePlayer getYouTubePlayer() {
    return _youTubePlayer;
  }

  public void play() {
    if (_store.youTubeStore.isInit.getState()) {
      _youTubePlayer.play();
      _store.youTubeStore.isPlaying.setState(true);
    }
  }

  public void pause() {
    if (_store.youTubeStore.isInit.getState()) {
      _youTubePlayer.pause();
      _store.youTubeStore.isPlaying.setState(false);
    }
  }

  public void finish() {
    seekToMillis(getDurationMillis());
    _store.youTubeStore.isPlaying.setState(false);
  }

  public boolean isPlaying() {
    return _youTubePlayer.isPlaying();
  }

  private boolean hasNext() {
    return _youTubePlayer.hasNext();
  }

  private boolean hasPrevious() {
    return _youTubePlayer.hasPrevious();
  }

  public void next() {
    if (hasNext()) {
      _youTubePlayer.next();
    }
  }

  public void previous() {
    if (hasPrevious()) {
      _youTubePlayer.previous();
    }
  }

  public int getCurrentTimeMillis() {
    return _youTubePlayer.getCurrentTimeMillis();
  }

  private int getDurationMillis() {
    return _youTubePlayer.getDurationMillis();
  }

  public void seekToMillis(int ms) {
    if (_store.youTubeStore.isInit.getState()) {
      _youTubePlayer.seekToMillis(ms);
    }
  }

  public void seekRelativeMillis(int ms) {
    _youTubePlayer.seekRelativeMillis(ms);
  }

  public void setFullscreen(boolean fullscreen) {
    _youTubePlayer.setFullscreen(fullscreen);
  }

  public void setFullscreenControlFlags(int i) {

  }

  public int getFullscreenControlFlags() {
    return _youTubePlayer.getFullscreenControlFlags();
  }

  public void addFullscreenControlFlag(int i) {

  }

  public void setPlayerStyle(YouTubePlayer.PlayerStyle playerStyle) {

  }

  public void setShowFullscreenButton(boolean isShow) {
    _youTubePlayer.setShowFullscreenButton(isShow);
  }

  public void setManageAudioFocus(boolean b) {

  }

  @Override
  public void onFullscreen(boolean isFullScreen) {

  }

  @Override
  public void onPlaying() {
    _store.youTubeStore.isPlaying.setState(true);
  }

  @Override
  public void onPaused() {
    _store.youTubeStore.isPlaying.setState(false);
  }

  @Override
  public void onStopped() {
    _store.youTubeStore.isPlaying.setState(false);
  }

  @Override
  public void onBuffering(boolean b) {

  }

  @Override
  public void onSeekTo(int ms) {
  }

  @Override
  public void onLoading() {
    _store.youTubeStore.isReady.setState(false);
    _store.youTubeStore.isLoading.setState(true);
  }

  @Override
  public void onLoaded(String videoId) {
    _store.youTubeStore.isReady.setState(true);
    _store.youTubeStore.isLoading.setState(false);
  }

  @Override
  public void onAdStarted() {

  }

  @Override
  public void onVideoStarted() {

  }

  @Override
  public void onVideoEnded() {

  }

  @Override
  public void onError(YouTubePlayer.ErrorReason errorReason) {

  }

  public void loadVideo() {
    onLoading();
    if (_store.youTubeStore.autoPlay.getState() && _store.youTubeStore.isInit.getState()) {
      _youTubePlayer.loadVideo(_store.youTubeStore.playingVideoId.getState());
    } else {
      _youTubePlayer.cueVideo(_store.youTubeStore.playingVideoId.getState());
    }
  }

  public void setVideoId(String videoId) {
    _store.youTubeStore.playingVideoId.setState(videoId);
  }

  public void setAutoPlay(Boolean autoPlay) {
    _store.youTubeStore.autoPlay.setState(autoPlay);
  }
}
