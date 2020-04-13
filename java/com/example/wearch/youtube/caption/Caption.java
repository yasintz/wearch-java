package com.example.wearch.youtube.caption;

public class Caption {
  public final Double start;
  public final Double dur;
  public final String text;
  public final Double end;

  public Caption(Double s, Double d, String t) {
    start = s * 1000;
    dur = d * 1000;
    text = t;
    end = start + dur;
  }
}
