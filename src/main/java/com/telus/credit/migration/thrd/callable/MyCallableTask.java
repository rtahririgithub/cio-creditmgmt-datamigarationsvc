package com.telus.credit.migration.thrd.callable;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.telus.credit.migration.XBureauSvc;

@Component
@Scope("prototype")
public class MyCallableTask implements Callable<String>{
    @Autowired
    public  XBureauSvc xBureauSvc;
	public MyCallableTask(XBureauSvc xBureauSvc2) {
		xBureauSvc=xBureauSvc2;
	}
	@Override
	public String call() throws Exception {
		int dbRetrieveLimit=4; //100000
		System.out.println(Thread.currentThread().getName());
		System.out.println("xBureauSvc=" + xBureauSvc);
		
		String orchestrateMigrationResult = "";
		try{
			orchestrateMigrationResult=xBureauSvc.orchestrateMigration(dbRetrieveLimit);	
		}catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.out.println("orchestrateMigrationResult=" + orchestrateMigrationResult);
		return orchestrateMigrationResult;
	}

}
