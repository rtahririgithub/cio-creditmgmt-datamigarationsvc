
package com.telus.credit;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.telus.credit.migration.MigrationConstants;
import com.telus.credit.migration.MyAsyncServiceLauncher;
import com.telus.credit.migration.XBureauSvc;
import com.telus.credit.migration.thrd.MyAsyncService;
import com.telus.credit.migration.thrd.Runnable.MyRunnableThread;
import com.telus.credit.migration.thrd.callable.MyCallableTask;


@SpringBootTest
@SpringBootConfiguration
@ComponentScan("com.telus.credit")
class XBureauSvcTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(XBureauSvcTest.class);
    @Autowired
    private XBureauSvc xBureauSvc; 
    @Autowired
    private MyAsyncService myAsyncService;    
    @Autowired
    private MyAsyncServiceLauncher myAsyncServiceLauncher;     
	@BeforeEach
	void setUp() throws Exception {
		
	}
	//@Test
	void testReformat_FireStoreDoct() {
		 try {
			 int maxNumberOfItemsToReturn=50000;
			xBureauSvc.reformat_FireStoreDoct(maxNumberOfItemsToReturn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	//@Test
	void testGetCustomerIdList() {
		 try {
			 FileWriter f = new FileWriter("c://temp//FirestoreCustomerIdList2.txt", true);
			 BufferedWriter writer = new BufferedWriter(f);
			
			 int maxToReturn=50000;
			ArrayList<String> custlist = xBureauSvc.getCustomerIdList("customer.individual.atReferredType", "Individual",maxToReturn);
			for (String object : custlist) {
				String line = object +",bureaucontent,FALSE,2022-03-27 14:17,2022-03-27 14:17,1,1";

				writer.append(line);
				writer.append("\n");
				LOGGER.debug(object);
			}

			 writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	//@Test
	void testMyCallableThread() {
		 final long start = System.currentTimeMillis();
		try {
			//for cpu intensive operation use Runtime.getRuntime().availableProcessors();
			//int nThreads=Runtime.getRuntime().availableProcessors();
			//for io intensive operattion use a number per use cse 			
			int nThreads=4;//350;
			
			//Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue.
			ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
			ArrayList taskResultList = new ArrayList();
			for (int i = 0; i < 11; i++) {
				// issue: with spring bean in MyRunnableThread is not read.
				//threadPool.execute(new MyRunnableThread());
				
				MyCallableTask task = new MyCallableTask(xBureauSvc);
				Future<String> rslt = threadPool.submit( task);
				taskResultList.add(rslt);
				LOGGER.debug("rslt isDone = " + rslt.isDone());
			}
			
			while (true) {
				
				ArrayList<Boolean> statusList =new ArrayList<Boolean>();
				int counter=0;
				for (Object object : taskResultList) {							
					try {
						Future<String> x =(Future<String>) object;
						statusList.add(x.isDone());					
						LOGGER.debug(counter+ "-isDone= " + x.isDone());
						counter++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	
				if(!statusList.contains(false) ){
					LOGGER.debug("all threads are completed.");
					break;
				}			
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.debug("Total Elapsed time to finish all threads: "+ (System.currentTimeMillis() - start)); //Elapsed time: 8354
		LOGGER.debug("done");
		}
	
	*/
	//@Test
	void test_start_N_AsyncService() {
		int num_of_threads			=4;
		int num_of_db_rows_limit	=250;
		myAsyncServiceLauncher.start_N_AsyncService(num_of_threads,num_of_db_rows_limit);
		LOGGER.debug("start_N_AsyncService is done");
	}
/*
	//@Test
	void testMyAsyncService() {
		myAsyncServiceLauncher.start_N_AsyncService(num_of_threads,num_of_db_rows_limit);
		
        final long start = System.currentTimeMillis();
        
		ArrayList taskResultList = new ArrayList();
		try {
			for (int i = 1; i <= MigrationConstants.NUM_OF_THREADS; i++) {
				CompletableFuture<String> x = myAsyncService.runMyTask();
				//LOGGER.debug("CompletableFuture = " + x);
				taskResultList.add(x);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.debug("Total Elapsed time to start all threads: "+ (System.currentTimeMillis() - start)); //Elapsed time: 6000
		LOGGER.debug( "taskResultList size= " + taskResultList.size());
		while (true) {
			
			ArrayList<Boolean> statusList =new ArrayList<Boolean>();
			int counter=0;
			LOGGER.debug("--------------------");
			for (Object object : taskResultList) {							
				try {
					CompletableFuture<String> x =(CompletableFuture<String>) object;
					statusList.add(x.isDone());					
					System.out.print("," +counter+ "-isDone= " + x.isDone() + ",");
					counter++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
			if(!statusList.contains(false) ){
				LOGGER.debug("\nall threads are completed.");
				break;
			}			
		}
		
		LOGGER.debug("Total Elapsed time to finish all threads: "+ (System.currentTimeMillis() - start)); //Elapsed time: 8109
		LOGGER.debug("done");
	}
*/

	//query xbureau table and update firestore 
	//@Test
	void testOrchestrateMigration() {
		final long start = System.currentTimeMillis();		      	
		try {
			int dbRetrieveLimit=4;
			xBureauSvc.orchestrateMigration(dbRetrieveLimit);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
		LOGGER.debug("Elapsed time: "+ (System.currentTimeMillis() - start)); //Elapsed time For 11 customers: 7283
		 LOGGER.debug("done");
	}	
	//
	//@Test
	void testAddBureau_and_Reformat_FireStoreDoc() {
		long custID=161949362106625434L;
		final long start = System.currentTimeMillis();		      	
		try {
			xBureauSvc.addBureau_and_Reformat_FireStoreDoc(custID, "bureuaReportContent");
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
		LOGGER.debug("Elapsed time: "+ (System.currentTimeMillis() - start)); //Elapsed time For 11 customers: 7283
		 LOGGER.debug("done");
	}	

}

