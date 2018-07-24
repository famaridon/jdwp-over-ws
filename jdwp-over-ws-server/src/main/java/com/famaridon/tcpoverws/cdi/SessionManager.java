package com.famaridon.tcpoverws.cdi;

import com.famaridon.tcpoverws.exceptions.DebugSocketException;
import com.famaridon.tcpoverws.exceptions.SingletonLockedException;

import javax.websocket.Session;

public interface SessionManager {
    boolean validate(String token);

    void open(Session session) throws SingletonLockedException, DebugSocketException;

    void close(Session session);
}
