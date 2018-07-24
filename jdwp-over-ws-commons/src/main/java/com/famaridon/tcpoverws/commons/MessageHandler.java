package com.famaridon.tcpoverws.commons;

import java.nio.ByteBuffer;

public interface MessageHandler {

  void onMessage(ByteBuffer message);
}
