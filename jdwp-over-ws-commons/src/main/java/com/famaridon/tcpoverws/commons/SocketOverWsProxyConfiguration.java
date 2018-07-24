package com.famaridon.tcpoverws.commons;

public class SocketOverWsProxyConfiguration {

  private String host = "127.0.0.0";
  private int port = 5005;
  private int bufferSize = 2048;
  private int threadCheckInterrupt = 1000;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public int getThreadCheckInterrupt() {
    return threadCheckInterrupt;
  }

  public void setThreadCheckInterrupt(int threadCheckInterrupt) {
    this.threadCheckInterrupt = threadCheckInterrupt;
  }
}
