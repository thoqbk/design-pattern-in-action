package com.garena.design.pattern.interceptor;

import com.garena.design.pattern.interceptor.impl.BinaryInterceptor;
import com.garena.design.pattern.interceptor.impl.CompressInterceptor;
import com.garena.design.pattern.interceptor.impl.EncryptInterceptor;
import com.garena.design.pattern.interceptor.impl.JSONInterceptor;
import java.util.HashMap;
import java.util.Map;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public final class PipelineFactory {

    private static final Map<String, Pipeline> pipelines = new HashMap<>();

    public static Pipeline get(String config) {
        checkAndInitializePipelines();
        Pipeline retVal = pipelines.get(config);
        if (retVal == null) {
            throw new RuntimeException("Invalid pipeline config: " + config);
        }
        return retVal;
    }

    private static void checkAndInitializePipelines() {
        if (!pipelines.isEmpty()) {
            return;
        }
        //ELSE:
        //default:
        Pipeline defaultPipeline  = new Pipeline();
        defaultPipeline.attach(new BinaryInterceptor());
        defaultPipeline.attach(new JSONInterceptor());
        pipelines.put("default", defaultPipeline);
        
        //encrypt:
        Pipeline encrypt = new Pipeline();
        encrypt.attach(new BinaryInterceptor());
        encrypt.attach(new EncryptInterceptor());
        encrypt.attach(new JSONInterceptor());
        pipelines.put("encrypt", encrypt);
        
        //compress:
        Pipeline compress = new Pipeline();
        compress.attach(new BinaryInterceptor());
        compress.attach(new CompressInterceptor());
        compress.attach(new JSONInterceptor());
        pipelines.put("compress", compress);
        
        //encrypt+compress:
        Pipeline encryptNCompress = new Pipeline();
        encryptNCompress.attach(new BinaryInterceptor());
        encryptNCompress.attach(new EncryptInterceptor());
        encryptNCompress.attach(new CompressInterceptor());
        encryptNCompress.attach(new JSONInterceptor());
        pipelines.put("encrypt+compress", encryptNCompress);
        
        //compress+encrypt:
        Pipeline compressNEncrypt = new Pipeline();
        compressNEncrypt.attach(new BinaryInterceptor());
        compressNEncrypt.attach(new CompressInterceptor());
        compressNEncrypt.attach(new EncryptInterceptor());
        compressNEncrypt.attach(new JSONInterceptor());
        pipelines.put("compress+encrypt", compressNEncrypt);        
    }
}
