package com.garena.design.pattern.interceptor.impl;

import com.garena.design.pattern.interceptor.AbstractInterceptor;
import com.garena.design.pattern.interceptor.MessageContext;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class CountInterceptor extends AbstractInterceptor {

    private final AtomicInteger count = new AtomicInteger(0);

    private final Logger logger = LoggerFactory.getLogger(CountInterceptor.class);

    @Override
    public void messageReceived(MessageContext context) {
        context.next();
        int reicevedMessages = count.incrementAndGet();
        logger.debug("Received " + reicevedMessages + " message(s)");
    }

    @Override
    public String getName() {
        return "count";
    }
}
