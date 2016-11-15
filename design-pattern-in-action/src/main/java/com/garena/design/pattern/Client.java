package com.garena.design.pattern;

import com.garena.design.pattern.interceptor.Pipeline;
import com.garena.design.pattern.interceptor.PipelineFactory;
import com.garena.design.pattern.state.impl.ConnectionImpl;
import com.garena.design.pattern.interceptor.impl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class Client implements MessageListener {

    private final Connection connection;
    private static final String HOST = "localhost";
    private static final int PORT = 1311;

    private static final List<String> VALID_COMMANDS = Arrays.asList("exit", "ls", "cd", "cat");

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client() {
        Pipeline pipeline = PipelineFactory.get("encrypt+compress");

        this.connection = new ConnectionImpl(pipeline, HOST, PORT);
    }

    public void start() throws IOException {
        connection.connect();
        connection.setMessageLisener(this);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] command = parseCommand(line);
            if (command == null) {
                logger.info("Invalid command: " + line);
                continue;
            }
            //ELSE:
            JSONObject message = new JSONObject();
            message.put("type", "command");
            message.put("body", command);
            connection.send(message);
        }
    }

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure(Client.class.getResource("/com/garena/design/pattern/in/action/resource/log4j.properties"));
        (new Client()).start();
    }

    @Override
    public void on(JSONObject message) {
        if ("fail".equals(message.optString("status"))) {
            logger.info(message.getString("body"));
        } else if ("successful".equals(message.optString("status"))) {
            JSONArray body = message.optJSONArray("body");
            if (body != null) {
                for (int idx = 0; idx < body.size(); idx++) {
                    logger.info(body.getString(idx));
                }
            } else if (message.optString("body", null) != null) {
                logger.info(message.getString("body"));
            }
        }
    }

    @Override
    public void on(byte[] bytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void on(Exception ex) {
    }
    
    private String[] parseCommand(String line) {
        String[] retVal = line.trim()
                .replaceAll("\\s+", " ")
                .split("\\s");
        if (retVal.length == 0 || !VALID_COMMANDS.contains(retVal[0])) {
            retVal = null;
        }
        logger.debug("Command: " + Arrays.toString(retVal));
        return retVal;
    }
}
