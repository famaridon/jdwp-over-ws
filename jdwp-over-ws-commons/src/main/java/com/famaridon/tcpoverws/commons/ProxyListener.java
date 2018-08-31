package com.famaridon.tcpoverws.commons;

import java.nio.ByteBuffer;

public interface ProxyListener {

  void onReceiveFromWebSocket(ByteBuffer byteBuffer);

  void onEmmitToWebSocket(ByteBuffer byteBuffer);
}
