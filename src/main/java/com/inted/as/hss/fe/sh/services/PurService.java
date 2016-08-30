package com.inted.as.hss.fe.sh.services;

import java.util.HashMap;
import java.util.Map;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.utils.DiameterUtil;

/**
 * 
 * @author Segun Sogunle Class for Sh Interface - Profile-Update-Request Service
 * 
 *  Reference Doc: 3GPP TS 29.328
 * 
 * The service allows an Application Server to do the following:
 * 
 * -Update repository(transparent) data stored at the HSS for each IMS Public User Identity
 * -Update the PSI Activation State of a distinct Public Service Identity in the HSS
 * -Update the Dynamic Service Activation Info stored at the HSS
 * -Update the Short Message Service Registration Info stored at the HSS
 * -update the STN-SR stored at the HSS.
 * 
 */
public class PurService {

	private static final Logger LOG = LoggerFactory.getLogger(PurService.class);

	private Request r;

	public PurService(Request r) {
		this.r = r;
	}
	
	
private Map<String,Object> getAvps(Request req) throws AvpDataException{
		
		Map<String,Object> avps = new HashMap<String,Object>();
		
		//Vendor-specific-application-Id not implemented yet!
		
		avps.put("auth_session_state", DiameterUtil.getAuthSessionState(req));
		avps.put("origin_host", DiameterUtil.getOriginHost(req));
		avps.put("origin_realm", DiameterUtil.getOriginRealm(req));
		avps.put("dest_realm", DiameterUtil.getDestinationRealm(req));
		avps.put("server_name", DiameterUtil.getServerName(req));
		avps.put("data_references", DiameterUtil.getDataReferenceAvps(req));
		avps.put("public_identity", DiameterUtil.getPublicIdentity(req));
		avps.put("user_data", DiameterUtil.getPublicIdentity(req));
		
		return avps;
	}
}
