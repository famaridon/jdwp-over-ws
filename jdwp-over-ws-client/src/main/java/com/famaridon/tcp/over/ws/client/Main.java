package com.famaridon.tcp.over.ws.client;

import com.famaridon.tcp.over.ws.client.console.WaitingConsoleThread;
import com.famaridon.tcpoverws.commons.SocketOverWsProxyConfiguration;
import com.famaridon.tcpoverws.commons.SocketOverWsProxyRunnable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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

    while (!cmd.hasOption("disable-reconnect")) {
      WaitingConsoleThread waitingConsoleThread = new WaitingConsoleThread();
      new Thread(waitingConsoleThread).start();
      SocketChannel socket = serverSocket.accept();
      waitingConsoleThread.stop();

      SocketOverWsProxyConfiguration proxyConfiguration = new SocketOverWsProxyConfiguration();
      WebSocketHandlerClient client = WebSocketHandlerClient.newInstance(remote, token);

      try {
        SocketOverWsProxyRunnable proxy = new SocketOverWsProxyRunnable(proxyConfiguration, socket,
            new WebSocketClientWrapper(client));
        Thread thread = new Thread(proxy);
        thread.start();
        thread.join();
      } catch (IOException | InterruptedException e) {
        throw new IllegalStateException(e);
      }
    }
  }
}
