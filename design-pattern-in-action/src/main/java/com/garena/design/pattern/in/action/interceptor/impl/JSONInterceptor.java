package com.garena.design.pattern.in.action.interceptor.impl;

import com.garena.design.pattern.in.action.interceptor.AbstractInterceptor;
import com.garena.design.pattern.in.action.interceptor.Message;
import com.garena.design.pattern.in.action.interceptor.MessageContext;
import net.sf.json.JSONObject;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public class JSONInterceptor extends AbstractInterceptor{

    @Override
    public String getName() {
        return "json";
    }
    
    /**
     * Binary to JSON
     * @param context 
     */
    @Override
    public void messageReceived(MessageContext context) {
        String messageInString = new String((byte[])context.getMessage().getContent());
        context.next(new Message(JSONObject.fromObject(messageInString)));
    }
    
    /**
     * JSON to binary
     * @param context 
     */
    @Override
    public void writeRequested(MessageContext context) {
        String jsonInString = ((JSONObject)context.getMessage().getContent()).toString();        
        context.next(new Message(jsonInString.getBytes()));
    }
    
}
