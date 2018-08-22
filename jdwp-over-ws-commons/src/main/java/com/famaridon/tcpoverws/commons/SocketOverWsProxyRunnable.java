package com.famaridon.tcpoverws.commons;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
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
  private final StreamCount streamCount = new StreamCount();
  private Thread runnableThread;

  public SocketOverWsProxyRunnable(SocketOverWsProxyConfiguration configuration,
      WebSocketWrapper ws) throws IOException {
    this.configuration = configuration;
    this.socket = SocketChannel.open();
    this.socket.connect(new InetSocketAddress(configuration.getHost(), configuration.getPort()));
    this.ws = ws;
    this.buffer = ByteBuffer.allocate(this.configuration.getBufferSize());
    this.ws.addMessageHandler(this);
  }

  public SocketOverWsProxyRunnable(SocketOverWsProxyConfiguration configuration,
      SocketChannel socket, WebSocketWrapper ws) throws IOException {
    this.configuration = configuration;
    this.ws = ws;
    this.socket = socket;
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
        LOGGER.debug("message read from socket {}", buffer);
        buffer.flip();
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
    LOGGER.debug("message received from web socket {}", message);
    try {
      this.socket.write(message);
      //this.streamCount.addRecived(message);
    } catch (IOException e) {
      LOGGER.error("Message received but can't be writing to debug", e);
    }
  }

  @Override
  public void onError(Exception ex) {
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    if(this.runnableThread != null) {
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

  public StreamCount getStreamCount() {
    return streamCount;
  }
}
