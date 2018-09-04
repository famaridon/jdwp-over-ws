package com.famaridon.tcpoverws.commons;

import java.nio.ByteBuffer;

public interface MessageHandler {

  void onOpen();

  void onMessage(ByteBuffer message);

  void onError(Exception ex);

  void onClose(int code, String reason, boolean remote);

}
