package com.example.wearch.lib.debouncer;

import com.example.wearch.lib.Settimeout;

public class DebouncerListener {
  final public String type;
  final public Settimeout timer;

  public DebouncerListener(String type, Settimeout timer) {
    this.type = type;
    this.timer = timer;
  }
}
