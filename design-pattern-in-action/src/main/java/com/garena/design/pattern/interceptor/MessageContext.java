package com.garena.design.pattern.interceptor;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public interface MessageContext {

    public void next();
    
    public void next(Message message);
    
    public void next(Message message, boolean reversal);
    
    public void next(Message message, Interceptor start);
    
    public Message getMessage();
    
    /**
     * Get current interceptor
     * @return 
     */
    public Interceptor getInterceptor();
    
    /**
     * Get current direction
     * @return 
     */
    public Direction getDirection();
    
}