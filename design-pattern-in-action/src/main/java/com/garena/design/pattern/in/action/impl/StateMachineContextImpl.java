package com.garena.design.pattern.in.action.impl;

import com.garena.design.pattern.in.action.Context;
import com.garena.design.pattern.in.action.MessageListener;
import com.garena.design.pattern.in.action.State;
import com.garena.design.pattern.in.action.impl.state.*;
import com.garena.design.pattern.in.action.interceptor.*;
import com.garena.design.pattern.in.action.interceptor.impl.*;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class StateMachineContextImpl implements Context {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineContextImpl.class);

    private State currentState;
    private final Map<String, State> nameNStateMap = new HashMap<>();
    private final Map<String, Object> parameters = new HashMap<>();

    private final List<String> history = new ArrayList<>();

    private final Pipeline pipeline;

    public StateMachineContextImpl(Pipeline pipeline) {
        this.pipeline = pipeline;

        State[] states = new State[]{
            new ActiveState(),
            new CloseConnectionState(),
            new ClosedState(),
            new FailState(),
            new OpenConnectionState(),
            new RetryState()
        };
        for (State state : states) {
            this.nameNStateMap.put(state.getName(), state);
        }
    }

    public void setMessageListener(MessageListener listener) {
        ((ActiveState) nameNStateMap.get("active")).setMessageListener(listener);
    }

    @Override
    public String getState() {
        if (currentState == null) {
            return null;
        }
        return this.currentState.getName();
    }

    @Override
    public void setState(String name) {
        this.setState(name, null);
    }

    @Override
    public void setState(String name, Object args) {
        State state = this.nameNStateMap.get(name);
        if (state == null) {
            throw new RuntimeException("State not found: " + name);
        }
        this.history.add(name);
        this.currentState = state;

        boolean allowDefaultEvent = false;
        for (String allowedEvent : this.currentState.getAllowedEvents()) {
            if (allowedEvent == null) {
                allowDefaultEvent = true;
                break;
            }
        }
        if (allowDefaultEvent) {
            this.process(null, args);
        }
        logger.info("Changed state to \"" + name + "\"");
    }

    @Override
    public void set(String name, Object value) {
        this.parameters.put(name, value);
    }

    @Override
    public Object get(String name) {
        return this.parameters.get(name);
    }

    @Override
    public Object delete(String name) {
        return this.parameters.remove(name);
    }

    @Override
    public void process(String event, Object arg) {
        boolean found = false;
        for (String allowedEvent : this.currentState.getAllowedEvents()) {
            boolean b = allowedEvent == null && event == null;
            boolean b2 = allowedEvent != null && allowedEvent.equals(event);
            if (b || b2) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new RuntimeException("Event " + event + " is not allowed in state \"" + this.currentState.getName() + "\"");
        }
        if ("send".equals(event)) {
            MessageContextImpl messageContext = new MessageContextImpl(Direction.DOWN);
            messageContext.setMessage(new Message(arg));
            messageContext.setPipeline(pipeline);
            pipeline.intercept(messageContext);
            try {
                ((OutputStream) get("outputStream")).write((byte[]) messageContext.getMessage().getContent());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else if ("receive".equals(event)) {
            MessageContextImpl messageContext = new MessageContextImpl(Direction.UP);
            messageContext.setMessage(new Message(arg));
            messageContext.setPipeline(pipeline);
            pipeline.intercept(messageContext);
            JSONObject message = (JSONObject) messageContext.getMessage().getContent();
            this.currentState.process(this, event, message);
        } else {
            this.currentState.process(this, event, arg);
        }
    }

    @Override
    public List<String> getHistory() {
        return ImmutableList.copyOf(history);
    }

}