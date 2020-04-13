package com.example.wearch.lib.eventbus;


class EventBusListener {
  final public String type;
  final public Runnable handler;

  EventBusListener(String type, Runnable handler) {
    this.type = type;
    this.handler = handler;
  }
}
