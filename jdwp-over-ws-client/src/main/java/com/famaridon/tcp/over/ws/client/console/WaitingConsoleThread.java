package com.famaridon.tcp.over.ws.client.console;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingConsoleThread implements Runnable {

  private static final char[] WAITTING_CHARS = {'/', '-', '\\'};

  private final Queue<Character> waitingCharsQueue;

  public WaitingConsoleThread() {
    this.waitingCharsQueue = new LinkedList<>();
    for (char waitingChar : WAITTING_CHARS) {
      waitingCharsQueue.add(waitingChar);
    }
  }


  @Override
  public void run() {
    Character character = this.waitingCharsQueue.poll();
    this.waitingCharsQueue.add(character);
    System.out.print("\r");
    System.out.print("Waiting  for connection ");
    System.out.print(character);
  }

}
