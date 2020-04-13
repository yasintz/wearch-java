package com.example.wearch.lib.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StoreEventDispatcher<T> implements IEventDispatcher<T> {
  ArrayList<IEventHandler> handlers = new ArrayList<>();

  @Override
  public void addEventListener(IEventHandler<T> handler) {
    handlers.add(0, handler);
  }


  @Override
  public void removeEventListener(IEventHandler<T> handler) {
    List<IEventHandler> toRemove = new ArrayList<>();
    for (IEventHandler availableHandler : handlers) {
      if (availableHandler.equals(handler)) {
        toRemove.add(availableHandler);
      }
    }
    handlers.removeAll(toRemove);
  }

  @Override
  public void dispatchAll(T prop) {
    ArrayList<IEventHandler> handlersClone = (ArrayList<IEventHandler>) handlers.clone();
    for (final IEventHandler handler : handlersClone) {
      handler.callback(prop, new Runnable() {
        @Override
        public void run() {
          removeEventListener(handler);
        }
      });
    }
  }

  @Override
  public void removeAllListeners() {
    handlers.clear();
  }
}
