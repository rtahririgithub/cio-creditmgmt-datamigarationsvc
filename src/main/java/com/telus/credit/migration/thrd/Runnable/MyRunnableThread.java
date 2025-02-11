package com.telus.credit.migration.thrd.Runnable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.telus.credit.migration.XBureauSvc;

@Component
@Scope("prototype")
public class MyRunnableThread implements Runnable {	
	
    @Autowired
    public  XBureauSvc xBureauSvc;	
    
    @Override
    public void run() {
    	try {    		
    		String orchestrateMigration = xBureauSvc.orchestrateMigration(1);
    		System.out.println("orchestrateMigration=" + orchestrateMigration);
		} catch (Exception e) {			
			System.out.println(Thread.currentThread().getName());	
			System.out.println("xBureauSvc=" + xBureauSvc);
			e.printStackTrace();
			System.out.println("---------------------");
		}
    }




}
