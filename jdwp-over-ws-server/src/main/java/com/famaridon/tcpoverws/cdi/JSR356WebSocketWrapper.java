package com.famaridon.tcpoverws.cdi;

import com.famaridon.tcpoverws.commons.MessageHandler;
import com.famaridon.tcpoverws.commons.WebSocketWrapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.websocket.Session;

public class JSR356WebSocketWrapper implements WebSocketWrapper {

  private final Session session;

  public JSR356WebSocketWrapper(Session session) {
    this.session = session;
  }

  @Override
  public void addMessageHandler(MessageHandler handler) {
    this.session.addMessageHandler(new JSR356MessageHandlerWrapper(handler));
  }

  @Override
  public void sendMessage(ByteBuffer message) {
    try {
      this.session.getBasicRemote().sendBinary(message);
    } catch (IOException e) {
      throw new IllegalStateException("Can't forward message", e);
    }
  }

  private class JSR356MessageHandlerWrapper implements
      javax.websocket.MessageHandler.Whole<ByteBuffer> {

    private final MessageHandler handler;

    private JSR356MessageHandlerWrapper(MessageHandler handler) {
      this.handler = handler;
    }

    @Override
    public void onMessage(ByteBuffer message) {
      this.handler.onMessage(message);
    }
  }
}
