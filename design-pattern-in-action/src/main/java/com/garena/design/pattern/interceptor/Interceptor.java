package com.garena.design.pattern.interceptor;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public interface Interceptor {
    
    public void intercept(MessageContext context);
    
    public String getName();
}
