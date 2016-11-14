package com.garena.design.pattern.in.action.interceptor.impl;

import com.garena.design.pattern.in.action.interceptor.Direction;
import static com.garena.design.pattern.in.action.interceptor.Direction.DOWN;
import static com.garena.design.pattern.in.action.interceptor.Direction.UP;
import com.garena.design.pattern.in.action.interceptor.Interceptor;
import com.garena.design.pattern.in.action.interceptor.Message;
import com.garena.design.pattern.in.action.interceptor.MessageContext;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public class MessageContextImpl implements MessageContext {

    private Direction direction;
    private Message message;
    private Interceptor receiver;
    private Pipeline pipeline;

    public MessageContextImpl(Direction direction) {
        this.direction = direction;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setReceiver(Interceptor receiver) {
        this.receiver = receiver;
    }

    @Override
    public void next(Message message, boolean reversal) {
        int oldReceiverIdx = -1;
        for (int idx = 0; idx < pipeline.getSize(); idx++) {
            if (pipeline.getInterceptor(idx) == receiver) {
                oldReceiverIdx = idx;
                break;
            }
        }
        if (reversal) {
            this.direction = (this.direction == Direction.UP) ? Direction.DOWN : Direction.UP;
        }
        Interceptor nextInterceptor = null;
        switch (this.getDirection()) {
            case UP: {
                nextInterceptor = pipeline.getInterceptor(oldReceiverIdx + 1);
                break;
            }
            case DOWN: {
                nextInterceptor = pipeline.getInterceptor(oldReceiverIdx - 1);
                break;
            }
        }
        if (message != null) {
            this.message = message;
        }
        receiver = nextInterceptor;
        if(nextInterceptor != null) {
            nextInterceptor.intercept(this);            
        }        
    }

    @Override
    public void next() {
        this.next(null);
    }

    @Override
    public void next(Message message) {
        this.next(message, false);
    }

    @Override
    public void next(Message message, Interceptor start) {
        this.receiver = start;
        next(message);
    }

    @Override
    public Message getMessage() {
        return this.message;
    }

    @Override
    public Interceptor getInterceptor() {
        return this.receiver;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }
}