package com.garena.design.pattern.main;

import com.garena.design.pattern.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class MAINClient {

    private static final Logger logger = LoggerFactory.getLogger(MAINClient.class);

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure(Client.class.getResource("/com/garena/design/pattern/in/action/resource/log4j.properties"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean encrypt = true;
        boolean compress = true;

        logger.info("Encrypt data(Y/n) ?");
        String line = reader.readLine();
        if ("n".equals(line)) {
            encrypt = false;
        }
        logger.info("Compress data(Y/n) ?");
        line = reader.readLine();
        if ("n".equals(line)) {
            compress = false;
        }

        Client client = new Client.Builder()
                .encrypt(encrypt)
                .compress(compress)
                .setPort(1311)
                .build();
        client.start();
    }
}
