package com.garena.design.pattern.state.impl;

import com.garena.design.pattern.state.Context;
import com.garena.design.pattern.MessageListener;
import com.garena.design.pattern.state.State;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class ActiveState implements State {

    private static final Logger logger = LoggerFactory.getLogger(ActiveState.class);

    private MessageListener listener = null;

    public ActiveState() {
    }

    @Override
    public String getName() {
        return "active";
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(Context context, String event, Object arg) {
        if ("receive".equals(event) && listener != null) {
            listener.on((JSONObject) arg);
        }
    }

    @Override
    public String[] getAllowedEvents() {
        return new String[]{"send", "receive"};
    }

}
