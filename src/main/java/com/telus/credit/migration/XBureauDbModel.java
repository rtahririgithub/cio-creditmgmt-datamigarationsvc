package com.telus.credit.migration;

public class XBureauDbModel {
	    private String customerid;
		private String bureauReport;
		private java.util.Date CREATED_TS;
		private java.util.Date UPDATED_TS;
		private String VERSION;
		private boolean processedind;

		public String getCustomerid() {
			return customerid;
		}

		public void setCustomerid(String customerid) {
			this.customerid = customerid;
		}

		
		public boolean isProcessedind() {
			return processedind;
		}

		public void setProcessedind(boolean processedind) {
			this.processedind = processedind;
		}

		public String getBureaureport(){
			return bureauReport;
		}

		public void setBureaureport(String bureauReport){
			this.bureauReport=bureauReport;
		}

		public java.util.Date getCreated_ts(){
			return CREATED_TS;
		}

		public void setCreated_ts(java.util.Date CREATED_TS){
			this.CREATED_TS=CREATED_TS;
		}

		public java.util.Date getUpdated_ts(){
			return UPDATED_TS;
		}

		public void setUpdated_ts(java.util.Date UPDATED_TS){
			this.UPDATED_TS=UPDATED_TS;
		}

		public String getVersion(){
			return VERSION;
		}

		public void setVersion(String VERSION){
			this.VERSION=VERSION;
		}
		
	}