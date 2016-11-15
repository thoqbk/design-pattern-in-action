package com.garena.design.pattern;

import net.sf.json.JSONObject;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */

public interface MessageListener {
    
    public void on(JSONObject message);
    
    public void on(byte[] bytes);
    
    public void on(Exception ex);
}
