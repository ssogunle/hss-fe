package com.inted.as.hss.fe.cx.services;

import org.apache.olingo.client.api.domain.ClientEntity;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;

import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.csdr.CsdrClient;
import com.inted.as.hss.fe.utils.CxConstants;
import com.inted.as.hss.fe.utils.DiameterConstants;
import com.inted.as.hss.fe.utils.DiameterUtil;
import com.inted.as.hss.fe.utils.EnumConverter;

public class UarService {

	private static final Logger LOG = LoggerFactory.getLogger(UarService.class);
	private Request r;
	private CsdrClient client;
	private final long vendorId = 10415;

	public UarService(Request r) {
		this.r = r;
		client = new CsdrClient();
	}

	public Answer getResponse() throws Exception {

		Answer response = null;
		AvpSet responseAvps = null;

		// 1. Fetch Mandatory AVPs
		String privateId = DiameterUtil.getUserName(r);
		String publicId = DiameterUtil.getPublicIdentity(r);
		String visitedNetworkId = DiameterUtil.getVisitedNetworkId(r);
		int userAuthType = DiameterUtil.getUserAuthType(r);

		int count = 0;
		if (privateId == null) {
			LOG.warn("Missing Avp: User-Name");
			count++;
		}

		if (publicId == null) {
			LOG.warn("Missing Avp: Public-Identity");
			count++;
		}

		if (visitedNetworkId == null) {
			LOG.warn("Missing Avp: Visited-Network ID > ROAMING NOT ALLOWED");
			count++;
		}

		if (userAuthType == -1) {
			LOG.warn("Missing Avp: User-Authorization-Type");
			count++;
		}

		// 2. Check for Missing AVPs
		if (count > 0)
			throw new Exception(
					"DIAMETER MISSING AVP (" + DiameterConstants.ResultCode.DIAMETER_MISSING_AVP.getCode() + ")");

		// 3. Check if subscriber identities & mapping exists
		// if it exists, fetch the data set
		ClientEntity regData = client.fetchRegisteredSet(privateId, publicId);

		if (regData == null)
			throw new Exception("DIAMETER ERROR USER UNKNOWN ("
					+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_USER_UNKNOWN.getCode() + ")");

		// 4. Check for Emergency Registration
		boolean emergencyReg = (DiameterUtil.getUARFlags(r) & DiameterConstants.AVPValue.UAR_Flag_Emergency) != 0;

		if (emergencyReg)
			LOG.debug("Emergency Registration from " + publicId + "/" + privateId);

		boolean barred = regData.getProperty("Barred") == null ? false
				: Boolean.parseBoolean(regData.getProperty("Barred").getValue().toString());

		// 5. Check if IMPU is barred
		if (!emergencyReg && barred)
			throw new Exception("DIAMETER AUTHORIZATION REJECTED ("
					+ DiameterConstants.ResultCode.DIAMETER_AUTHORIZATION_REJECTED.getCode() + ")");

		String visitedNetwork = String.valueOf(regData.getProperty("VisitedNetwork"));

		boolean canRegister = regData.getProperty("CanRegister") == null ? true
				: Boolean.parseBoolean(regData.getProperty("CanRegister").getValue().toString());

		// 6. Check User Authorization Type
		switch (userAuthType) {

		case DiameterConstants.AVPValue.UAT_Registration:
			if (!emergencyReg) {
				if (visitedNetwork == null)
					throw new Exception("IMS DIAMETER ERROR ROAMING NOT ALLOWED ("
							+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_ROAMING_NOT_ALLOWED
									.getCode()
							+ ")");
			}

			if (!canRegister)
				throw new Exception("DIAMETER AUTHORIZATION REJECTED ("
						+ DiameterConstants.ResultCode.DIAMETER_AUTHORIZATION_REJECTED.getCode() + ")");
			break;

		case DiameterConstants.AVPValue.UAT_Registration_and_Capabilities:

			if (!emergencyReg) {
				if (visitedNetwork == null)
					throw new Exception("IMS DIAMETER ERROR ROAMING NOT ALLOWED ("
							+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_ROAMING_NOT_ALLOWED
									.getCode()
							+ ")");
			}

			if (!canRegister)
				throw new Exception("DIAMETER AUTHORIZATION REJECTED ("
						+ DiameterConstants.ResultCode.DIAMETER_AUTHORIZATION_REJECTED.getCode() + ")");

			response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
			responseAvps = response.getAvps();

			// Fetch and add Server Capabilities to responseAvps: TBC

			if (emergencyReg)
				return response;

			break;
		}

		String state = regData.getProperty("UserState") == null ? "NotRegistered"
				: regData.getProperty("UserState").getValue().toString();

		String serverName = regData.getProperty("ScscfId")==null? null:
				regData.getProperty("ScscfId").getValue().toString();
		
		int userState = EnumConverter.getUserState(state);

		// 8. Check IMPU Registration State
		switch (userState) {

		case CxConstants.IMPU_user_state_Registered:

			if (userAuthType == DiameterConstants.AVPValue.UAT_Registration)
				response = r.createAnswer(
						DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_SUBSEQUENT_REGISTRATION.getCode());

			else if (userAuthType == DiameterConstants.AVPValue.UAT_De_Registration)
				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());

			if (response != null && serverName != null) {
				responseAvps = response.getAvps();
				responseAvps.addAvp(Avp.SERVER_NAME, serverName.getBytes(), vendorId, true, false);
			}
			break;
		case CxConstants.IMPU_user_state_Unregistered:

			if (userAuthType == DiameterConstants.AVPValue.UAT_Registration) {

				response = r.createAnswer(
						DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_SUBSEQUENT_REGISTRATION.getCode());

				if (serverName != null) {
					responseAvps = response.getAvps();
					responseAvps.addAvp(Avp.SERVER_NAME, serverName.getBytes(), vendorId, true, false);
				}

			} else if (userAuthType == DiameterConstants.AVPValue.UAT_De_Registration)
				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());

			break;
		case CxConstants.IMPU_user_state_Not_Registered:
		case CxConstants.IMPU_user_state_Auth_Pending:

			if (userAuthType == DiameterConstants.AVPValue.UAT_De_Registration)
				response = r.createAnswer(
						DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_IDENTITY_NOT_REGISTERED
								.getCode());

			else if (userAuthType == DiameterConstants.AVPValue.UAT_Registration) {

				response = r.createAnswer(
						DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_FIRST_REGISTRATION.getCode());

				if (serverName != null) {
					responseAvps = response.getAvps();
					responseAvps.addAvp(Avp.SERVER_NAME, serverName.getBytes(), vendorId, true, false);
				}
				// Add Capabilities: TBC
				break;
			}
		}
		return response;
	}
}