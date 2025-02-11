package com.telus.credit.migration;



import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.cloud.gcp.pubsub.support.converter.JacksonPubSubMessageConverter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telus.credit.migration.thrd.MyAsyncService;
import com.telus.credit.migration.thrd.Runnable.MyRunnableThread;


@Service
public class MessageReceiver {

	 
    //private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);
    @Autowired
    private MyAsyncServiceLauncher myAsyncServiceLauncher;   
    
    @Autowired
    public  XBureauSvc xBureauSvc;
    
    public  JacksonPubSubMessageConverter messageConverter;

    
    public MessageReceiver(XBureauSvc svc, ObjectMapper objectMapper) {
        this.xBureauSvc = svc;
        this.messageConverter = new JacksonPubSubMessageConverter(objectMapper);
    }
    

    @ServiceActivator(inputChannel = "pubSubInputChannel")
    public void messageReceiver(@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage acknowledgeablePubsubMessage) {
        try {
            //LOGGER.info("Purge Message received " + Instant.now() + " {}", message);
            ackMessage(acknowledgeablePubsubMessage.ack(), acknowledgeablePubsubMessage.getPubsubMessage().getMessageId());

            processByTaskType(acknowledgeablePubsubMessage);

        } catch (Exception e) {
            //LOGGER.warn("PUBSUB-100 Exception purging audit data. messageId={}", message.getPubsubMessage().getMessageId(), e);
        	System.out.println("PUBSUB-100 Exception purging audit data. messageId={}"+ acknowledgeablePubsubMessage.getPubsubMessage().getMessageId()+ e);
        }
    }


	private void processByTaskType(BasicAcknowledgeablePubsubMessage message) {
	    MigrationMessageTrigger triggerMessage = messageConverter.fromPubSubMessage(message.getPubsubMessage(), MigrationMessageTrigger.class);
	 	String tasktype	= (triggerMessage!=null)?triggerMessage.getTasktype():"";
		try {       
		    	if("orchestrateMigration".equalsIgnoreCase(tasktype)) {
		    		String num_of_db_rows_limitStr	= (triggerMessage!=null)?triggerMessage.getNum_of_db_rows_limit():"1000000";
		    		int num_of_db_rows_limit=Integer.valueOf(num_of_db_rows_limitStr).intValue();		    		
		    		String num_of_threadsStr	= (triggerMessage!=null)?triggerMessage.getNum_of_threads():"100";
		    		int num_of_threads=Integer.valueOf(num_of_threadsStr).intValue();		    		
		    		myAsyncServiceLauncher.start_N_AsyncService(num_of_threads,num_of_db_rows_limit);		            
		    	}else {
		    		if("updateMetadataInCustDoc".equalsIgnoreCase(tasktype)) {
		    			String key		= (triggerMessage!=null)?triggerMessage.getKey():"";
		    			String value	= (triggerMessage!=null)?triggerMessage.getValue():"";		    			
		            	if(!key.isEmpty() && !value.isEmpty()) {
		            		System.out.println("whereEqualToKey="+key+ ", " +"whereEqualToValue="+value);
		            		xBureauSvc.updateMetadataInCustDoc(key,value);
		            	}   			
		    		}else {
		    			if("reformat_FireStoreDoct".equalsIgnoreCase(tasktype)) {
		    				 int maxNumberOfItemsToReturn=4;
			    			String key		= (triggerMessage!=null)?triggerMessage.getKey():"";
			    			String value	= (triggerMessage!=null)?triggerMessage.getValue():"";		
			            	if(!key.isEmpty() && !value.isEmpty()) {
			            		if("maxNumberOfItemsToReturn".equalsIgnoreCase(key))			          				
			    				 maxNumberOfItemsToReturn=Integer.valueOf(value).intValue();			            			
			            	}			    			
			            	for (int i = 0; i < 750; i++) {
			            		xBureauSvc.reformat_FireStoreDoct(maxNumberOfItemsToReturn);
							}
		    				
		    			}
		    		}
		    	}    
		 }catch(Exception e) {
			 e.printStackTrace();
		 }       
	
	
	}

    private void ackMessage(ListenableFuture<Void> future, String messageId) {
        try {
            future.get();
        } catch (Exception e) {
            //LOGGER.warn("Exception acknowledging pubsub message. messageId=" + messageId);
        }
    }
    
    

}
