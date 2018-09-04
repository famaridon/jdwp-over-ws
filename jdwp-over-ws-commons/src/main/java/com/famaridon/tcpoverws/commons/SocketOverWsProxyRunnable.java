package com.famaridon.tcpoverws.commons;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketOverWsProxyRunnable implements Runnable, Closeable, MessageHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SocketOverWsProxyRunnable.class);

  private final SocketOverWsProxyConfiguration configuration;

  private final SocketChannel socket;

  private final WebSocketWrapper ws;
  private final ByteBuffer buffer;
  private Thread runnableThread;

  public SocketOverWsProxyRunnable(SocketOverWsProxyConfiguration configuration)
      throws IOException {
    this.configuration = configuration;
    this.ws = this.configuration.getWebsocket();
    this.socket = this.configuration.getSocket();
    this.buffer = ByteBuffer.allocate(this.configuration.getBufferSize());
    this.ws.addMessageHandler(this);
  }

  @Override
  public void run() {
    this.runnableThread = Thread.currentThread();
    this.runnableThread.setName(SocketOverWsProxyConfiguration.class.getSimpleName() + "-Thread");
    try {
      while (!this.runnableThread.isInterrupted()) {
        buffer.clear();
        this.socket.read(this.buffer);
        buffer.flip();
        this.configuration.getProxyListeners()
            .forEach(proxyListener -> proxyListener.onEmmitToWebSocket(buffer.asReadOnlyBuffer()));
        ws.sendMessage(buffer);
      }
    } catch (ClosedByInterruptException e) {
      // nothing to do we simply stop listening
    } catch (IOException e) {
      // TODO nicer exception handling
      throw new IllegalStateException(e);
    } finally {
      this.close();
    }
  }

  @Override
  public void onOpen() {

  }

  @Override
  public void onMessage(ByteBuffer message) {
    try {
      this.configuration.getProxyListeners().forEach(
          proxyListener -> proxyListener.onReceiveFromWebSocket(message.asReadOnlyBuffer()));
      this.socket.write(message);
    } catch (IOException e) {
      LOGGER.error("Message received but can't be writing to debug", e);
    }
  }

  @Override
  public void onError(Exception ex) {
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    if (this.runnableThread != null) {
      this.runnableThread.interrupt();
    }
  }

  @Override
  public void close() {
    try {
      this.socket.close();
    } catch (IOException e) {
      LOGGER.warn("Can't close socket", e);
    }
  }

}
