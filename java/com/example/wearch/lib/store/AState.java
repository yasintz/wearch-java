package com.example.wearch.lib.store;

import java.util.ArrayList;

public class AState<T> extends StoreEventDispatcher<ArrayList<T>> {
  private ArrayList<T> _prop;

  private final ArrayList<T> initialValue;

  public AState(ArrayList<T> initialVal) {
    _prop = (ArrayList<T>) initialVal.clone();
    this.initialValue = (ArrayList<T>) initialVal.clone();
    updateHandlers();
  }

  public ArrayList<T> getState() {
    return _prop;
  }

  public void addState(T p) {
    _prop.add(p);
    updateHandlers();
  }

  public void setInitialValue() {
    _prop = (ArrayList<T>) initialValue.clone();
    updateHandlers();
  }

  public void remove(T p) {
    _prop.remove(p);
    updateHandlers();
  }

  public void reset() {
    removeAllListeners();
    setInitialValue();
  }

  public void clear() {
    _prop.clear();
    updateHandlers();
  }

  private void updateHandlers() {
    dispatchAll(_prop);
  }

  @Override
  public void addEventListener(IEventHandler<ArrayList<T>> handler) {
    super.addEventListener(handler);
    handler.callback(_prop, endRunnable(handler));
  }

  private Runnable endRunnable(final IEventHandler<ArrayList<T>> handler) {
    return new Runnable() {
      @Override
      public void run() {
        removeEventListener(handler);
      }
    };
  }
}
