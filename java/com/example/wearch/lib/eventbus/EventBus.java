package com.example.wearch.lib.eventbus;

import java.util.ArrayList;

public class EventBus {
  private ArrayList<EventBusListener> listeners = new ArrayList<>();
  private static final EventBus ourInstance = new EventBus();

  public static EventBus getInstance() {
    return ourInstance;
  }
  public void on(String type, Runnable callback) {
    listeners.add(new EventBusListener(type, callback));
  }

  public void emit(String type) {
    ArrayList<EventBusListener> listenersClone = (ArrayList<EventBusListener>) listeners.clone();
    for (EventBusListener listener : listenersClone) {
      if (listener.type.equals(type)) {
        listener.handler.run();
      }
    }
  }

  public void off(String type) {
    ArrayList<EventBusListener> toRemove = new ArrayList<>();
    for (EventBusListener listener : listeners) {
      if (listener.type.equals(type)) {
        toRemove.add(listener);
      }
    }
    listeners.removeAll(toRemove);
  }

  public void off(String type, Runnable callback) {
    ArrayList<EventBusListener> toRemove = new ArrayList<>();
    for (EventBusListener listener : listeners) {
      if (listener.type.equals(type) && listener.handler.equals(callback)) {
        toRemove.add(listener);
      }
    }
    listeners.removeAll(toRemove);
  }
}
