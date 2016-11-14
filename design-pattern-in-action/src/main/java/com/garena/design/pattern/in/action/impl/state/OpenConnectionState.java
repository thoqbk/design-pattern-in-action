package com.garena.design.pattern.in.action.impl.state;

import com.garena.design.pattern.in.action.Context;
import com.garena.design.pattern.in.action.MessageListener;
import com.garena.design.pattern.in.action.SocketReader;
import com.garena.design.pattern.in.action.State;
import java.net.Socket;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class OpenConnectionState implements State, MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OpenConnectionState.class);
    private Context context;

    private SocketReader socketReader;

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public void process(Context context, String event, Object arg) {
        if ("receive".equals(event)) {
            logger.info("First message: " + ((JSONObject) arg).getString("body"));
            context.setState("active");
            return;
        }
        //ELSE: open connection
        String host = context.get("host").toString();
        int port = Integer.parseInt(context.get("port").toString());

        try {
            Socket socket = new Socket(host, port);
            context.set("socket", socket);
            context.set("outputStream", socket.getOutputStream());
            this.context = context;
            this.socketReader = new SocketReader(socket, this);
            this.socketReader.start();
        } catch (Exception ex) {
            logger.error("Open connection fail", ex);
            context.setState("retry");
        }
    }

    @Override
    public String[] getAllowedEvents() {
        return new String[]{null, "receive"};
    }

    @Override
    public void on(JSONObject message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void on(byte[] bytes) {
        context.process("receive", bytes);
    }

    @Override
    public void on(Exception ex) {
        context.setState("retry");
    }

}
