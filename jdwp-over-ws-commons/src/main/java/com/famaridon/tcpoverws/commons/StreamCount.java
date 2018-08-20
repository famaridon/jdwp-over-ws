package com.famaridon.tcpoverws.commons;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamCount {

  private static final Logger LOG = LoggerFactory.getLogger(StreamCount.class);

  public AtomicLong recived = new AtomicLong(0L);
  public AtomicLong emitted = new AtomicLong(0L);

  public void addRecived(ByteBuffer message) {
    LOG.info("Received {}", this.recived.addAndGet(message.limit() - message.position()));
  }

  public void addEmitted(ByteBuffer message) {
    this.recived.addAndGet(message.limit() - message.position());
  }

  public Long getRecived() {
    return this.recived.get();
  }

  public Long getEmitted() {
    return this.emitted.get();
  }
}
