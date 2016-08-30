package com.inted.as.hss.fe.models;

/**
 * 
 * @author Segun Sogunle
 * For searching for the precise subscriber data set
 */

public class SearchKey {

	private String name;
	private String value;

	public SearchKey(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
