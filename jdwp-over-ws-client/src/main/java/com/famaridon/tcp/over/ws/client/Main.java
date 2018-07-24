package com.famaridon.tcp.over.ws.client;

import java.io.IOException;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.famaridon.tcpoverws.commons.SocketOverWsProxyConfiguration;
import com.famaridon.tcpoverws.commons.SocketOverWsProxyRunnable;
import org.apache.commons.cli.*;
import org.java_websocket.client.WebSocketClient;
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
        options.addOption("h", "help", false, "print the help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption('h')) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "cli", options );
            return;
        }

        int port = Integer.parseInt(cmd.getOptionValue('p'));
        String remote = cmd.getOptionValue('r');
        String token = cmd.getOptionValue('t');

        LOGGER.info("Listening on port {}", port);
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", port));
        SocketChannel socket = serverSocket.accept();

        SocketOverWsProxyConfiguration proxyConfiguration = new SocketOverWsProxyConfiguration();

        WebSocketHandlerClient client = WebSocketHandlerClient.newInstance(remote, token);

        try {
            SocketOverWsProxyRunnable proxy = new SocketOverWsProxyRunnable(proxyConfiguration, socket, new WebSocketClientWrapper(client));
            Thread thread = new Thread(proxy);
            thread.start();
            thread.join();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }


    }
}
