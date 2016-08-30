package com.inted.as.hss.fe.utils;

public class EnumConverter {

	public static int getImpuType(String type) {

		switch (type) {
		case "PublicUserIdentity":
			return 0;
		case "DistinctPsi":
			return 1;
		case "WildcardedPsi":
			return 2;
		default:
			return -1;
		}
	}

	public static int getPsiActivation(String psi) {

		switch (psi) {
		case "Inactive":
			return 0;
		case "Active":
			return 1;
		default:
			return -1;
		}
	}

	public static int getDsai(String dsai) {

		switch (dsai) {
		case "Active":
			return 0;
		case "Inactive":
			return 1;
		default:
			return -1;
		}
	}

	public static int getSptType(String sptType) {

		switch (sptType) {

		case "SipMethod":
			return 0;
		case "RequestUri":
			return 1;
		case "SipHeader":
			return 2;
		case "SessionDescription":
			return 3;
		case "SessionCase":
			return 4;
		default:
			return -1;
		}
	}

	public static int getUserState(String state) {

		switch (state) {

		case "NotRegistered":
			return 0;
		case "Registered":
			return 1;
		case "Unregistered":
			return 2;
		case "AuthenticationPending":
			return 3;

		default:
			return -1;
		}
	}

	public static int getAuthScheme(String scheme) {

		switch (scheme) {
		case "AKAv1":
			return 1;
		case "AKAv2":
			return 2;
		case "Auth_Scheme_MD5":
			return 4;
		case "Digest":
			return 8;
		case "HTTP_Digest_MD5":
			return 16;
		case "Early":
			return 32;
		case "NASS_Bundled":
			return 64;
		case "SIP_Digest":
			return 128;

		default:
			return -1;
		}
	}

	public static int getPpi(String ppi) {

		switch (ppi) {
		case "Registered":
			return 0;
		case "Unregistered":
			return 1;
		case "Any":
			return 2;

		default:
			return -1;
		}
	}
}
