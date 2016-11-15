package com.garena.design.pattern.interceptor;

import com.garena.design.pattern.interceptor.impl.MessageContextImpl;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public class Pipeline implements Interceptor{
    
    private final Logger logger = LoggerFactory.getLogger(Pipeline.class);
    
    private final List<Interceptor> interceptors = new LinkedList<>();

    public void attach(Interceptor interceptor){
        interceptors.add(interceptor);
    }
    
    public void detach(int idx){
        interceptors.remove(idx);
    }
    
    public Interceptor getInterceptor(int idx){
        Interceptor retVal = null;
        if(idx >=0 && idx < this.interceptors.size()) {
            retVal = this.interceptors.get(idx);
        }
        return retVal;
    }
    
    public int getSize(){
        return this.interceptors.size();
    }
    
    @Override
    public void intercept(MessageContext context) {
        if (this.getSize() > 0) {
            Interceptor targetInterceptor = null;
            switch (context.getDirection()) {
                case UP: {
                    targetInterceptor = this.getInterceptor(0);
                    break;
                }
                case DOWN: {
                    targetInterceptor = this.getInterceptor(this.getSize() - 1);
                    break;
                }
            }
            //debug
            logger.debug("Next interceptor: " + targetInterceptor.getName());
            ((MessageContextImpl) context).setReceiver(targetInterceptor);
            targetInterceptor.intercept(context);
        }
    }
    
    @Override
    public String getName() {
        return "pipeline";
    }
}
