package com.famaridon.tcpoverws.commons;

import java.nio.ByteBuffer;

public interface WebSocketWrapper {

    void addMessageHandler(MessageHandler handler);
    void sendMessage(ByteBuffer message);
}
