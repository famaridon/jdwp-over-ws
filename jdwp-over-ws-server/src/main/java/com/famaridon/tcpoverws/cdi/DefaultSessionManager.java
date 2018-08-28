package com.famaridon.tcpoverws.cdi;

import com.famaridon.tcpoverws.commons.SocketOverWsProxyConfiguration;
import com.famaridon.tcpoverws.commons.SocketOverWsProxyRunnable;
import com.famaridon.tcpoverws.exceptions.DebugSocketException;
import com.famaridon.tcpoverws.exceptions.SingletonLockedException;
import com.famaridon.tcpoverws.ws.TunnelWebSocket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.websocket.Session;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class DefaultSessionManager implements SessionManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TunnelWebSocket.class);
  public static final int DEFAULT_PORT = 8787;
  public static final String DEFAULT_HOST = "localhost";

  @Resource
  private ManagedThreadFactory managedThreadFactory;

  private Session session;

  private SocketOverWsProxyRunnable proxy;

  private Thread thread;

  // Configuration
  private String token;
  private String host = DEFAULT_HOST;
  private int port = DEFAULT_PORT;

  @PostConstruct
  public void init() {
    this.token = System.getenv("DEBUG_TOKEN");
    if (StringUtils.isBlank(this.token)) {
      this.token = RandomStringUtils.randomAlphanumeric(32);
    }
    LOGGER.info("Debug token is set to : {}", this.token);
  }


  @Override
  public boolean validate(String token) {
    return this.token.equals(token);
  }

  @Override
  public void open(Session session) throws SingletonLockedException, DebugSocketException {
    if (this.session != null) {
      throw new SingletonLockedException("Session loked by " + this.session.getId());
    }
    this.session = session;
    LOGGER.info("Session lock tack by {}", session.getId());

    try {
      SocketOverWsProxyConfiguration proxyConfiguration = new SocketOverWsProxyConfiguration();
      SocketChannel socket = SocketChannel.open(new InetSocketAddress(this.host,this.port));
      socket.configureBlocking(true);
      proxyConfiguration.setSocket(socket);
      proxyConfiguration.setWebsocket( new JSR356WebSocketWrapper(this.session));
      this.proxy = new SocketOverWsProxyRunnable(proxyConfiguration);
      this.thread = this.managedThreadFactory.newThread(this.proxy);
      this.thread.start();
    } catch (IOException e) {
      throw new DebugSocketException("Can't open debug socket", e);
    }


  }

  @Override
  public void close(Session session) {
    if (this.session == null) {
      LOGGER.warn("Close session but no session is opened");
      return;
    }

    if (!this.session.getId().equals(session.getId())) {
      LOGGER.debug("Close session {} but this session don't have lock", session.getId());
      return;
    }

    this.session = null;
    LOGGER.info("Session unlocked");

    if (this.thread != null) {
      this.thread.interrupt();
    }
    this.thread = null;
  }

}
