package com.example.wearch.lib.store;

public interface IEventDispatcher<T> {
  void addEventListener(IEventHandler<T> handler);

  void removeEventListener(IEventHandler<T> handler);

  void dispatchAll(T prop);

  void removeAllListeners();
}
