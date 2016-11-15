package com.garena.design.pattern.interceptor.impl;

import com.garena.design.pattern.interceptor.AbstractInterceptor;
import com.garena.design.pattern.interceptor.Message;
import com.garena.design.pattern.interceptor.MessageContext;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class CompressInterceptor extends AbstractInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CompressInterceptor.class);

    @Override
    public String getName() {
        return "compress";
    }

    /**
     * Compress
     *
     * @param context
     */
    @Override
    public void writeRequested(MessageContext context) {
        byte[] originalBytes = (byte[]) context.getMessage().getContent();
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(bytesOutputStream)) {
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(originalBytes.length);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(originalBytes);
            zipOutputStream.closeEntry();
            byte[] compressedBytes = bytesOutputStream.toByteArray();
            logger.debug("Compress: " 
                    + originalBytes.length + " byte(s) --> " 
                    + compressedBytes.length + " byte(s)");

            //next
            context.next(new Message(compressedBytes));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Decompress
     *
     * @param context
     */
    @Override
    public void messageReceived(MessageContext context) {
        byte[] compressedBytes = (byte[]) context.getMessage().getContent();
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(compressedBytes));
        try {
            zipInputStream.getNextEntry();
            byte[] originalBytes = ByteStreams.toByteArray(zipInputStream);
            logger.debug("Decompress: " 
                    + compressedBytes.length + " byte(s), --> "
                    + originalBytes.length + " byte(s)");
            
            zipInputStream.close();
            //next
            context.next(new Message(originalBytes));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

}
