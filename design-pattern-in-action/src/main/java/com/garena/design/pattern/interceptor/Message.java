package com.garena.design.pattern.interceptor;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public class Message {

    private final Object content;

    public Message(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public boolean isException() {
        return getContent() instanceof Exception;
    }
}