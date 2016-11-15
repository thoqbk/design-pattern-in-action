package com.garena.design.pattern;

import net.sf.json.JSONObject;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */

public interface Connection {
    
    public void send(JSONObject message);
    
    public void connect();
    
    public void close();
    
    public void setMessageLisener(MessageListener listener);
}
