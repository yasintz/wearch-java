package com.example.wearch.lib;

import java.util.Timer;
import java.util.TimerTask;


public class Settimeout {
  private Timer timer;

  public boolean isStart() {
    return isStart;
  }

  private boolean isStart;
  private final int delay;
  private final Runnable body;

  public Settimeout(Runnable callback,int delay) {
    this.delay = delay;
    this.body = callback;
    timer = new Timer();
    isStart = false;
  }

  public void startIfNotStart() {
    if (!isStart) {
      isStart = true;
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          body.run();
        }
      }, delay);
    }
  }

  public void stopIfNotStop() {
    if (isStart) {
      isStart = false;
      timer.cancel();
      timer = new Timer();
    }
  }
}