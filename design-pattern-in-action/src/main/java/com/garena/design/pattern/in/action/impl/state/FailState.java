package com.garena.design.pattern.in.action.impl.state;

import com.garena.design.pattern.in.action.Context;
import com.garena.design.pattern.in.action.State;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class FailState implements State {

    @Override
    public String getName() {
        return "fail";
    }

    @Override
    public void process(Context context, String event, Object arg) {

    }

    @Override
    public String[] getAllowedEvents() {
        return new String[]{};
    }

}
