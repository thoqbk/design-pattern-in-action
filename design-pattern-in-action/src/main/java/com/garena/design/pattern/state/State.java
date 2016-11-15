package com.garena.design.pattern.state;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */

public interface State {
    
    public String getName();

    public String[] getAllowedEvents();
    
    public void process(Context context, String event, Object arg);
}
