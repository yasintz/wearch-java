package com.example.wearch.lib.store;

public interface IEventHandler<T> {
  void callback(T val,Runnable end);
}
