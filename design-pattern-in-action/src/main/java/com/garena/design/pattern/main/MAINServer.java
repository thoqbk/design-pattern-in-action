package com.garena.design.pattern.main;

import com.garena.design.pattern.Client;
import com.garena.design.pattern.Server;
import java.io.IOException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class MAINServer {
    
    private static final Logger logger = LoggerFactory.getLogger(MAINServer.class);
    
    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure(Client.class.getResource("/com/garena/design/pattern/in/action/resource/log4j.properties"));
        Server server = new Server();
        server.start();
        logger.info("Server is running at port " + Server.PORT);
        System.in.read();
        server.close();
    }
}
