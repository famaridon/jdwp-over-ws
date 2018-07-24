package com.famaridon.tcp.over.ws.client;

import com.famaridon.tcpoverws.commons.MessageHandler;
import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandlerClient extends WebSocketClient implements Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandlerClient.class);

  private final List<MessageHandler> handlers = new ArrayList<>();
  private boolean ready = false;
  private Object readyLock = new Object();

  private WebSocketHandlerClient(String serverUri, Map<String, String> headers)
      throws URISyntaxException {
    super(new URI(serverUri), headers);
  }

  public static WebSocketHandlerClient newInstance(String serverUri, String token)
      throws URISyntaxException {
    Validate.notBlank(token);
    Map<String, String> headers = new HashMap<>();
    headers.put("X-Debug-Token", token);
    return new WebSocketHandlerClient(serverUri, headers);
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    LOGGER.info("WebSocket tunnel opened.");
    synchronized (readyLock) {
      this.ready = true;
      this.readyLock.notifyAll();
    }
  }

  public void addMessageHandler(MessageHandler handler) {
    this.handlers.add(handler);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    LOGGER.info("Tunnel closed {}.", reason);
  }

  @Override
  public void onMessage(String message) {
    throw new NotImplementedException("Text message is not supported.");
  }

  @Override
  public void onMessage(ByteBuffer message) {
    this.handlers.forEach((h) -> h.onMessage(message));
  }

  @Override
  public void onError(Exception ex) {
    LOGGER.error("WebSocket error", ex);
  }

  public void waitReady() throws InterruptedException {
    if (!this.ready) {
      synchronized (readyLock) {
        this.readyLock.wait();
      }
    }
  }

  @Override
  public void close() {
    super.close();
  }
}