package com.inted.as.hss.fe.sh.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.csdr.CsdrClient;
import com.inted.as.hss.fe.models.enums.ShDataReferences;
import com.inted.as.hss.fe.utils.DiameterUtil;

/**
 * 		   @author Segun Sogunle
 * 
 *  	   Class for Sh Interface - User-Data-Request Service
 * 
 *         Reference Doc: 3GPP TS 29.328 This service allows an AS read
 *         transparent and/or non-transparent data for a specified user from the
 *         HSS.
 * 
 */

public class UdrService {

	private static final Logger LOG = LoggerFactory.getLogger(UdrService.class);

	private Request r;

	public UdrService(Request r) {
		this.r = r;
	}

	public String getData() throws Exception {

		String data = null;
		/*
		 * Check for mandatory AVPs
		 */
		Map<String, Object> avps = getAvps(r);
		List<String> missingAvps = checkMandatoryAvps(avps);
		boolean requestIsValid = isValid(missingAvps);

		if (!requestIsValid)
			return data;

		/*
		 * Check if the data request is permitted on the AS
		 */
		// ------------NOT YET IMPLEMENTED!

		/*
		 * Fetch Requested Data
		 */
		data = fetchUserData((List<ShDataReferences>) avps.get("data_references"), avps
				.get("public_identity").toString());

		return data;
	}

	private Map<String, Object> getAvps(Request req) throws AvpDataException {

		Map<String, Object> avps = new HashMap<String, Object>();

		// Vendor-specific-application-Id not implemented yet!
		// Service-Indication-Vector not implemented yet!
		// DSAI-Tag not implemented yet!
		// Check if proxiable flag is set
		avps.put("auth_session_state", DiameterUtil.getAuthSessionState(req));
		avps.put("origin_host", DiameterUtil.getOriginHost(req));
		avps.put("origin_realm", DiameterUtil.getOriginRealm(req));
		avps.put("dest_realm", DiameterUtil.getDestinationRealm(req));
		avps.put("server_name", DiameterUtil.getServerName(req));
		avps.put("data_references", DiameterUtil.getDataReferenceAvps(req));
		avps.put("public_identity", DiameterUtil.getPublicIdentity(req));

		// String serviceIndication = null;
		// List<ShCodes> dataReferences =
		// DiameterUtil.getDataReferenceAvps(req);

		return avps;
	}

	/*
	 * Check for mandatory fields in the message
	 */
	private List<String> checkMandatoryAvps(Map<String, Object> avps) {
		LOG.info("Checking Mandatory AVPS");

		List<String> result = new ArrayList<String>();

		if (Integer.parseInt(avps.get("auth_session_state").toString()) == -1)
			result.add("Auth-Session-State");

		if (avps.get("origin_host") == null)
			result.add("Origin-Host");

		if (avps.get("origin_realm") == null)
			result.add("Origin-Realm");

		if (avps.get("dest_realm") == null)
			result.add("Destination-Realm");

		if (avps.get("server_name") == null)
			result.add("Server-Name");

		if (((List<ShDataReferences>) avps.get("data_references")).size() < 1
				|| avps.get("data_references") == null)
			result.add("Data-Reference");

		if (avps.get("public_identity") == null)
			result.add("Public-Identity");

		return result;
	}

	/**
	 * Checks if all mandatory AVPs are present
	 * 
	 * @return boolean
	 */
	public boolean isValid(List<String> missingAvps) {

		if (missingAvps.size() > 0) {
			String output = "\n***Missing AVP(s)***: ";

			for (int i = 0; i < missingAvps.size(); i++) {
				output = output + " [" + missingAvps.get(i) + "]  ";
			}
			LOG.error(output);
			return false;
		}
		return true;
	}

	/****************************************
	 * Fetches the required user data
	 * 
	 * - Subset of data that can be accessed via Sh AVPs- 3GPP TS 29.328
	 * 
	 * Code | Data | SearchKey AVP(s) | Supported 
	 * 0 | Repository Data | <IMPU/MSISDN, Data-Reference, Service-Indication > | Not Yet 
	 * 10 |  <IMPU/MSISDN/PSI, Data-Reference, Identity-Set > Not yet 
	 * 11 |  IMS User State | <IMPU, Data-Reference> Yes 
	 * 12 |  S-CSCF Name | <IMPU/PSI, Data-Reference> | Yes
	 * 13 |  IntialFilterCriteria <IMPU/PSI, Data-Reference, Server-Name> | Not yet
	 * 16 |  Charging Information <IMPU/PSI/MSISDN, Data-Reference> | Not yet 
	 * 32 |  IMSI
	 * 33 |  IMS Private User Identity
	 ****************************************/

	private String fetchUserData(List<ShDataReferences> dataReferences, String impu)
			throws Exception {

		CsdrClient client = new CsdrClient();

		// Test Request with just IMS_USER_STATE : I need to check this with
		// DataReferences List

		String userData = client.fetchUdrData(dataReferences, impu);

		if (userData == null)
			userData = "Data not found";

		return userData;
	}

}