package com.inted.as.hss.fe.models.enums;

public enum UserAuthorizationType {
	REGISTRATION(0), DE_REGISTRATION(1), REGISTRATION_AND_CAPABILITIES(2);

	int value;

	private UserAuthorizationType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
