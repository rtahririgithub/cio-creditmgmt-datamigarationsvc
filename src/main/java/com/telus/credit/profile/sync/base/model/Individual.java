package com.telus.credit.profile.sync.base.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Individual implements RelatedPartyInterface {
	
	public Individual() {
	}
	private String id;
	private String birthDate;
	private String role;
	private List<ContactMedium> contactMedium;
	private List<TelusIndividualIdentification> individualIdentification;
	

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public List<ContactMedium> getContactMedium() {
		return contactMedium;
	}

	public void setContactMedium(List<ContactMedium> contactMedium) {
		this.contactMedium = contactMedium;
	}

	public List<TelusIndividualIdentification> getIndividualIdentification() {
		return individualIdentification;
	}

	public void setIndividualIdentification(List<TelusIndividualIdentification> individualIdentification) {
		this.individualIdentification = individualIdentification;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public PartyType getRelatedPartyType() {
		return PartyType.INDIVIDUAL;
	}
	
	private String relatedPartyType;
	public void setRelatedPartyType(String relatedPartyType) {
		this.relatedPartyType = relatedPartyType;
	}
	
	private String atReferredType;
	public void setAtReferredType(String atReferredType) {	
		this.atReferredType = atReferredType;
	}	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
