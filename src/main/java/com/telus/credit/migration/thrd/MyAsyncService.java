package com.telus.credit.migration.thrd;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.telus.credit.migration.MigrationConstants;
import com.telus.credit.migration.XBureauSvc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MyAsyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyAsyncService.class);

    @Autowired
    public  XBureauSvc xBureauSvc;

  
    
   @Async
    public CompletableFuture<String> runMyTask(int num_of_db_rows_limit) throws Exception {
        final long start = System.currentTimeMillis();
        CompletableFuture<String> rslt= new  CompletableFuture<String>();
   
        try {        
        	String orchestrateMigrationRslt="";
        	orchestrateMigrationRslt = xBureauSvc.orchestrateMigration(num_of_db_rows_limit);
        	
        	rslt.complete(orchestrateMigrationRslt);
			LOGGER.info("orchestrateMigrationRslt="+orchestrateMigrationRslt);
		} catch (Throwable e) {
			e.printStackTrace();
			rslt.completeExceptionally(e);
		}
       LOGGER.info("MyAsyncService.runMyTask thread " + Thread.currentThread().getName()+" Elapsed time: "+ (System.currentTimeMillis() - start));
        return rslt;
    }

}