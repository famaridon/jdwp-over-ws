package com.famaridon.tcp.over.ws.client;

import com.famaridon.tcp.over.ws.client.console.StreamCountConsoleThread;
import com.famaridon.tcp.over.ws.client.console.StreamCountProxyListener;
import com.famaridon.tcp.over.ws.client.console.WaitingConsoleThread;
import com.famaridon.tcpoverws.commons.SocketOverWsProxyConfiguration;
import com.famaridon.tcpoverws.commons.SocketOverWsProxyRunnable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws URISyntaxException, IOException, ParseException {

    // create Options object
    Options options = new Options();

    // add t option
    options.addOption("p", "port", true, "the local listening port");
    options.addOption("r", "remote", true, "the remote web service url");
    options.addOption("t", "token", true, "the generated server token");
    options.addOption(null, "disable-reconnect", false,
        "if connexion fail will not wait for new connection");
    options.addOption("h", "help", false, "print the help");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    if (cmd.hasOption('h')) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("cli", options);
      return;
    }

    int port = Integer.parseInt(cmd.getOptionValue('p'));
    String remote = cmd.getOptionValue('r');
    String token = cmd.getOptionValue('t');

    ServerSocketChannel serverSocket = ServerSocketChannel.open();
    serverSocket.bind(new InetSocketAddress("localhost", port));
    LOGGER.info("Listening on port {}", port);

    ScheduledExecutorService consoleTimmer = Executors.newScheduledThreadPool(1);

    while (!cmd.hasOption("disable-reconnect")) {
      // wait for connexion
      ScheduledFuture<?> waittingTask = consoleTimmer
          .scheduleAtFixedRate(new WaitingConsoleThread(), 0, 250, TimeUnit.MILLISECONDS);
      SocketChannel socket = serverSocket.accept();
      socket.configureBlocking(true);
      waittingTask.cancel(false);

      SocketOverWsProxyConfiguration proxyConfiguration = new SocketOverWsProxyConfiguration();
      StreamCountProxyListener streamCountProxyListener = new StreamCountProxyListener();
      proxyConfiguration.addProxyListener(streamCountProxyListener);
      proxyConfiguration.setSocket(socket);
      WebSocketHandlerClient client = WebSocketHandlerClient.newInstance(remote, token);
      proxyConfiguration.setWebsocket(new WebSocketClientWrapper(client));

      ScheduledFuture<?> streamCountTask = null;
      try {
        SocketOverWsProxyRunnable proxy = new SocketOverWsProxyRunnable(proxyConfiguration);
        Thread thread = new Thread(proxy);
        thread.start();
        streamCountTask = consoleTimmer
            .scheduleAtFixedRate(new StreamCountConsoleThread(streamCountProxyListener), 0, 250,
                TimeUnit.MILLISECONDS);
        thread.join();
      } catch (IOException | InterruptedException e) {
        throw new IllegalStateException(e);
      } finally {
        if (streamCountTask != null) {
          streamCountTask.cancel(false);
        }
      }
    }
  }
}
