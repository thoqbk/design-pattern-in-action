package com.garena.design.pattern.state.impl;

import com.garena.design.pattern.state.Context;
import com.garena.design.pattern.state.State;
import java.util.List;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class RetryState implements State {

    private static final int MAX_RETRY_TIMES = 2;
    private static final int SLEEP = 5000;

    @Override
    public String getName() {
        return "retry";
    }

    @Override
    public void process(Context context, String event, Object arg) {        
        int currentRetryTimes = countRetryTimes(context.getHistory());        
        if (currentRetryTimes <= MAX_RETRY_TIMES) {
            sleepThenRetry(context);
        } else {
            context.setState("fail");
        }
    }

    @Override
    public String[] getAllowedEvents() {
        return new String[]{null};
    }

    private void sleepThenRetry(Context context) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
            }
            context.setState("open");
        });
        thread.start();
    }
    
    private int countRetryTimes(List<String> history) {
        int retVal = 0;
        for(String state : history) {
            if("retry".equals(state)) {
                retVal++;
            }
            boolean b = "retry".equals(state) || "open".equals(state);
            if(!b) {
                break;
            }
        }
        return retVal;
    }
}
