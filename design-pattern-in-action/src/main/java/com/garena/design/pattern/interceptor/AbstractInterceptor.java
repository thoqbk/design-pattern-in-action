package com.garena.design.pattern.interceptor;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public abstract class AbstractInterceptor implements Interceptor {

    public void exceptionCaught(MessageContext context) {
        context.next();
    }

    public void messageReceived(MessageContext context) {
        context.next();
    }

    public void writeRequested(MessageContext context) {
        context.next();
    }

    @Override
    public final void intercept(MessageContext context) {
        if (context.getMessage() != null && context.getMessage().isException()) {
            this.exceptionCaught(context);
        } else {
            switch (context.getDirection()) {
                case UP: {
                    this.messageReceived(context);
                    break;
                }
                case DOWN: {
                    this.writeRequested(context);
                    break;
                }
            }
        }
    }
}
