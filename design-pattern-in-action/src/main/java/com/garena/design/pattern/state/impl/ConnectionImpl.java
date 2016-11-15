package com.garena.design.pattern.state.impl;

import com.garena.design.pattern.Connection;
import com.garena.design.pattern.MessageListener;
import com.garena.design.pattern.state.Context;
import com.garena.design.pattern.interceptor.Pipeline;
import net.sf.json.JSONObject;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class ConnectionImpl implements Connection {

    private final Context context;

    public ConnectionImpl(Pipeline pipeline, String host, int port) {
        this.context = new StateMachineContextImpl(pipeline);
        this.context.set("host", host);
        this.context.set("port", port);
    }

    @Override
    public void send(JSONObject message) {
        context.process("send", message);
    }

    @Override
    public void connect() {
        context.setState("open");
    }

    @Override
    public void close() {
        context.setState("close");
    }

    @Override
    public void setMessageLisener(MessageListener listener) {
        ((StateMachineContextImpl) this.context).setMessageListener(listener);
    }

}
