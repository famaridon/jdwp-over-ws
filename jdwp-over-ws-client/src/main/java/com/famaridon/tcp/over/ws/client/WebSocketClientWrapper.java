package com.famaridon.tcp.over.ws.client;

import com.famaridon.tcpoverws.commons.MessageHandler;
import com.famaridon.tcpoverws.commons.WebSocketWrapper;
import java.nio.ByteBuffer;

public class WebSocketClientWrapper implements WebSocketWrapper {

  private final WebSocketHandlerClient client;

  public WebSocketClientWrapper(WebSocketHandlerClient client) {
    this.client = client;
    new Thread(client::connect).start();
  }

  @Override
  public void addMessageHandler(MessageHandler handler) {
    this.client.addMessageHandler(handler);
  }

  @Override
  public void sendMessage(ByteBuffer message) {
    try {
      this.client.waitReady();
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
    this.client.send(message);
  }
}
