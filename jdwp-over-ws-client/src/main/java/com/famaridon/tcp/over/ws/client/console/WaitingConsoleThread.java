package com.famaridon.tcp.over.ws.client.console;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingConsoleThread implements Runnable {

  private static final char[] WAITTING_CHARS = {'/', '-', '\\'};

  private boolean waiting = true;
  private final Queue<Character> waitingCharsQueue;

  public WaitingConsoleThread() {
    this.waitingCharsQueue = new LinkedList<>();
    for (char waitingChar : WAITTING_CHARS) {
      waitingCharsQueue.add(waitingChar);
    }
  }


  @Override
  public void run() {
    while (this.isWaiting()) {
      Character character = this.waitingCharsQueue.poll();
      this.waitingCharsQueue.add(character);
      System.out.print("\r");
      System.out.print(character);
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        this.waiting = false;
      }
    }
    System.out.print("\r");
  }

  public boolean isWaiting() {
    return waiting;
  }

  public void stop() {
    this.waiting = false;
  }
}
