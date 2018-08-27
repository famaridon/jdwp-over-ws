package com.famaridon.tcp.over.ws.client.console;

public class StreamCountConsoleThread implements Runnable {


  private final StreamCountProxyListener streamCountProxyListener;

  public StreamCountConsoleThread(
      StreamCountProxyListener streamCountProxyListener) {
    this.streamCountProxyListener = streamCountProxyListener;
  }


  @Override
  public void run() {
    System.out.print("\r");
    System.out.print(String.format("WebSocket : received %1d o | emitted %2d o",
        this.streamCountProxyListener.getReceivedFromWebSocket(),
        this.streamCountProxyListener.getReceivedFromWebSocket()));

  }

}
