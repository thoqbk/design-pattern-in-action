package com.garena.design.pattern.in.action.impl.state;

import com.garena.design.pattern.in.action.Context;
import com.garena.design.pattern.in.action.MessageListener;
import com.garena.design.pattern.in.action.SocketReader;
import com.garena.design.pattern.in.action.State;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class ActiveState implements State {

    private static final Logger logger = LoggerFactory.getLogger(ActiveState.class);

    private MessageListener listener = null;

    public ActiveState() {
    }

    @Override
    public String getName() {
        return "active";
    }
    
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(Context context, String event, Object arg) {
        if ("send".equals(event)) {
            JSONObject message = (JSONObject) arg;
            byte[] messageInBytes = message.toString().getBytes();
            byte[] lengthInBytes = ByteBuffer.allocate(SocketReader.HEADER_LENGTH)
                    .putInt(0, messageInBytes.length)
                    .array();

            byte[] sentBytes = new byte[messageInBytes.length + lengthInBytes.length];
            System.arraycopy(lengthInBytes, 0, sentBytes, 0, lengthInBytes.length);
            System.arraycopy(messageInBytes, 0, sentBytes, lengthInBytes.length, messageInBytes.length);
            try {
                //send
                ((OutputStream) context.get("outputStream")).write(sentBytes);
            } catch (IOException ex) {
                logger.error("Cannot send message", ex);
                context.setState("retry");
            }
        } else if ("receive".equals(event) && listener != null) {
            listener.on((JSONObject) arg);
        }
    }

    @Override
    public String[] getAllowedEvents() {
        return new String[]{"send", "receive"};
    }

}
