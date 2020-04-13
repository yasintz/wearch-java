package com.example.wearch.lib.store;

class Listener {
  private String type;
  private IEventHandler handler;

  String getType() {
    return type;
  }
  IEventHandler getHandler() {
    return handler;
  }

  Listener(String type, IEventHandler handler) {
    this.type = type;
    this.handler = handler;
  }
}
