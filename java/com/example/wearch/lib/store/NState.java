package com.example.wearch.lib.store;


public class NState<T> extends StoreEventDispatcher<T> {
  private T _prop;
  private final T initialValue;

  public NState(final T initialValue) {
    _prop = initialValue;
    this.initialValue = initialValue;
  }

  public T getState() {
    return _prop;
  }

  public void setState(T p) {
    if (!p.equals(_prop)) {
      this._prop = p;
      updateHandlers();
    }
  }

  public void reset() {
    removeAllListeners();
    setInitialValue();
  }

  public void setInitialValue() {
    setState(initialValue);
  }

  private void updateHandlers() {
    dispatchAll(_prop);
  }

  @Override
  public void addEventListener(IEventHandler<T> handler) {
    super.addEventListener(handler);
    handler.callback(_prop, endRunnable(handler));
  }

  private Runnable endRunnable(final IEventHandler<T> handler) {
    return new Runnable() {
      @Override
      public void run() {
        removeEventListener(handler);
      }
    };
  }
}
