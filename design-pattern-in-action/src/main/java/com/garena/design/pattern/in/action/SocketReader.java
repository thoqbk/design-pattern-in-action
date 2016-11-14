package com.garena.design.pattern.in.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public class SocketReader extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SocketReader.class);
    public static final int HEADER_LENGTH = 4;
    private final InputStream inputStream;

    private final MessageListener listener;

    public SocketReader(Socket socket, MessageListener listener) throws IOException {
        this.listener = listener;
        this.inputStream = socket.getInputStream();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[102400];
        int bufferIdx = 0;
        int waiting = HEADER_LENGTH;
        while (true) {
            try {
                int read = this.inputStream.read(buffer, bufferIdx, waiting);
                if(read == -1) {
                    break;
                }
                waiting -= read;
                bufferIdx += read;
                if (waiting > 0) {
                    continue;
                }
                //ELSE:
                if (bufferIdx == HEADER_LENGTH) {//having full message
                    int length = ByteBuffer.allocate(4).put(buffer, 0, HEADER_LENGTH)
                            .getInt(0);
                    if (length > buffer.length - HEADER_LENGTH) {
                        throw new Exception("Too long message: " + length);
                    }
                    waiting = length;
                } else {//having full message
                    byte[] messageInByes = new byte[bufferIdx];
                    System.arraycopy(buffer, 0, messageInByes, 0, messageInByes.length);
                    //fire event:
                    listener.on(messageInByes);
                    //reset and continue reading
                    bufferIdx = 0;
                    waiting = HEADER_LENGTH;
                }
            } catch (Exception ex) {
                logger.error("Reading exception", ex);
                listener.on(ex);
                break;
            }
        }
    }

}
