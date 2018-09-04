package com.famaridon.tcp.over.ws.client.console;

import com.famaridon.tcpoverws.commons.ProxyListener;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamCountProxyListener implements ProxyListener {

  private static final Logger LOG = LoggerFactory.getLogger(StreamCountProxyListener.class);

  private AtomicReference<BigInteger> receivedFromWebSocket = new AtomicReference<>(BigInteger.ZERO);
  private AtomicReference<BigInteger> emittedToWebSocket = new AtomicReference<>(BigInteger.ZERO);

  @Override
  public void onReceiveFromWebSocket(ByteBuffer byteBuffer) {
    this.receivedFromWebSocket.accumulateAndGet(getByteBufferSize(byteBuffer), BigInteger::add);
  }

  @Override
  public void onEmmitToWebSocket(ByteBuffer byteBuffer) {
    this.emittedToWebSocket.accumulateAndGet(getByteBufferSize(byteBuffer), BigInteger::add);
  }

  private BigInteger getByteBufferSize(ByteBuffer byteBuffer) {
    return BigInteger.valueOf(byteBuffer.limit() - byteBuffer.position());
  }

  public BigInteger getReceivedFromWebSocket() {
    return receivedFromWebSocket.get();
  }

  public BigInteger getEmittedToWebSocket() {
    return emittedToWebSocket.get();
  }

}
