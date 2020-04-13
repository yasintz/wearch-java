package com.example.wearch.lib.debouncer;


import com.example.wearch.lib.STATIC;
import com.example.wearch.lib.Settimeout;
import com.example.wearch.lib.eventbus.EventBus;

import java.util.ArrayList;

public class Debouncer {
  private final ArrayList<DebouncerListener> debounces;

  private Debouncer() {
    debounces = new ArrayList<>();
  }

  private static final Debouncer ourInstances = new Debouncer();

  public static Debouncer getInstance() {
    return ourInstances;
  }

  public void trigger(String type) {
    for (DebouncerListener dl : debounces) {
      if (dl.type.equals(type)) {
        dl.timer.stopIfNotStop();
        dl.timer.startIfNotStart();
      }
    }
  }

  public void debounce(String type, Runnable callback, int delay) {
    debounces.add(new DebouncerListener(type, new Settimeout(callback, delay)));
  }

  public void debounce(String type, Runnable callback, int delay, boolean initiallyStart) {
    Settimeout st = new Settimeout(callback, delay);
    if (initiallyStart) {
      st.startIfNotStart();
    }
    debounces.add(new DebouncerListener(type, st));
  }

  private void start(String type) {
    for (DebouncerListener dl : debounces) {
      if (dl.type.equals(type)) {
        dl.timer.startIfNotStart();
      }
    }
  }

  private void stop(String type) {
    for (DebouncerListener dl : debounces) {
      if (dl.type.equals(type)) {
        dl.timer.stopIfNotStop();
      }
    }
  }
}
