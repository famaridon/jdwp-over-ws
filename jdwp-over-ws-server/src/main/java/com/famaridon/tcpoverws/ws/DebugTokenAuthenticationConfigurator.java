package com.famaridon.tcpoverws.ws;

import com.famaridon.tcpoverws.cdi.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collections;
import java.util.List;

public class DebugTokenAuthenticationConfigurator extends ServerEndpointConfig.Configurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugTokenAuthenticationConfigurator.class);

    @Inject
    private SessionManager sessionManager;

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);

        List<String> tokens = request.getHeaders().get("X-Debug-Token");

        if(tokens.isEmpty()){
            LOGGER.debug("No X-Debug-Token found.");
            resetAccept(response);
        }

        tokens.forEach((token) -> {
            if(!this.sessionManager.validate(token)) {
                resetAccept(response);
            }
        });

    }

    private void resetAccept(HandshakeResponse response) {
        response.getHeaders().replace(HandshakeResponse.SEC_WEBSOCKET_ACCEPT, Collections.emptyList());
    }
}
