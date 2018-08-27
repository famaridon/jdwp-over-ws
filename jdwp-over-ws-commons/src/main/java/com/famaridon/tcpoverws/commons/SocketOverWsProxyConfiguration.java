package com.famaridon.tcpoverws.commons;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class SocketOverWsProxyConfiguration {

  private SocketChannel socket;
  private WebSocketWrapper websocket;
  private int bufferSize = 2048;
  private List<ProxyListener> proxyListeners = new ArrayList<>();

  public SocketChannel getSocket() {
    return socket;
  }

  public void setSocket(SocketChannel socket) {
    this.socket = socket;
  }

  public WebSocketWrapper getWebsocket() {
    return websocket;
  }

  public void setWebsocket(WebSocketWrapper websocket) {
    this.websocket = websocket;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public void setProxyListeners(
      List<ProxyListener> proxyListeners) {
    this.proxyListeners = proxyListeners;
  }

  public boolean addProxyListener(ProxyListener proxyListener) {
    return this.proxyListeners.add(proxyListener);
  }

  public List<ProxyListener> getProxyListeners() {
    return proxyListeners;
  }
}
