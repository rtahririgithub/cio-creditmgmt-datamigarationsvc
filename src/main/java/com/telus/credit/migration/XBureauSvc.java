package com.telus.credit.migration;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.telus.credit.profile.sync.base.model.TelusCreditDecisionWarning;

import com.telus.credit.profile.sync.base.model.Attachments;
import com.telus.credit.profile.sync.base.model.ContactMedium;
import com.telus.credit.profile.sync.base.model.CreditProfile;
import com.telus.credit.profile.sync.base.model.Customer;
import com.telus.credit.profile.sync.base.model.IdentificationType;
import com.telus.credit.profile.sync.base.model.Individual;
import com.telus.credit.profile.sync.base.model.MediumCharacteristic;
import com.telus.credit.profile.sync.base.model.MetaDataType;
import com.telus.credit.profile.sync.base.model.Organization;
import com.telus.credit.profile.sync.base.model.OrganizationIdentification;
import com.telus.credit.profile.sync.base.model.RelatedParty;
import com.telus.credit.profile.sync.base.model.RiskLevelRiskAssessment;
import com.telus.credit.profile.sync.base.model.TelusCreditProfile;
import com.telus.credit.profile.sync.base.model.TelusIndividualIdentification;
import com.telus.credit.profile.sync.firestore.model.CustomerDocument;





@Component
@Service
public class XBureauSvc {
	@Autowired
	XBureuaRepository xBureuaRepository;
    
	@Autowired
	private static CryptoService cryptoService;
	@Autowired
	public void setEncryptionService(CryptoService cryptoService) {
		XBureauSvc.cryptoService = cryptoService;
	}

	 private static final Logger LOGGER = LoggerFactory.getLogger(XBureauSvc.class);
	@Value("${cp.sync.firestore.schemaVersion}")
	private String schemaVersion;
	
	@Autowired
	private Firestore firestore;

	@Value("${firestore.collection.prefix}")
	private String collectionPrefix;	  

	public String  dummy(int limit) throws Exception {
		return "dummy";
	}
	int orchestrateMigrationCounter=1;
	
	
public String  orchestrateMigration(int dbRetrieveLimit) throws Exception {
	String msg="orchestrateMigration:";

	long startFindByProcessedind = System.currentTimeMillis();	
	List<XBureauDbModel> list = xBureuaRepository.findByProcessedind(false,dbRetrieveLimit);
	
	msg= msg + " findByProcessedind=" +list.size() + ".";
	
	//LOGGER.debug( "startFindByProcessedind= {}ms="+  (System.currentTimeMillis() - startFindByProcessedind) );
		
	long startTs_allStartaddBureau_and_Reformat_FireStoreDoc = System.currentTimeMillis();	
	ArrayList custIdList = new ArrayList();
	for (XBureauDbModel xBureauDbModel : list) {
		long custId = Long.valueOf(xBureauDbModel.getCustomerid() );
		String bureuaReportContent = xBureauDbModel.getBureaureport();
		long start_addBureau_and_Reformat_FireStoreDoc = System.currentTimeMillis();
		addBureau_and_Reformat_FireStoreDoc(custId, bureuaReportContent);
		LOGGER.info("custId="+custId + " :addBureau_and_Reformat_FireStoreDoctook="+  (System.currentTimeMillis() - start_addBureau_and_Reformat_FireStoreDoc) );
		
		custIdList .add(custId);

		long start_updateProcessedind = System.currentTimeMillis();
		int updateProcessedindId = xBureuaRepository.updateProcessedind(custId);		
		LOGGER.info("custId="+custId + " :updateProcessedind="+  (System.currentTimeMillis() - start_updateProcessedind) );
		

	}
	long start_updateProcessedind = System.currentTimeMillis();
	
	xBureuaRepository.updateProcessedindInBulk(custIdList);		
	LOGGER.info( " :updateProcessedindInBulk="+  (System.currentTimeMillis() - start_updateProcessedind) );
		
	
	
	long elapsedTime = (System.currentTimeMillis() - startTs_allStartaddBureau_and_Reformat_FireStoreDoc) ;
	msg= orchestrateMigrationCounter+ " For " + list.size() + " customers, method addBureau_and_Reformat_FireStoreDoc took  "+  elapsedTime + "ms. " +elapsedTime/1000/60 +"  min" ;
	
	orchestrateMigrationCounter++;
	return msg;
}
private void updateMetaData(CustomerDocument custFirestoreDoc) {
		custFirestoreDoc.setMetaData(populateMetaData(custFirestoreDoc.getCustomer()));
		
		Map<String, Object> metaDataMap = custFirestoreDoc.getMetaData();
		
		if(metaDataMap!=null) {			
			Set<Entry<String, Object>> setOfEntries = metaDataMap.entrySet(); 
			Iterator<Entry<String, Object>> iterator = setOfEntries.iterator();
	
			while (iterator.hasNext()) {
			  Entry<String, Object> entry = iterator.next();
			  Object value = entry.getValue();
			  String key = entry.getKey();
	
			  if ( "CREDIT_PROFILE_ID_LIST".equalsIgnoreCase(key)  ) {			    
			   iterator.remove(); 
			  // LOGGER.debug(" removed CREDIT_PROFILE_ID_LIST: " + entry);
			  }		
			  if ("SCHEMA_VERSION".equalsIgnoreCase(key) ) {
			    metaDataMap.put("SCHEMA_VERSION", "1");
			    //LOGGER.debug(" updating SCHEMA_VERSION : exiting value " + entry +" to new value " + "1");			    
			  }				  	  
			}
		}
		custFirestoreDoc.setMetaData(metaDataMap);
	}
	
	
	
	int maxToreturn = 1000;
	public void updateMetadataInCustDoc(String whereEqualToKey, String whereEqualToValue) throws InterruptedException, ExecutionException {
		int count=0;
		while(true) {
			boolean hasMoreDoc = updateMetadataInCustDocBulk(whereEqualToKey,whereEqualToValue);
			if(!hasMoreDoc) {
				break;
			}
			count++;
			//LOGGER.debug("So far batch updateMetadataInCustDoc. count=" + count*maxToreturn);	
		}
		//LOGGER.debug("Total batch updateMetadataInCustDoc. count=" + count*maxToreturn);		

	}
	public ArrayList<String>  getCustomerIdList(String key, String value,int maxToreturn) throws InterruptedException, ExecutionException {
		//long custId
		ArrayList<String> custIdList = new ArrayList<String>();
		boolean hasMoreDoc=true;
		try {
			
			//A CollectionReference can be used for adding documents, getting document references, and querying for documents (using the methods inherited from Query). 
			CollectionReference collectionReference = firestore.collection(collectionPrefix + "customers");
			
			//select method creates and returns a new Query instance that applies a field mask to the result and returns the specified subset of fields. 
			//You can specify a list of field paths to return, or use an empty list to only return the references of matching documents.
			Query query1 = collectionReference.select("customer");				
			
			Query query2 =query1.whereEqualTo(key, value).limit(maxToreturn);
					//query1.whereNotEqualTo(key, value).limit(maxToreturn);
			
			//get operation executes the query and returns the results as QuerySnapshot. An ApiFuture that will be resolved with the results of the Query.
			ApiFuture<QuerySnapshot> aApiFuture = query2.get();
			
			//get operation waits if necessary for the computation to complete, and then retrieves its result. 			
			//A QuerySnapshot contains the results of a query. It can contain zero or more DocumentSnapshotobjects.
			QuerySnapshot querySnapshot = aApiFuture.get();

			 
			if (querySnapshot!=null && !querySnapshot.isEmpty()) {
				//Returns the documents in this QuerySnapshot as a List in order of the query.
				//A QueryDocumentSnapshot contains data read from a document in a Firestore database as part of a query. 
				List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshot.getDocuments();
				if(queryDocumentSnapshots==null) 
					return custIdList;
				
				//LOGGER.debug("matchingDocumentsCount=<" + queryDocumentSnapshots.size() +">");	
				
			 for (QueryDocumentSnapshot queryDocumentSnapshot0 : queryDocumentSnapshots) 
			 {
				//Returns the contents of the document converted to a CustomerDocument POJO.
				CustomerDocument custFirestoreDoc =queryDocumentSnapshot0.toObject(CustomerDocument.class);
				
				if(custFirestoreDoc.getCustomer()!=null) {
					custIdList.add(custFirestoreDoc.getCustomer().getId());
					//LOGGER.info("customerID=" + custFirestoreDoc.getCustomer().getId());	
				}
				
			 }
			}
		}catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		return custIdList;
		
	}
	public boolean updateMetadataInCustDocBulk(String whereEqualToKey, String whereEqualToValue) throws InterruptedException, ExecutionException {
		//long custId
		boolean hasMoreDoc=true;
		try {
			
			//A CollectionReference can be used for adding documents, getting document references, and querying for documents (using the methods inherited from Query). 
			CollectionReference collectionReference = firestore.collection(collectionPrefix + "customers");
			
			//select method creates and returns a new Query instance that applies a field mask to the result and returns the specified subset of fields. 
			//You can specify a list of field paths to return, or use an empty list to only return the references of matching documents.
			Query query1 = collectionReference.select("customer");				
			
			Query query2 = query1.whereEqualTo(whereEqualToKey, whereEqualToValue).limit(maxToreturn);
			
			//get operation executes the query and returns the results as QuerySnapshot. An ApiFuture that will be resolved with the results of the Query.
			ApiFuture<QuerySnapshot> aApiFuture = query2.get();
			
			//get operation waits if necessary for the computation to complete, and then retrieves its result. 			
			//A QuerySnapshot contains the results of a query. It can contain zero or more DocumentSnapshotobjects.
			QuerySnapshot querySnapshot = aApiFuture.get();

			 
			if (querySnapshot!=null && !querySnapshot.isEmpty()) {
				//Returns the documents in this QuerySnapshot as a List in order of the query.
				//A QueryDocumentSnapshot contains data read from a document in a Firestore database as part of a query. 
				List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshot.getDocuments();
				if(queryDocumentSnapshots==null) 
					return false;
				
				//LOGGER.debug("matchingDocumentsCount=<" + queryDocumentSnapshots.size() +">");	
				
			 for (QueryDocumentSnapshot queryDocumentSnapshot0 : queryDocumentSnapshots) 
			 {
				//Returns the contents of the document converted to a CustomerDocument POJO.
				CustomerDocument custFirestoreDoc =queryDocumentSnapshot0.toObject(CustomerDocument.class);
				
				if(custFirestoreDoc.getCustomer()!=null) {
					LOGGER.info("customerID=" + custFirestoreDoc.getCustomer().getId());	
				}
				//keep FireStoreId for when we need to replace the existing doc with the updated doc
				custFirestoreDoc.setFireStoreId(queryDocumentSnapshot0.getId());
			
				//update metaDataMap
				if(custFirestoreDoc!=null) {								
					updateMetaData(custFirestoreDoc);
				}
				//store the updated doc in firestore db
				String custExistingDocumentFireStoreId = custFirestoreDoc.getFireStoreId();
				//get a DocumentReference instance using custExistingDocumentFireStoreId
				DocumentReference docRef = collectionReference.document(custExistingDocumentFireStoreId);
				ApiFuture<String> futureTransaction = null;
				try {
						futureTransaction = firestore.runTransaction(transaction -> 
																			{
																				transaction.set(docRef, custFirestoreDoc);
																			     String firestoreId = docRef.getId();
																		         return firestoreId;					
																			}
																		  );				
				} catch (Throwable e) {
					e.printStackTrace();
			        //throw e;
				}				
			    String fireStoreId =(futureTransaction!=null)?futureTransaction.get():null;
			    LOGGER.info("fireStoreId=" + fireStoreId);		
			}
		 }else {
			 hasMoreDoc=false;
		 }
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		return hasMoreDoc;
	}
	private static final String NAME = "customers";
	protected String getCollectionName() {
		return collectionPrefix + NAME; //example of a collectionName = "creditcol_v1.0_dev_customers"
	}		
	protected Optional<CustomerDocument> getCurrentDocumentId(String custId) throws InterruptedException, ExecutionException {
		QuerySnapshot qSanpshot = firestore.collection(
								getCollectionName())
								.select("customer")
								.whereEqualTo("metadata." + MetaDataType.CUSTOMER_ID.name(), custId)
								.get().get();
		if (!qSanpshot.isEmpty()) {
			List<CustomerDocument> docs = qSanpshot.getDocuments().stream().map(document -> {
				CustomerDocument doc =document.toObject(CustomerDocument.class);
				doc.setFireStoreId(document.getId());
				return doc;
			}).collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(docs)) {
				return Optional.ofNullable(docs.get(0));
			}
		}
		return Optional.ofNullable(null);
	}

	public void addBureau_and_Reformat_FireStoreDoc(long custId, String bureuaReportContent) throws InterruptedException, ExecutionException {
		
		
		try {
			//A CollectionReference can be used for adding documents, getting document references, and querying for documents (using the methods inherited from Query). 
			CollectionReference collectionReference = firestore.collection(collectionPrefix + "customers");
			
			//select method creates and returns a new Query instance that applies a field mask to the result and returns the specified subset of fields. 
			//You can specify a list of field paths to return, or use an empty list to only return the references of matching documents.
			Query query1 = collectionReference.select("customer");
			Query query2 = query1.whereEqualTo("metadata.CUSTOMER_ID", String.valueOf(custId));
			
			//get operation executes the query and returns the results as QuerySnapshot. An ApiFuture that will be resolved with the results of the Query.
			ApiFuture<QuerySnapshot> aApiFuture = query2.get();
			
			//get operation waits if necessary for the computation to complete, and then retrieves its result. 			
			//A QuerySnapshot contains the results of a query. It can contain zero or more DocumentSnapshotobjects.
			QuerySnapshot querySnapshot = aApiFuture.get();

			if (querySnapshot!=null && !querySnapshot.isEmpty()) {
				//Returns the documents in this QuerySnapshot as a List in order of the query.
				//A QueryDocumentSnapshot contains data read from a document in a Firestore database as part of a query. 
				List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshot.getDocuments();		
				QueryDocumentSnapshot queryDocumentSnapshot0 = queryDocumentSnapshots.get(0);
				
				//Returns the contents of the document converted to a CustomerDocument POJO.
				CustomerDocument custFirestoreDoc =queryDocumentSnapshot0.toObject(CustomerDocument.class);
				
				//keep FireStoreId for when we need to replace the existing doc with the updated doc
				custFirestoreDoc.setFireStoreId(queryDocumentSnapshot0.getId());

				//convert schema version 1(ph1.0) customer doc to version 2(ph2.1)
				
				Customer customerV2 = mapV1_to_V2_Schema(custFirestoreDoc.getCustomer());
				

				//update existing creditprofile  with bureuaReportContent as an attachment 			
				List<Attachments> attachments = createAttachment(bureuaReportContent);								
				TelusCreditProfile creditProfile0 =customerV2.getCreditProfile().get(0);
				creditProfile0.setAttachments(encryptCreditProfileAttachments(attachments));
				
				
				//update firestore document 
				custFirestoreDoc.setCustomer(customerV2);
				custFirestoreDoc.setMetaData(populateMetaData(custFirestoreDoc.getCustomer()));
				 
				//store the updated doc in firestore db
				int i=0;
				String custExistingDocumentFireStoreId = custFirestoreDoc.getFireStoreId();
				//get a DocumentReference instance using custExistingDocumentFireStoreId
				DocumentReference docRef = collectionReference.document(custExistingDocumentFireStoreId);
				ApiFuture<String> futureTransaction;
				try {
						futureTransaction = firestore.runTransaction(transaction -> 
																			{
																				transaction.set(docRef, custFirestoreDoc);
																			     String firestoreId = docRef.getId();
																		         return firestoreId;					
																			}
																		  );				
				} catch (Throwable e) {
					e.printStackTrace();
			        throw e;
				}				
			    String fireStoreId =(futureTransaction!=null)?futureTransaction.get():null;
			  //  LOGGER.debug("fireStoreId=" + fireStoreId);		
			}

			//LOGGER.debug("HERE");
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}  

	}
	
	
public void reformat_FireStoreDoct(int maxNumberOfItemsToReturn) throws InterruptedException, ExecutionException {

		try {
			//A CollectionReference can be used for adding documents, getting document references, and querying for documents (using the methods inherited from Query). 
			CollectionReference collectionReference = firestore.collection(collectionPrefix + "customers");
			
			//select method creates and returns a new Query instance that applies a field mask to the result and returns the specified subset of fields. 
			//You can specify a list of field paths to return, or use an empty list to only return the references of matching documents.
			Query query1 = collectionReference.select("customer");
			Query query2 = query1.whereEqualTo("customer.individual.atReferredType", "Individual").limit(maxNumberOfItemsToReturn);

			query2 = query1.whereEqualTo("customer.createUpdateFlag", "U");
		
			//get operation executes the query and returns the results as QuerySnapshot. An ApiFuture that will be resolved with the results of the Query.
			ApiFuture<QuerySnapshot> aApiFuture = query2.get();
			
			//get operation waits if necessary for the computation to complete, and then retrieves its result. 			
			//A QuerySnapshot contains the results of a query. It can contain zero or more DocumentSnapshotobjects.
			QuerySnapshot querySnapshot = aApiFuture.get();

			
			
			if (querySnapshot!=null && !querySnapshot.isEmpty()) {
				//Returns the documents in this QuerySnapshot as a List in order of the query.
				//A QueryDocumentSnapshot contains data read from a document in a Firestore database as part of a query. 
				List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshot.getDocuments();
				//if(queryDocumentSnapshots==null)  return false;
				
				LOGGER.debug("matchingDocumentsCount=<" + queryDocumentSnapshots.size() +">");	
				
			 for (QueryDocumentSnapshot queryDocumentSnapshot0 : queryDocumentSnapshots) 
			 {
				//Returns the contents of the document converted to a CustomerDocument POJO.
				CustomerDocument custFirestoreDoc =queryDocumentSnapshot0.toObject(CustomerDocument.class);
				
				if(custFirestoreDoc.getCustomer()!=null) {
					LOGGER.info("customerID=" + custFirestoreDoc.getCustomer().getId());	
				}
				//keep FireStoreId for when we need to replace the existing doc with the updated doc
				custFirestoreDoc.setFireStoreId(queryDocumentSnapshot0.getId());
			
				//convert schema version 1(ph1.0) customer doc to version 2(ph2.1)
				Customer customerV2 = mapV1_to_V2_Schema(custFirestoreDoc.getCustomer());				
				//update firestore document 
				custFirestoreDoc.setCustomer(customerV2);
				custFirestoreDoc.setMetaData(populateMetaData(custFirestoreDoc.getCustomer()));
				 
				//store the updated doc in firestore db
				String custExistingDocumentFireStoreId = custFirestoreDoc.getFireStoreId();
				//get a DocumentReference instance using custExistingDocumentFireStoreId
				DocumentReference docRef = collectionReference.document(custExistingDocumentFireStoreId);
				ApiFuture<String> futureTransaction = null;				
				try {
						futureTransaction = firestore.runTransaction(transaction -> 
																			{
																				transaction.set(docRef, custFirestoreDoc);
																			     String firestoreId = docRef.getId();
																		         return firestoreId;					
																			}
																		  );				
				} catch (Throwable e) {
					e.printStackTrace();
			        //throw e;
				}				
			    String fireStoreId =(futureTransaction!=null)?futureTransaction.get():null;
			    LOGGER.info("fireStoreId=" + fireStoreId);		
			 }
			}
		} catch (Throwable e) {
				e.printStackTrace();
		        //throw e;
			}
	
		}
	private List<Attachments> createAttachment(String kbContent) {
		List<Attachments> attachments = new ArrayList<Attachments>();
		if(kbContent==null ||kbContent.isEmpty()) {
			return attachments;
		}
		Attachments attachment= new Attachments();					
		attachment.setAtReferredType("CreditBureauReport");
		attachment.setAttachmentType("Bureau Report");
		attachment.setMimeType("application/text");
		attachment.setSourceCode("EQUIFAX");		
		String encodedkbContent = new String(Base64.encodeBase64(kbContent.getBytes()));
		attachment.setContent(encodedkbContent);	
		attachments.add(attachment);
		return attachments;
	}  

	private Map<String, Object> populateMetaData(Customer customer) {
		Map<String, Object> metaData = new HashMap<>();
		metaData.put(MetaDataType.CUSTOMER_ID.name(), customer.getId());
		metaData.put(MetaDataType.SCHEMA_VERSION.name(), schemaVersion);

		if (ObjectUtils.isNotEmpty(customer.getCreditProfile())) 
		{
			//popluate metadata.CREDIT_PROFILE_ID_LIST 
			metaData.putAll(populateCreditProfileIdList(customer.getCreditProfile()));
			
			//popluate metadata. individual or organization credential IDs
			TelusCreditProfile primaryTelusCreditProfile = getPrimayCreditProfile (customer.getCreditProfile());
			RelatedParty relatedPartyCustomer = getCustomerRoleRelatedParty(primaryTelusCreditProfile);
			
			if(relatedPartyCustomer!=null && relatedPartyCustomer.getIndividual()!=null ) {
				metaData.putAll(populateIndividualMetaData(relatedPartyCustomer.getIndividual()));
			}
			else 
			{
				if(relatedPartyCustomer!=null &&  relatedPartyCustomer.getOrganization()!=null) {
					metaData.putAll(populateCompanyMetaData(relatedPartyCustomer.getOrganization()));
				}
			}
		
		}
		//LOGGER.debug("metaData generated::{}", metaData);
		return metaData;
	}	
	
	
	
	private Map<String, List> populateCreditProfileIdList(List<TelusCreditProfile> creditProfile) {
		Map<String, List> metaData = new HashMap<>();
		List<String> ids = creditProfile.stream().map(TelusCreditProfile :: getId).collect(Collectors.toList());
		metaData.put(MetaDataType.CREDIT_PROFILE_ID_LIST.name(), ids);
		return metaData;
	}

	private Map<String, String> populateIndividualMetaData(Individual person) {
		Map<String, String> metaData = new HashMap<>();
		if (!CollectionUtils.isEmpty(person.getIndividualIdentification())) {
			for (TelusIndividualIdentification telusIdentity : person.getIndividualIdentification()) {
				if (IdentificationType.getIdentificationType(telusIdentity.getIdentificationType()) != null) {
					metaData.put(
								MetaDataType.getIdentificationType( telusIdentity.getIdentificationType()).name() ,
								telusIdentity.getIdentificationIdHashed() );
				}
			}
		}
		if(!StringUtils.isBlank(person.getBirthDate())) {
			metaData.put(MetaDataType.BIRTH_DATE.name(), person.getBirthDate());
		}
		if(!CollectionUtils.isEmpty(person.getContactMedium())) {
			Optional<MediumCharacteristic> preferedContact = person.getContactMedium().stream().filter(contact ->
					Boolean.TRUE.equals(contact.getPreferred())
					&& ObjectUtils.isNotEmpty(contact.getValidFor()) && ObjectUtils.isNotEmpty(contact.getValidFor().getEndDateTime()) 
					&& toUtcTimestamp(contact.getValidFor().getEndDateTime()).after(Timestamp.from(Instant.now())))
					.map(ContactMedium::getCharacteristic).filter(medium -> !StringUtils.isBlank(medium.getPostCode()))
					.findFirst();
			if(!preferedContact.isPresent()) {
				Optional<MediumCharacteristic> contactExist =person.getContactMedium().stream().filter(contact -> ObjectUtils.isNotEmpty(contact.getValidFor()) 
						&& ObjectUtils.isNotEmpty(contact.getValidFor().getEndDateTime()) 
						&& toUtcTimestamp(contact.getValidFor().getEndDateTime()).after(Timestamp.from(Instant.now())))
						.map(ContactMedium::getCharacteristic).filter(medium -> !StringUtils.isBlank(medium.getPostCode()))
						.findFirst();
				if (contactExist.isPresent()) {
					metaData.put(MetaDataType.POSTAL_CODE.name(), contactExist.get().getPostCode() );
				}
			} else {
				metaData.put(MetaDataType.POSTAL_CODE.name(),preferedContact.get().getPostCode() );
			}
		}
		
		//LOGGER.debug("Individual metaData generated::{}", metaData);
		return metaData;
	}

	private Map<String, String> populateCompanyMetaData(Organization org) {
		Map<String, String> metaData = new HashMap<>();
		if (!CollectionUtils.isEmpty(org.getOrganizationIdentification())) {
			for (OrganizationIdentification orgIdentity : org.getOrganizationIdentification()) {
				if (IdentificationType.getIdentificationType(orgIdentity.getIdentificationType()) != null) {
					metaData.put(
							IdentificationType.getIdentificationType(orgIdentity.getIdentificationType()).name(),
							orgIdentity.getIdentificationIdHashed()
							);					
				}
			}
		}
		
		//LOGGER.debug("Organisation metaData generated::{}", metaData);
		return metaData;
	}
	
	private TelusCreditProfile getPrimayCreditProfile(List<TelusCreditProfile> creditProfile) {
		if(creditProfile!=null) {
			for (TelusCreditProfile telusCreditProfile : creditProfile) {
				if(!"SEC".equalsIgnoreCase( telusCreditProfile.getCustomerCreditProfileRelCd()) ){
					return telusCreditProfile;
				}
			}
		}
		return null;
	}	
	
	private RelatedParty getCustomerRoleRelatedParty(TelusCreditProfile primaryTelusCreditProfile) {
		if(primaryTelusCreditProfile==null) {
			return null;
		}
		List<RelatedParty> relatedPartyCustomerList = primaryTelusCreditProfile.getRelatedParty();
		if(relatedPartyCustomerList!=null ) {
			for (RelatedParty relatedPartyCustomer : relatedPartyCustomerList) {
				if( "customer".equalsIgnoreCase(relatedPartyCustomer.getRole()) ){
					return relatedPartyCustomer;
				}
			}
		}
		return null;
		
	}	
	
    private  Timestamp toUtcTimestamp(String isoDatetime) {
        if (StringUtils.isBlank(isoDatetime)) {
            return null;
        }
        Instant instant = Instant.parse(isoDatetime);
        return new Timestamp(instant.minusSeconds(ZoneId.systemDefault().getRules().getOffset(instant).getTotalSeconds()).toEpochMilli());
    }	
    
	private Customer mapV1_to_V2_Schema(Customer firestoreCustomer) {		
		Customer updatedCustomer = new Customer();
		
		updatedCustomer.setId(firestoreCustomer.getId());
		updatedCustomer.setAccountInfo(firestoreCustomer.getAccountInfo());
		updatedCustomer.setBaseType(firestoreCustomer.getBaseType());
		updatedCustomer.setCreateUpdateFlag(firestoreCustomer.getCreateUpdateFlag());
		updatedCustomer.setCustUid(firestoreCustomer.getCustUid());
		updatedCustomer.setEventDescription(firestoreCustomer.getEventDescription());
		updatedCustomer.setEventReceivedTime(firestoreCustomer.getEventReceivedTime());		
		
		//updatedCustomer.setIndividual(Individual)
		//updatedCustomer.setOrganisation(Organization)
		updatedCustomer.setSchemaLocation(firestoreCustomer.getSchemaLocation());	
		updatedCustomer.setSubmitterEventTime(firestoreCustomer.getSubmitterEventTime());
		updatedCustomer.setType(firestoreCustomer.getType());		
		
		List<TelusCreditProfile> v2_CreditProfiles = new ArrayList<TelusCreditProfile>();		
		updatedCustomer.setBaseType(StringUtils.defaultIfEmpty(firestoreCustomer.getBaseType(), ""));
		
		if (firestoreCustomer.getCreditProfile().size() > 0) {	
			//Creditprofile with TelusCharacteristic are V1 schema
			if (ObjectUtils.isNotEmpty(firestoreCustomer.getCreditProfile().get(0).getTelusCharacteristic())) {			
				for (TelusCreditProfile creditProfileV1 : firestoreCustomer.getCreditProfile()) {					
					TelusCreditProfile creditProfileV2 = convertCreditProfile_V1_to_V2(firestoreCustomer,creditProfileV1);					
					v2_CreditProfiles.add(creditProfileV2);
				}
					updatedCustomer.setCreditProfile(v2_CreditProfiles);
				return updatedCustomer;
			}
		}
		return firestoreCustomer;
	}
	

private TelusCreditProfile convertCreditProfile_V1_to_V2(Customer customer, TelusCreditProfile creditProfileV1) {
	String customerId = customer.getId();
	TelusCreditProfile creditProfileV2 = new TelusCreditProfile();

	creditProfileV2.setId(creditProfileV1.getId());
	creditProfileV2.setAttachments(creditProfileV1.getAttachments());
	creditProfileV2.setCreditClassCd(creditProfileV1.getTelusCharacteristic().getCreditClassCd());
	
	List<TelusCreditDecisionWarning> warningsV1 = creditProfileV1.getTelusCharacteristic().getWarningHistoryList();
	for (TelusCreditDecisionWarning telusCreditDecisionWarning : warningsV1) {
		telusCreditDecisionWarning.setId(telusCreditDecisionWarning.getId());
		telusCreditDecisionWarning.setWarningDetectionTs((telusCreditDecisionWarning.getWarningDetectionDate()));
	}
	creditProfileV2.setWarnings(creditProfileV1.getTelusCharacteristic().getWarningHistoryList());
	
 
	creditProfileV2.setCreationTs(creditProfileV1.getCreationTs());
	creditProfileV2.setValidFor(creditProfileV1.getValidFor());
	creditProfileV2.setBaseType(creditProfileV1.getBaseType());
	creditProfileV2.setType(creditProfileV1.getType());
	
	
	creditProfileV2.setCreditRiskLevelDecisionCd(creditProfileV1.getTelusCharacteristic().getRiskLevelDecisionCd());
	creditProfileV2.setClpCreditLimitAmt(creditProfileV1.getTelusCharacteristic().getClpCreditLimit());
	creditProfileV2.setClpContractTermNum(creditProfileV1.getTelusCharacteristic().getClpContractTerm());
	creditProfileV2.setClpRatePlanAmt(creditProfileV1.getTelusCharacteristic().getClpRatePlanAmt());
	creditProfileV2.setCreditRiskLevelTs(creditProfileV1.getTelusCharacteristic().getRiskLevelDt());
	
	
	creditProfileV2.setPrimaryCreditScoreCd(creditProfileV1.getTelusCharacteristic().getPrimaryCreditScoreCd());
	creditProfileV2.setPrimaryCreditScoreTypeCd(creditProfileV1.getTelusCharacteristic().getPrimaryCreditScoreTypeCd());
	
	creditProfileV2.setBureauDecisionCd(creditProfileV1.getTelusCharacteristic().getBureauDecisionCode());
	creditProfileV2.setBureauDecisionCdTxtEn(creditProfileV1.getTelusCharacteristic().getBureauDecisionMessage());
	creditProfileV2.setBureauDecisionCdTxtFr(creditProfileV1.getTelusCharacteristic().getBureauDecisionMessage_fr());
	creditProfileV2.setCreditProgramName(creditProfileV1.getTelusCharacteristic().getCreditProgramName());
	creditProfileV2.setCreditClassCd(creditProfileV1.getTelusCharacteristic().getCreditClassCd());
	creditProfileV2.setCreditClassTs(creditProfileV1.getTelusCharacteristic().getCreditClassDate());
	creditProfileV2.setCreditDecisionCd(creditProfileV1.getTelusCharacteristic().getCreditDecisionCd());
	creditProfileV2.setCreditDecisionTs(creditProfileV1.getTelusCharacteristic().getCreditDecisionDate());
	creditProfileV2.setCreditRiskLevelNum(creditProfileV1.getTelusCharacteristic().getRiskLevelNumber());
	creditProfileV2.setCreditRiskLevelTs(creditProfileV1.getTelusCharacteristic().getRiskLevelDt());
	creditProfileV2.setCreditRiskLevelDecisionCd(creditProfileV1.getTelusCharacteristic().getRiskLevelDecisionCd());
	creditProfileV2.setClpCreditLimitAmt(creditProfileV1.getTelusCharacteristic().getClpCreditLimit());
	creditProfileV2.setAverageSecurityDepositAmt(creditProfileV1.getTelusCharacteristic().getAverageSecurityDepositAmt());
	
	RiskLevelRiskAssessment lastRiskAssessment = new RiskLevelRiskAssessment();									
	lastRiskAssessment.setAssessmentMessageCd(creditProfileV1.getTelusCharacteristic().getAssessmentMessageCode());
	creditProfileV2.setLastRiskAssessment(lastRiskAssessment);
	
	RiskLevelRiskAssessment riskLevelRiskAssessment = new RiskLevelRiskAssessment();
	riskLevelRiskAssessment.setId(creditProfileV1.getTelusCharacteristic().getCreditAssessmentId());
	creditProfileV2.setRiskLevelRiskAssessment(riskLevelRiskAssessment);
	
	//populate RelatedParty and engaged party
	{
		RelatedParty customerRelatedParty = new RelatedParty();
		customerRelatedParty.setId(customerId);
		customerRelatedParty.setRole("customer");
		customerRelatedParty.setType("Customer");
		if(customer.getIndividual()!=null) {											
			customerRelatedParty.setIndividual(customer.getIndividual());
		}else {
			if(customer.getOrganisation()!=null) {
				customerRelatedParty.setOrganization((customer.getOrganisation()));				
			}				
		}	
		List<RelatedParty> relatedParties = new ArrayList<RelatedParty>();
		relatedParties.add(customerRelatedParty);							
		creditProfileV2.setRelatedParty(relatedParties );								
	}
	
	creditProfileV2.setTelusCharacteristic(null);
	return creditProfileV2;
}	


private List<Attachments> encryptCreditProfileAttachments(List<Attachments> attachments) {
	if(attachments!=null) {
		try{
			for (Attachments attachment : attachments) {
				
				attachment.setContent(cryptoService.encrypt(attachment.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw e;
		}		
	}
	return attachments;
}

}
