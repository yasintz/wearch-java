package com.example.wearch.lib;

import java.util.Timer;
import java.util.TimerTask;

public class SetInterval {
  private Timer timer;

  public boolean isStart() {
    return isStart;
  }

  private boolean isStart;
  private final int delay;
  private final int period;
  private final Runnable body;

  public SetInterval(Runnable callback, int delay, int period) {
    this.delay = delay;
    this.period = period;
    this.body = callback;
    timer = new Timer();
    isStart = false;
  }

  public void startIfNotStart() {
    if (!isStart) {
      isStart = true;
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          body.run();
        }
      }, delay, period);
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
