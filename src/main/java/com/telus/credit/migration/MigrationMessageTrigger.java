
package com.telus.credit.migration;

public class MigrationMessageTrigger {
/*

30,000,000 records
Each record processing time = 1 sec
30000000 / 24h(86400 sec) = 347 threads
=> 350 threads, each threads processing 100K records => can process 350x100k=35 mil records in 24 hours
with  10 pods => 350/10 = 35 => can process35 mil records in 24/10 =2.5 hours

35 mil = 10 pods X 350 threads X 100k records

1-  deploy svc to 10  pods
2-  trigger/start the svc 
2-1 invoke runMyTask 35 times
2-2 runMyTask invokes orchestrateMigration with to process 100k rows from db


35 mil records= 10 pods X 350 threads X 100k records 100k = 0.34% of 30 million
1000  records= 1 pods X 4 threads X 250 records
1000  records= 2 pods X 2 threads X 250 records .  25% of 1000

 sample message
{
  "tasktype": "orchestrateMigration",
  "num_of_threads": "2",
  "num_of_db_rows_limit": "250"
}


 
  	{
 	 "tasktype":"updateMetadataInCustDoc"
 	 "key":"metadata.CUSTOMER_ID",
 	 "value":"39174124",
 	} 
  	or 
  	{
 	 "tasktype":"updateMetadataInCustDoc"
 	 "key":"metadata.SCHEMA_VERSION",
 	 "value":"2",
 	}
 
{
  "tasktype": "reformat_FireStoreDoct",
  "key": "maxNumberOfItemsToReturn",
  "value": "4"
}


*/	
	private String tasktype;
	
	public String getNum_of_threads() {
		return num_of_threads;
	}
	public void setNum_of_threads(String num_of_threads) {
		this.num_of_threads = num_of_threads;
	}
	public String getNum_of_db_rows_limit() {
		return num_of_db_rows_limit;
	}
	public void setNum_of_db_rows_limit(String num_of_db_rows_limit) {
		this.num_of_db_rows_limit = num_of_db_rows_limit;
	}

	private String num_of_threads;
	private String num_of_db_rows_limit;	

	
	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String val) {
        this.key = val;
    }
    
	private String value ;    
    public String getValue() {
        return value;
    }
    public void setValue(String val) {
        this.value = val;
    }	
}

