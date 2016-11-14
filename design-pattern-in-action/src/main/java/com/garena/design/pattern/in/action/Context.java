package com.garena.design.pattern.in.action;

import java.util.List;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */

public interface Context {    
    
    public String getState();
    
    public void setState(String name);
    
    public void setState(String name, Object args);    
    
    public void set(String name, Object value);
    
    public Object get(String name);
    
    public Object delete(String name);
    
    public void process(String event, Object arg);
    
    public List<String> getHistory();
}
