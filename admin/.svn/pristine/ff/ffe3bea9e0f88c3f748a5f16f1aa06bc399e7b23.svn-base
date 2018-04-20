/**
 * Date:2018年3月1日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Date:2018年3月1日
 * Description:
 * Author:QinMing
 */
@Service  
public class SpringService implements ApplicationListener<ContextRefreshedEvent> {  
    private static ApplicationContext applicationContext = null;  
    @Override  
    public void onApplicationEvent(ContextRefreshedEvent event) {  
        if(applicationContext == null){  
            applicationContext = event.getApplicationContext();  
        }  
    }  
    /*ApplicationContext context= ContextLoader.getCurrentWebApplicationContext();//尝试下这个方法*/  
    public ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }  
    public Object getBean(String classname) throws BeansException, ClassNotFoundException{
    	return applicationContext.getBean(classname);
    }
}  
