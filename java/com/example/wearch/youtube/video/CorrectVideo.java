package com.example.wearch.youtube.video;

public class CorrectVideo extends YouTubeVideo {
  public final int sentencePos;
  public final int wordPosStart;
  public final int wordPosEnd;

  public CorrectVideo(YouTubeVideo yt, int sentencePos, int wordPosStart, int wordPosEnd) {
    super(yt.videoId, yt.captions);
    this.sentencePos = sentencePos;
    this.wordPosStart = wordPosStart;
    this.wordPosEnd = wordPosEnd;
  }
}
