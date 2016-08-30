package com.inted.as.hss.fe.cx.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.data.Property;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.csdr.CsdrClient;
import com.inted.as.hss.fe.cx.models.TApplicationServer;
import com.inted.as.hss.fe.cx.models.TIMSSubscription;
import com.inted.as.hss.fe.cx.models.TInitialFilterCriteria;
import com.inted.as.hss.fe.cx.models.TPublicIdentity;
import com.inted.as.hss.fe.cx.models.TSePoTri;
import com.inted.as.hss.fe.cx.models.TServiceProfile;
import com.inted.as.hss.fe.cx.models.TTrigger;
import com.inted.as.hss.fe.sh.models.TShData;
import com.inted.as.hss.fe.sh.models.TShIMSData;
import com.inted.as.hss.fe.utils.CxConstants;
import com.inted.as.hss.fe.utils.DiameterConstants;
import com.inted.as.hss.fe.utils.DiameterUtil;
import com.inted.as.hss.fe.utils.ODataUtil;

/**
 * 
 * Class Adapted from SAR.java at FHoSS
 * 
 */

public class SarService {

	private static final Logger LOG = LoggerFactory.getLogger(SarService.class);
	private Request r;
	private CsdrClient client;
	private final long vendorId = 10415; // CX VENDOR ID

	public SarService(Request r) {
		this.r = r;
		client = new CsdrClient();
	}

	public Answer getResponse() throws Exception {
		Answer response = null;
		AvpSet responseAvps = null;

		// 1. Fetch Mandatory AVPs
		String publicId = DiameterUtil.getPublicIdentity(r);
		String privateId = DiameterUtil.getUserName(r);
		// avps.put("VendorSpecificAppId",
		// DiameterUtil.getVendorSpecificApplicationId(r));
		int authSessionState = DiameterUtil.getAuthSessionState(r);
		String originHost = DiameterUtil.getOriginHost(r);
		String originRealm = DiameterUtil.getOriginRealm(r);
		String destRealm = DiameterUtil.getDestinationRealm(r);
		String serverName = DiameterUtil.getServerName(r);
		int serverAssignmentType = DiameterUtil.getServerAssignmentType(r);
		int userDataAlreadyAvailable = DiameterUtil.getUserDataAlreadyAvailable(r);

		int count = 0;

		if (privateId == null) {
			LOG.warn("Missing Avp: User-Name");
			count++;
		}

		if (publicId == null) {
			LOG.warn("Missing Avp: Public-Identity");
			count++;
		}

		if (authSessionState == -1) {
			LOG.warn("Missing Avp: Auth-Session-State");
			count++;
		}

		if (originHost == null) {
			LOG.warn("Missing Avp: Originating-Host");
			count++;
		}

		if (originRealm == null) {
			LOG.warn("Missing Avp: Originating-Realm");
			count++;
		}

		if (destRealm == null) {
			LOG.warn("Missing Avp: Destination-Realm");
			count++;
		}

		if (serverName == null) {
			LOG.warn("Missing Avp: Server-Name");
			count++;
		}

		if (serverAssignmentType == -1) {
			LOG.warn("Missing Avp: Server-Assignment-Type");
			count++;
		}

		if (userDataAlreadyAvailable == -1) {
			LOG.warn("Missing Avp: User-Data-Already-Available");
			count++;
		}

		// 2. Check for missing Avps
		if (count > 0)
			throw new Exception(
					"DIAMETER MISSING AVP (" + DiameterConstants.ResultCode.DIAMETER_MISSING_AVP.getCode() + ")");

		// 3. Check if subscriber identities & mapping exists
		// if it exists, fetch the data set
		ClientEntity regData = client.fetchRegisteredSet(privateId, publicId);
		if (regData == null)
			throw new Exception("DIAMETER ERROR USER UNKNOWN ("
					+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_USER_UNKNOWN.getCode() + ")");

		// 4. Check if multiple public identities exist for
		// server-assignment-request
		switch (serverAssignmentType) {
		case CxConstants.Server_Assignment_Type_Timeout_Deregistration:
		case CxConstants.Server_Assignment_Type_User_Deregistration:
		case CxConstants.Server_Assignment_Type_Deregistration_Too_Much_Data:
		case CxConstants.Server_Assignment_Type_Timeout_Deregistration_Store_Server_Name:
		case CxConstants.Server_Assignment_Type_User_Deregistration_Store_Server_Name:
		case CxConstants.Server_Assignment_Type_Administrative_Deregistration:
			break;

		default:
			Avp avp = DiameterUtil.findAvp(Avp.PUBLIC_IDENTITY, r);
			if (avp != null) {
				Avp next_public_identity = DiameterUtil.getNextPublicIdentityAVP(r, avp);
				if (next_public_identity != null) {
					throw new Exception("DIAMETER AVP OCCURS TOO MANY TIMES("
							+ DiameterConstants.ResultCode.DIAMETER_AVP_OCCURS_TOO_MANY_TIMES.getCode() + ")");
				}
			}
		}

		// 5. Check if public identity is a Public Service Identifier: TBC
		// 5.1 if yes, Check for PSI Activation value: TBC
		// String impuType = regData.getProperty("impuType") == null ? null:
		// regData.getProperty("impuType").toString();
		// String psiActivation = regData.getProperty("psiActivation") == null ?
		// null: regData.getProperty("psiActivation").toString();

		String userData = null;
		// 6. Check the received server-assignment-type
		switch (serverAssignmentType) {

		case CxConstants.Server_Assignment_Type_Registration:
		case CxConstants.Server_Assignment_Type_Re_Registration:

			// client.updateRegisteredUserInfo(String scscfName, String
			// userState): userState;Registered

			// Download user profile data for CX interface
			userData = downloadProfile(privateId);

			if (userData == null)
				throw new Exception("DIAMETER UNABLE TO COMPLY ("
						+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");

			response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
			responseAvps = response.getAvps();
			// Add Charging Info: TBC
			responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
			responseAvps.addAvp(Avp.USER_DATA_CXDX, userData.getBytes(), vendorId, true, false);

			LOG.info("User with Public Identity: " + publicId + " is Registered!");

			if (serverAssignmentType == CxConstants.Server_Assignment_Type_Registration) {
				// Send Sh registration state notifications for UserState to all
				// subscribers: TBC
			}
			break;
		case CxConstants.Server_Assignment_Type_Unregistered_User:
			// client.updateRegisteredUserInfo(String userState):
			// userState;Registered

			// Download user profile data for CX interface
			userData = downloadProfile(privateId);

			if (userData == null)
				throw new Exception("DIAMETER UNABLE TO COMPLY ("
						+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");

			response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
			responseAvps = response.getAvps();
			// Add Charging Info: TBC
			responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
			responseAvps.addAvp(Avp.USER_DATA_CXDX, userData.getBytes(), vendorId, true, false);

			LOG.info("User with Public Identity: " + publicId + " is Un-Registered!");

			// Send Sh notifications for new userState to all subscribers: TBC
			break;
		case CxConstants.Server_Assignment_Type_Timeout_Deregistration:
		case CxConstants.Server_Assignment_Type_User_Deregistration:
		case CxConstants.Server_Assignment_Type_Deregistration_Too_Much_Data:
		case CxConstants.Server_Assignment_Type_Administrative_Deregistration:
			LOG.info("Dereg: No behaviour yet!"); // TBC

			break;
		case CxConstants.Server_Assignment_Type_Timeout_Deregistration_Store_Server_Name:
		case CxConstants.Server_Assignment_Type_User_Deregistration_Store_Server_Name:
			// HSS decides not to keep SCSCF Name: TBC
			LOG.info("Dereg-Store-Server-Name: No behaviour yet!");
			break;
		case CxConstants.Server_Assignment_Type_No_Assignment:

			String scscfName = regData.getProperty("ScscfId") == null ? null
					: regData.getProperty("ScscfId").getValue().toString();

			if (!scscfName.equals(serverName)) {

				if (scscfName != null)
					response = r.createAnswer(
							DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_IDENTITY_ALREADY_REGISTERED
									.getCode());

				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode());

			} else {

				// Download user profile data for CX interface
				userData = downloadProfile(privateId);

				if (userData == null)
					throw new Exception("DIAMETER UNABLE TO COMPLY ("
							+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");

				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
				responseAvps = response.getAvps();
				// Add Charging Info: TBC
				responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
				responseAvps.addAvp(Avp.USER_DATA_CXDX, userData.getBytes(), vendorId, true, false);

			}

			break;
		case CxConstants.Server_Assignment_Type_Authentication_Failure:
		case CxConstants.Server_Assignment_Type_Authentication_Timeout:
			LOG.info("Authentication Failure & Timeout: No behaviour yet!");
			break;
		}

		return response;
	}

	public String downloadProfile(String privateId) throws Exception {
	
		String userData = null;
		try {
			// A. Fetch Subscription Profile
			ClientEntity subsProfile = client.fetchProfile(privateId);
			
			if (subsProfile == null)
				throw new Exception("DIAMETER ERROR USER UNKNOWN ("
						+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_USER_UNKNOWN.getCode()
						+ "): SUBSCRIPTION PROFILE NOT FOUND");
			
			// B. Fetch Merged Data
			ClientValue v = subsProfile.getProperty("MergedData").getValue();
			ClientComplexValue mergedData = (ClientComplexValue) v;
			
			ClientCollectionValue ifcs = mergedData.get("Ifcs").getCollectionValue();
			
			Iterator<ClientComplexValue> it = ifcs.iterator();
		
			// Service Profiles
			TServiceProfile sp = new TServiceProfile();
			
			int i = 0;
			while (it.hasNext()) {
				ClientComplexValue cv = it.next();
				
				// Trigger Point Cnf
				boolean cnf = cv.get("TriggerPointCnf") == null ? true
						: Boolean.parseBoolean(cv.get("TriggerPointCnf").getValue().toString());
			
				// Application Server URI
				String asUri = cv.get("ApplicationServerUri") == null ? null
						: cv.get("ApplicationServerUri").getValue().toString();
				
				if (asUri == null)
					continue;
				
				// Trigger Point
				TTrigger tp = new TTrigger();
				tp.setConditionTypeCNF(cnf);
				
				// Application Server
				TApplicationServer as = new TApplicationServer();
				as.setServerName(asUri);
				
				// C. Fetch Service Point Triggers
				ClientCollectionValue spts = cv.get("ServicePointTriggers").getCollectionValue();
			
				Iterator<ClientComplexValue> sptIt = spts.iterator();
				
				int j = 0;
				while (sptIt.hasNext()) {
					
					ClientComplexValue sptCv = sptIt.next();
			
					int grpId = sptCv.get("GroupId") == null ? j
							: Integer.parseInt( sptCv.get("GroupId").getValue().toString());

					boolean negated = sptCv.get("ConditionNegated") == null ? false
							: Boolean.parseBoolean( sptCv.get("ConditionNegated").getValue().toString());

					String type = sptCv.get("Type") == null ? null :  sptCv.get("Type").getValue().toString();

					String data = sptCv.get("Data") == null ? null :  sptCv.get("Data").getValue().toString();

					// Service Point Trigger
					TSePoTri spt = new TSePoTri();
					spt.setConditionNegated(negated);
					spt.getGroup().add(grpId);

					if (type.equals("SipMethod")) {
						spt.setMethod(data);
						tp.getSPT().add(spt);
					} else if (type.equals("RequestUri")) {
						spt.setRequestURI(data);
						tp.getSPT().add(spt);
					}
					// else if(type.equals("SessionCase")){
					// spt.setSessionCase(value);
					// }
					// else if(type.equals("SessionDescription"){
					// spt.setSessionDescription(value);
					// }
					// else if(type.equals("SipHeader")){
					// spt.setSIPHeader(value);
					// }
					else if(tp.getSPT().size() < 1) {
						//A dummy SIP method : INVITE
							spt.setMethod("REFER");
							tp.getSPT().add(spt);
					}
				

					j++;
				}
				

				// Ifc
				int priority = cv.get("Priority") == null ? i
						: Integer.parseInt(cv.get("Priority").getValue().toString());
				TInitialFilterCriteria ifc = new TInitialFilterCriteria();
				ifc.setApplicationServer(as);
				ifc.setTriggerPoint(tp);
				ifc.setPriority(priority);

				// Add to Service Profile
				sp.getInitialFilterCriteria().add(ifc);
				i++;
			}

			ClientCollectionValue impus = mergedData.get("Impus").getCollectionValue();
			it = impus.iterator();

			while (it.hasNext()) {
				ClientComplexValue cv = it.next();

				// Public Identity
				TPublicIdentity impu = new TPublicIdentity();

				String sipUri = cv.get("SipUri") == null ? null : cv.get("SipUri").getValue().toString();

				if (sipUri == null)
					continue;

				boolean barred = cv.get("Barred") == null ? false
						: Boolean.parseBoolean(cv.get("Barred").getValue().toString());

				impu.setIdentity(sipUri);
				impu.setBarringIndication(barred);

				sp.getPublicIdentity().add(impu);
			}

			// IMS Subscription
			TIMSSubscription subs = new TIMSSubscription();
			subs.setPrivateID(privateId);
			subs.getServiceProfile().add(sp);

			String data = client.xmlToString(subs);

			userData = data;
		} catch (Exception ex) {
			LOG.error("Could not completely download user profile: " + ex);
			ex.printStackTrace();
		}
		
		if(userData!=null)
			LOG.info("User Data sent to the S-CSCF: \n "+userData);
		
		return userData;
	}
}
