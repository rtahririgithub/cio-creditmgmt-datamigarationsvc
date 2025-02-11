package com.telus.credit.migration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;



@SpringBootApplication
@EnableAsync
public class DataMigrationSpringBootApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationSpringBootApplication.class);
   
	@Autowired
    private MyAsyncServiceLauncher myAsyncServiceLauncher;   
    
	@Autowired
	XBureuaRepository xBureuaRepository;    
	
    
	@Autowired
	GenConfig genConfig; 	
	
	public static void main(String[] args) {
		System.out.println("DataMigrationSpringBootApplication-1");
		SpringApplication.run(DataMigrationSpringBootApplication.class, args);		
		
		Properties prop = new Properties();
		try {
			prop.load(DataMigrationSpringBootApplication.class.getClassLoader().getResourceAsStream("git.properties"));			
			LOGGER.info("Git information: {}", prop);						
		} catch (Exception e) {
			LOGGER.warn("Couldn't load git information: {}", e.getMessage());
		}	
	}
	
	
	
	
	@PostConstruct	
	public void process() {
		
		InetAddress inetAddresstLocalHost=null;
		String podnameHostname = System.getenv("HOSTNAME");
		try {			
			LOGGER.info("genConfig.getDatasourceUrl= {}", genConfig.getDatasourceUrl());
			inetAddresstLocalHost = java.net.InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
		LOGGER.info("podName/HOSTNAME="+ podnameHostname + ". " + "java.net.InetAddress.getLocalHost="+ inetAddresstLocalHost);		
		
		
		//1000 records=> 2 pods X 2 threads each, X processing 250 records
		//50,000 records=> 4 pods X 3 threads each, X processing 5000 records
		//400,000 records=> 4 pods X 4 threads each, X processing 50000 records
		int num_of_db_rows_limit=50000; //1000000;
		int num_of_threads=5;		//350;
		myAsyncServiceLauncher.start_N_AsyncService(num_of_threads,num_of_db_rows_limit);	
		LOGGER.info("start_N_AsyncService completed");

	}
	
}
