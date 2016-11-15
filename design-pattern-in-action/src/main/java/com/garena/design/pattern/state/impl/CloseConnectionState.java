package com.garena.design.pattern.state.impl;

import com.garena.design.pattern.state.Context;
import com.garena.design.pattern.state.State;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class CloseConnectionState implements State {

    private static final Logger logger = LoggerFactory.getLogger(CloseConnectionState.class);

    @Override
    public String getName() {
        return "close";
    }

    @Override
    public void process(Context context, String event, Object arg) {
        Socket socket = (Socket) context.get("socket");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.error("Close connection fail", ex);
            }
        }
        context.delete("socket");
        logger.info("Connection has been closed");
        //moving to closed state
        context.setState("closed");
    }

    @Override
    public String[] getAllowedEvents() {
        return new String[]{null};
    }

}
