package com.example.wearch.lib.store;

public class LoadingStatus {
  private boolean show = true;
  private float opacityRange = 0.2f;
  private static final LoadingStatus ourInstance = new LoadingStatus();

  public static LoadingStatus getInstance() {
    return ourInstance;
  }

  private LoadingStatus() {
  }

  public boolean isShow() {
    return show;
  }

  public float getOpacityRange() {
    return opacityRange;
  }

  public void setLoadingStatus(boolean showStatus, float opacityRange) {
    this.show = showStatus;
    this.opacityRange = opacityRange;
  }
    public void setLoadingStatus(boolean showStatus) {
    this.show = showStatus;
  }
}
