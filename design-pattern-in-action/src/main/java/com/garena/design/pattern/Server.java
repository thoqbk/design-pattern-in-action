package com.garena.design.pattern;

import com.garena.design.pattern.interceptor.*;
import com.garena.design.pattern.interceptor.impl.MessageContextImpl;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class Server extends Thread implements MessageListener {

    public static final int PORT = 1311;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final ServerSocket serverSocket;

    private final Pipeline pipeline;

    private final List<SocketReader> readers = new ArrayList<>();

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        this.pipeline = PipelineFactory.get("encrypt+compress");
    }
    
    public void close() {
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
        }
        this.interrupt();
        for (SocketReader reader : this.readers) {
            try {
                reader.interrupt();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                OutputStream outputStream = socket.getOutputStream();
                ClientMessageListener listener = new ClientMessageListener(socket, outputStream);
                SocketReader reader = new SocketReader(socket, listener);
                reader.start();
                readers.add(reader);
                logger.info("Accepted new client connection");
                sayHelloClient(outputStream);
            } catch (IOException ex) {
                logger.error("Cannot accept socket connection", ex);
                break;
            }
        }
    }

    @Override
    public void on(JSONObject message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void on(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void on(Exception ex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class ClientMessageListener implements MessageListener {

        private final Socket socket;
        private final OutputStream outputStream;

        private String currentDirectory = System.getProperty("user.dir");

        public ClientMessageListener(Socket socket, OutputStream outputStream) {
            this.socket = socket;
            this.outputStream = outputStream;
        }

        @Override
        public void on(JSONObject message) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void on(byte[] bytes) {
            MessageContextImpl context = new MessageContextImpl(Direction.UP);
            context.setPipeline(pipeline);
            context.setMessage(new Message(bytes));
            pipeline.intercept(context);
            //Received json data
            JSONObject json = (JSONObject) context.getMessage().getContent();
            logger.debug("Received message: " + json.toString());
            if ("command".equals(json.optString("type"))) {
                JSONObject response = new JSONObject();
                JSONArray command = json.getJSONArray("body");
                switch (command.getString(0)) {
                    case "ls": {
                        response.put("body", list(currentDirectory));
                        response.put("status", "successful");
                        break;
                    }
                    case "cd": {
                        if (command.size() < 2) {
                            response.put("status", "fail");
                            response.put("body", "Missing parameter");
                        } else {
                            File newCurrentDirectory = new File(currentDirectory, command.getString(1));
                            if (newCurrentDirectory.exists() && newCurrentDirectory.isDirectory()) {
                                response.put("status", "successful");
                                this.currentDirectory = newCurrentDirectory.getAbsolutePath();
                            } else {
                                response.put("status", "fail");
                                response.put("body", "Invalid directory");
                            }
                        }
                        break;
                    }
                    case "cat": {
                        if (command.size() < 2) {
                            response.put("status", "fail");
                            response.put("body", "Missing parameter");
                        } else {
                            File file = new File(currentDirectory, command.getString(1));
                            if (file.exists() && file.isFile()) {
                                try {
                                    response.put("status", "successful");
                                    response.put("body", Files.toString(file, Charsets.UTF_8));
                                } catch (IOException ex) {
                                    response.put("status", "fail");
                                    response.put("body", ex.getMessage());
                                }
                            } else {
                                response.put("status", "fail");
                                response.put("body", "Invalid file path: " + command.getString(1));
                            }
                        }
                        break;
                    }
                }
                send(response);
            }
        }

        @Override
        public void on(Exception ex) {

        }

        private void send(JSONObject message) {
            MessageContextImpl context = new MessageContextImpl(Direction.DOWN);
            context.setPipeline(pipeline);
            context.setMessage(new Message(message));
            pipeline.intercept(context);
            //Received json data
            byte[] data = (byte[]) context.getMessage().getContent();
            try {
                this.outputStream.write(data);
            } catch (IOException ex) {
                logger.error("Cannot send message to client", ex);
            }
            logger.debug("Sent " + message + " to client");
        }

    }

    private List<String> list(String path) {
        List<String> retVal = new ArrayList<>();
        File directory = new File(path);
        File[] children = directory.listFiles();
        for (File child : children) {
            retVal.add(child.getName());
        }
        return retVal;
    }
    
    private void sayHelloClient(OutputStream outputStream) throws IOException {
        JSONObject json = new JSONObject();
        json.put("body", "Hello Client");

        MessageContextImpl context = new MessageContextImpl(Direction.DOWN);
        context.setPipeline(pipeline);
        context.setMessage(new Message(json));
        pipeline.intercept(context);

        byte[] messageInBytes = (byte[]) context.getMessage().getContent();
        outputStream.write(messageInBytes);
    }
}
