package com.famaridon.tcpoverws.ws;

import com.famaridon.tcpoverws.cdi.SessionManager;
import com.famaridon.tcpoverws.exceptions.DebugSocketException;
import com.famaridon.tcpoverws.exceptions.SingletonLockedException;
import java.io.IOException;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/ws/tunnel", configurator = DebugTokenAuthenticationConfigurator.class)
public class TunnelWebSocket {

  private static final Logger LOGGER = LoggerFactory.getLogger(TunnelWebSocket.class);

  @Inject
  private SessionManager sessionManager;

  @OnOpen
  public void onOpen(Session session) throws IOException {

    try {
      this.sessionManager.open(session);
    } catch (SingletonLockedException e) {
      LOGGER.debug("WebSocket aborted", e);
      session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Debug session is already opened"));
    } catch (DebugSocketException e) {
      LOGGER.debug("Can't open remote debug", e);
      session.close(new CloseReason(CloseCodes.CLOSED_ABNORMALLY, "Can't open remote debug"));
    }
  }

  @OnClose
  public void onClose(Session session, CloseReason reason) {
    LOGGER.info("WebSocket connection closed with CloseCode: {}", reason.getCloseCode());
    this.sessionManager.close(session);
  }
}
