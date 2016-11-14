package com.garena.design.pattern.in.action.interceptor.impl;

import com.garena.design.pattern.in.action.interceptor.AbstractInterceptor;
import com.garena.design.pattern.in.action.interceptor.Message;
import com.garena.design.pattern.in.action.interceptor.MessageContext;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class EncryptInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(EncryptInterceptor.class);
    private SecretKey secretKey;
    private IvParameterSpec ivSpec;

    public EncryptInterceptor() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec("password".toCharArray(), "salt".getBytes(), 65536, 256);
            SecretKey key = factory.generateSecret(spec);
            this.secretKey = new SecretKeySpec(key.getEncoded(), "AES");

            this.ivSpec = new IvParameterSpec(new byte[16]);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "encrypt";
    }

    /**
     * Encrypt
     *
     * @param context
     */
    @Override
    public void writeRequested(MessageContext context) {
        try {
            byte[] originalBytes = (byte[]) context.getMessage().getContent();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.ivSpec);
            byte[] encryptedBytes = cipher.doFinal(originalBytes);
            logger.debug("Encrypt: " + originalBytes.length + " byte(s) --> " + encryptedBytes.length + " byte(s)");

            //next
            context.next(new Message(encryptedBytes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Decrypt
     *
     * @param context
     */
    @Override
    public void messageReceived(MessageContext context) {
        try {
            byte[] encryptedBytes = (byte[]) context.getMessage().getContent();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.ivSpec);
            byte[] originalBytes = cipher.doFinal(encryptedBytes);

            logger.debug("Decrypt: " + encryptedBytes.length + " byte(s) --> " + originalBytes.length + " byte(s)");

            //next
            context.next(new Message(originalBytes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

}
