package com.inted.as.hss.fe.models.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Segun Sogunle Diameter Sh Data References
 */

public enum ShDataReferences {


	// Repository Data
	DATA_REFERENCE_REPOSITORY_DATA(0),

	// IMS Public Identity
	DATA_REFERENCE_IMS_PUBLIC_IDENTITY(10),

	// IMS User State
	DATA_REFERENCE_IMS_USER_STATE(11),

	// S-CSCF Server Name
	DATA_REFERENCE_SCSCF_NAME(12),

	// Intial Filter Criteria
	DATA_REFERENCE_IFC(13),

	// Location Information
	DATA_REFERENCE_LOCATION_INFO(14),

	// User State
	DATA_REFERENCE_USER_STATE(15),

	// Charging Information
	DATA_REFERENCE_CHARGING_INFO(16),

	// Tel URI
	DATA_REFERENCE_MSISDN(17),

	// Public Service Identity Activation
	DATA_REFERENCE_PSI_ACTIVATION(18),

	// Dynamic Service Activation Info
	DATA_REFERENCE_DSAI(19),

	// 20 is reserved

	// Service Level Trace Info
	DATA_REFERENCE_SERVICE_LEVEL_TRACE_INFO(21),

	// IP Address Secure Binding Information
	DATA_REFERENCE_IP_SECURE_BINDING_INFO(22),

	// User Equipment REachability
	DATA_REFERENCE_UE_REACHABILITY(23),

	// TADS Info
	DATA_REFERENCE_TADS_INFO(24),

	// International Mobile Subscriber Identity (IMSI)
	DATA_REFERENCE_IMSI(32),

	// IMS Private User Identity
	DATA_REFERENCE_IMS_PRIVATE_IDENTITY(33),

	// INVALID OR UNRECOGNIZED CODE
	DATA_REFERENCE_INVALID(-1);

	int value;

	private static Map<Integer, ShDataReferences> map = new HashMap<Integer, ShDataReferences>();

	private ShDataReferences(int value) {
		this.value = value;
	}

	static {
		for (ShDataReferences shCode : ShDataReferences.values()) {
			map.put(shCode.value, shCode);
		}
	}

	public static ShDataReferences valueOf(int value) {
		return map.get(value);
	}

}
