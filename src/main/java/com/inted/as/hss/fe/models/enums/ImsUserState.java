package com.inted.as.hss.fe.models.enums;
/*
 * 
 * @author Segun Sogunle
 * 
 * Enumerated data type for IMS User State according to TS 29.328
 * 
 * 
 * 0 (NOT_REGISTERED)
 * 1 (REGISTERED)
 * 2 (REGISTERED_UNREG_SERVICES)
 * 3 (AUTHENTICATION_PENDING
 * 
*/
public enum ImsUserState {
	
	NOT_REGISTERED,
	REGISTERED,
	REGISTERED_UNREG_SERVICES,
	AUTHENTICATION_PENDING;
	/*
	private int value;

	private ImsUserState(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
	*/
}
