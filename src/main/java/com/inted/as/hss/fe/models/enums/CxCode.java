package com.inted.as.hss.fe.models.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Segun Sogunle
 * 
 */
public enum CxCode {

	/*
	 * I-CSCF - HSS
	 */
	
	// User Authorization Request
	UAR_COMMAND_CODE(300),

	// User Authorization Answer
	UAA_COMMAND_CODE(300),

	// Location Information Request
	LIR_COMMAND_CODE(302),

	// Location Information Answer
	LIA_COMMAND_CODE(302),

	/*
	 * S-CSCF - HSS
	 */

	// Multimedia Authorization Request
	MAR_COMMAND_CODE(303),

	// Multimedia Authorization Answer
	MAA_COMMAND_CODE(303),

	// Server Assignment Request
	SAR_COMMAND_CODE(301),

	// Server Assignment Answer
	SAA_COMMAND_CODE(301),

	// Push Profile Request
	PPR_COMMAND_CODE(305),

	// Push Profile Answer
	PPA_COMMAND_CODE(305),

	// Registration Termination Request
	RTR_COMMAND_CODE(304),

	// Registration Termination Answer
	RTA_COMMAND_CODE(304);

	int value;

	private static Map<Integer, ShDataReferences> map = new HashMap<Integer, ShDataReferences>();

	private CxCode(int value) {
		this.value = value;
	}

	static {
		for (ShDataReferences shCode : ShDataReferences.values()) {
			map.put(shCode.value, shCode);
		}
	}

	public int getValue(){
		return value;
	}
	public static ShDataReferences valueOf(int value) {
		return map.get(value);
	}

}
