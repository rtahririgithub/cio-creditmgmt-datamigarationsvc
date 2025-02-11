package com.telus.credit.migration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telus.credit.migration.thrd.MyAsyncService;

@Service
public class MyAsyncServiceLauncher {
    @Autowired
    private MyAsyncService myAsyncService;   

	private static final Logger LOGGER = LoggerFactory.getLogger(MyAsyncServiceLauncher.class);
	   
	public void start_N_AsyncService(int num_of_threads,int num_of_db_rows_limit) {
		LOGGER.info("start start_N_AsyncService"+", num_of_threads="+ num_of_threads +", num_of_db_rows_limit="+ num_of_db_rows_limit );
        final long start = System.currentTimeMillis();        
		ArrayList taskResultList = new ArrayList();
		//start N threads
		try {
			for (int i = 1; i <= num_of_threads; i++) { 
				//runTaskAsynchronously
				CompletableFuture<String> orchestrateMigrationRslt = myAsyncService.runMyTask(num_of_db_rows_limit);

				LOGGER.info( "start_N_AsyncService Thread_ " + i +" : " + orchestrateMigrationRslt);
				taskResultList.add(orchestrateMigrationRslt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long elpastime = System.currentTimeMillis() - start;
		LOGGER.info("Total Elapsed time to start " +num_of_threads+ " threads: " + elpastime +"ms .  " + (elpastime/1000/60) +" min: "); 
		
		//check the status of threads
		//LOGGER.info( "taskResultList size= " + taskResultList.size());
		
		//checkAllThreadsStatus(num_of_threads, num_of_db_rows_limit, start, taskResultList);
	}

	private void checkAllThreadsStatus(int num_of_threads, int num_of_db_rows_limit, final long start,
			ArrayList taskResultList) {
		long elpastime;
		LOGGER.info("checking the status of each thread.");
		while (true) {

			ArrayList<Boolean> statusList =new ArrayList<Boolean>();
			int counter=0;
			//LOGGER.info("--------------------");
			for (Object object : taskResultList) {							
				try {
					CompletableFuture<String> x =(CompletableFuture<String>) object;					
					statusList.add(x.isDone());			
					counter++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}				
			if(!statusList.contains(false) ){
				LOGGER.info("\n all threads are completed.");
				break;
			}			
		}
		//get each thread's reponse message
		for (Object object : taskResultList) {	
			CompletableFuture<String> x =(CompletableFuture<String>) object;
			try {
				if(x!=null) {
					LOGGER.info("runMyTask response=" +x.get());
				}
			} catch (Exception e) {
				//e.printStackTrace();
			} 
		}
		elpastime = System.currentTimeMillis() - start;
		
		InetAddress inetAddresstLocalHost=null;
		String podnameHostname = System.getenv("HOSTNAME");
		try {			
			inetAddresstLocalHost = java.net.InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
		String podDetails = "podName/HOSTNAME="+ podnameHostname + ". " + "java.net.InetAddress.getLocalHost="+ inetAddresstLocalHost;			
		LOGGER.info(
				"Total Elapsed time to finish " + num_of_threads + " threads, each processing  "+num_of_db_rows_limit + " db rows " + elpastime +" ms.  " + (elpastime/1000/60) +" min:. " + podDetails
				); 

		LOGGER.info("done");	
		LOGGER.info("end start_N_AsyncService");
	}
}
