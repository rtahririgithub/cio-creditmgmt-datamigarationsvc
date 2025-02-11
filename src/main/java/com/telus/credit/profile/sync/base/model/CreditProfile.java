package com.telus.credit.profile.sync.base.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.Exclude;


import java.math.BigDecimal;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CreditProfile {

   private String id;

   private String creationTs;


   private String creditRiskLevelNum;

   private String primaryCreditScoreCd;

   private TimePeriod validFor;
   private String baseType;
   private String type;


   private String creditCheckConsentCd;
   private String applicationProvinceCd;
   private String lineOfBusiness;
   private String primaryCreditScoreTypeCd;

   private String bureauDecisionCd;
   

   private String bureauDecisionCdTxtEn;
   
   private String bureauDecisionCdTxtFr;
   
   private String creditProgramName;

   private String creditClassCd;
   private String creditClassTs;

   private String creditDecisionCd;

   private String creditDecisionTs;

   private String creditRiskLevelDecisionCd;


   private String creditRiskLevelTs;

   private BigDecimal clpRatePlanAmt;
   
   private Integer clpContractTermNum;


   private BigDecimal clpCreditLimitAmt;
   private BigDecimal averageSecurityDepositAmt;

   private RiskLevelRiskAssessment riskLevelRiskAssessment;
   private RiskLevelRiskAssessment lastRiskAssessment;

   
   private List<TelusCreditDecisionWarning> warnings;
   
    private List<RelatedParty> relatedParty;
   
    @JsonProperty("creditProfileLegacyId")   
    @JsonInclude
    private Long creditProfileLegacyId;
	 @JsonIgnore 
	 public Long getCreditProfileLegacyId() { 
		 return creditProfileLegacyId; 
	 } 
	 public void setCreditProfileLegacyId(Long creditProfileLegacyId) { 
		 this.creditProfileLegacyId = creditProfileLegacyId;
	}   
    
   @JsonProperty("attachments")
   private List<Attachments> attachments;
   
   @JsonProperty("channel")
   private TelusChannel channel;

   private String statusCd;
   private String statusTs;
   
   private String customerCreditProfileRelCd;
   
   //private Long creditAssessmentId;
  
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }

   public String getCreationTs() {
      return creationTs;
   }

   public void setCreationTs(String creditProfileDate) {
      this.creationTs = creditProfileDate;
   }
   public String getCreditRiskLevelNum() {
      return creditRiskLevelNum;
   }
   public void setCreditRiskLevelNum(String creditRiskRating) {
      this.creditRiskLevelNum = creditRiskRating;
   }

   public String getPrimaryCreditScoreCd() {
      return primaryCreditScoreCd;
   }
   public void setPrimaryCreditScoreCd(String creditScore) {
      this.primaryCreditScoreCd = creditScore;
   }
   public TimePeriod getValidFor() {
      return validFor;
   }
   public void setValidFor(TimePeriod validFor) {
      this.validFor = validFor;
   }

   @JsonProperty("@baseType")
   @Exclude
   public String getBaseType() {
      return  this.baseType;
   }

   @Exclude
   public void setBaseType(String baseType) {
      this.baseType = baseType;
   }

   @JsonProperty("@type")
   @Exclude
   public String getType() {
      return this.type;
   }

   @Exclude
   public void setType(String type) {
      this.type = type;
   }

   public String getCreditCheckConsentCd() {
      return creditCheckConsentCd;
   }

   public void setCreditCheckConsentCd(String creditProfileConsentCd) {
      this.creditCheckConsentCd = creditProfileConsentCd;
   }

   public String getApplicationProvinceCd() {
      return applicationProvinceCd;
   }

   public void setApplicationProvinceCd(String applicationProvinceCd) {
      this.applicationProvinceCd = applicationProvinceCd;
   }

   public String getLineOfBusiness() {
      return lineOfBusiness;
   }

   public void setLineOfBusiness(String lineOfBusiness) {
      this.lineOfBusiness = lineOfBusiness;
   }

   public String getPrimaryCreditScoreTypeCd() {
      return primaryCreditScoreTypeCd;
   }

   public void setPrimaryCreditScoreTypeCd(String primaryCreditScoreTypeCd) {
      this.primaryCreditScoreTypeCd = primaryCreditScoreTypeCd;
   }

   public String getBureauDecisionCd() {
      return bureauDecisionCd;
   }

   public void setBureauDecisionCd(String bureauDecisionCd) {
      this.bureauDecisionCd = bureauDecisionCd;
   }

   public String getBureauDecisionCdTxtEn() {
      return bureauDecisionCdTxtEn;
   }

   public void setBureauDecisionCdTxtEn(String bureauDecisionMessage) {
      this.bureauDecisionCdTxtEn = bureauDecisionMessage;
   }

   public String getBureauDecisionCdTxtFr() {
      return bureauDecisionCdTxtFr;
   }

   public void setBureauDecisionCdTxtFr(String bureauDecisionMessage_fr) {
      this.bureauDecisionCdTxtFr = bureauDecisionMessage_fr;
   }

   public String getCreditProgramName() {
      return creditProgramName;
   }

   public void setCreditProgramName(String creditProgramName) {
      this.creditProgramName = creditProgramName;
   }

   public String getCreditClassCd() {
      return creditClassCd;
   }

   public void setCreditClassCd(String creditClassCd) {
      this.creditClassCd = creditClassCd;
   }

   public String getCreditClassTs() {
      return creditClassTs;
   }

   public void setCreditClassTs(String creditClassDate) {
      this.creditClassTs = creditClassDate;
   }

   public String getCreditDecisionCd() {
      return creditDecisionCd;
   }

   public void setCreditDecisionCd(String creditDecisionCd) {
      this.creditDecisionCd = creditDecisionCd;
   }

   public String getCreditDecisionTs() {
      return creditDecisionTs;
   }

   public void setCreditDecisionTs(String creditDecisionDate) {
      this.creditDecisionTs = creditDecisionDate;
   }

   public String getCreditRiskLevelDecisionCd() {
      return creditRiskLevelDecisionCd;
   }

   public void setCreditRiskLevelDecisionCd(String riskLevelDecisionCd) {
      this.creditRiskLevelDecisionCd = riskLevelDecisionCd;
   }

   public String getCreditRiskLevelTs() {
      return creditRiskLevelTs;
   }

   public void setCreditRiskLevelTs(String riskLevelDt) {
      this.creditRiskLevelTs = riskLevelDt;
   }

   public BigDecimal getClpRatePlanAmt() {
      return clpRatePlanAmt;
   }

   public void setClpRatePlanAmt(BigDecimal clpRatePlanAmt) {
      this.clpRatePlanAmt = clpRatePlanAmt;
   }

   public Integer getClpContractTermNum() {
      return clpContractTermNum;
   }

   public void setClpContractTermNum(Integer clpContractTerm) {
      this.clpContractTermNum = clpContractTerm;
   }

   public BigDecimal getClpCreditLimitAmt() {
      return clpCreditLimitAmt;
   }

   public void setClpCreditLimitAmt(BigDecimal clpCreditLimit) {
      this.clpCreditLimitAmt = clpCreditLimit;
   }

   public BigDecimal getAverageSecurityDepositAmt() {
      return averageSecurityDepositAmt;
   }

   public void setAverageSecurityDepositAmt(BigDecimal averageSecurityDepositAmt) {
      this.averageSecurityDepositAmt = averageSecurityDepositAmt;
   }

   public RiskLevelRiskAssessment getRiskLevelRiskAssessment() {
      return riskLevelRiskAssessment;
   }

   public void setRiskLevelRiskAssessment(RiskLevelRiskAssessment riskLevelRiskAssessment) {
      this.riskLevelRiskAssessment = riskLevelRiskAssessment;
   }

   public RiskLevelRiskAssessment getLastRiskAssessment() {
	      return lastRiskAssessment;
	   }

   public void setLastRiskAssessment(RiskLevelRiskAssessment lastRiskAssessment) {
      this.lastRiskAssessment = lastRiskAssessment;
   }
   
   public List<TelusCreditDecisionWarning> getWarnings() {
      return warnings;
   }

   public void setWarnings(List<TelusCreditDecisionWarning> warningHistoryList) {
      this.warnings = warningHistoryList;
   }

   public List<RelatedParty> getRelatedParty() {
      return relatedParty;
   }

   public void setRelatedParty(List<RelatedParty> relatedParties) {
      this.relatedParty = relatedParties;
   }

   public List<Attachments> getAttachments() {
      return attachments;
   }

   public void setAttachments(List<Attachments> attachments) {
      this.attachments = attachments;
   }

   public TelusChannel getChannel() {
      return channel;
   }

   public void setChannel(TelusChannel channel) {
      this.channel = channel;
   }

   public String getStatusCd() {
      return statusCd;
   }

   public void setStatusCd(String creditProfileStatusCd) {
      this.statusCd = creditProfileStatusCd;
   }

   public String getStatusTs() {
      return statusTs;
   }

   public void setStatusTs(String creditProfileStatusTs) {
      this.statusTs = creditProfileStatusTs;
   }

   public String getCustomerCreditProfileRelCd() {
      return customerCreditProfileRelCd;
   }

   public void setCustomerCreditProfileRelCd(String customerCreditProfileRelCd) {
      this.customerCreditProfileRelCd = customerCreditProfileRelCd;
   }

	/*
	 * public Long getCreditAssessmentId() { return creditAssessmentId; }
	 * 
	 * public void setCreditAssessmentId(Long creditAssessmentId) {
	 * this.creditAssessmentId = creditAssessmentId; }
	 */
	/*
   @Exclude
   @JsonIgnore
   public String[] getNotNullFieldNames() {
      List<String> fieldNames = new ArrayList<>(18);
      if(ObjectUtils.isNotEmpty(this.creditRiskLevelNum)) {
         fieldNames.add("creditRiskLevelNum");
      }
      if(ObjectUtils.isNotEmpty(this.primaryCreditScoreCd)) {
         fieldNames.add("creditScore");
      }
      if(ObjectUtils.isNotEmpty(this.creditDecisionCd)) {
         fieldNames.add("creditDecisionCd");
      }
      if(ObjectUtils.isNotEmpty(this.creditProgramName)) {
         fieldNames.add("creditProgramName");
      }
      if(ObjectUtils.isNotEmpty(this.statusCd)) {
         fieldNames.add("statusCd");
      }
      if(ObjectUtils.isNotEmpty(this.statusTs)) {
         fieldNames.add("statusTs");
      }
      if(ObjectUtils.isNotEmpty(this.creditRiskLevelDecisionCd)) {
         fieldNames.add("creditRiskLevelDecisionCd");
      }
      if(ObjectUtils.isNotEmpty(this.creditRiskLevelTs)) {
         fieldNames.add("riskLevelDt");
      }
      if(ObjectUtils.isNotEmpty(this.creditCheckConsentCd)) {
         fieldNames.add("creditProfileConsentCd");
      }
      if(ObjectUtils.isNotEmpty(this.applicationProvinceCd)) {
         fieldNames.add("applicationProvinceCd");
      }
      if(ObjectUtils.isNotEmpty(this.lineOfBusiness)) {
         fieldNames.add("lineOfBusiness");
      }
      if(ObjectUtils.isNotEmpty(this.averageSecurityDepositAmt)) {
         fieldNames.add("averageSecurityDepositAmt");
      }
      if(ObjectUtils.isNotEmpty(this.baseType)) {
         fieldNames.add("baseType");
      }
      if(ObjectUtils.isNotEmpty(this.bureauDecisionCd)) {
         fieldNames.add("bureauDecisionCd");
      }
      if(ObjectUtils.isNotEmpty(this.bureauDecisionCdTxtEn)) {
         fieldNames.add("bureauDecisionCdTxtEn");
      }
      if(ObjectUtils.isNotEmpty(this.bureauDecisionCdTxtFr)) {
         fieldNames.add("bureauDecisionMessage_fr");
      }
      if(ObjectUtils.isNotEmpty(this.clpContractTermNum)) {
         fieldNames.add("clpContractTerm");
      }
      if(ObjectUtils.isNotEmpty(this.clpCreditLimitAmt)) {
         fieldNames.add("clpCreditLimit");
      }
      if(ObjectUtils.isNotEmpty(this.clpRatePlanAmt)) {
         fieldNames.add("clpRatePlanAmt");
      }
      if(ObjectUtils.isNotEmpty(this.statusCd)) {
         fieldNames.add("statusCd");
      }
      if(ObjectUtils.isNotEmpty(this.statusTs)) {
         fieldNames.add("statusTs");
      }
      if(ObjectUtils.isNotEmpty(this.customerCreditProfileRelCd)) {
         fieldNames.add("customerCreditProfileRelCd");
      }
      if(ObjectUtils.isNotEmpty(this.riskLevelRiskAssessment)) {
         fieldNames.add("riskLevelRiskAssessment");
      }
      return fieldNames.toArray(new String[fieldNames.size()]);
   }
 */
   @Override
   public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
