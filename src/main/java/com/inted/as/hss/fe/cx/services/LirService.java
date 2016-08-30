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

public class LirService {

	private static final Logger LOG = LoggerFactory.getLogger(LirService.class);
	private Request r;
	private CsdrClient client;
	private final long vendorId = 10415; // CX VENDOR ID

	public LirService(Request r) {
		this.r = r;
		client = new CsdrClient();
	}

	public Answer getResponse() throws Exception {
		Answer response = null;
		AvpSet responseAvps = null;

		String publicId = DiameterUtil.getPublicIdentity(r);

		if (publicId == null)
			throw new Exception("DIAMETER MISSING AVP: Public Identity ("
					+ DiameterConstants.ResultCode.DIAMETER_MISSING_AVP + ")");

		int originRequest = DiameterUtil.getOriginatingRequest(r);

		ClientEntity regData = client.fetchRegisteredSet(publicId);

		if (regData == null)
			throw new Exception("DIAMETER ERROR USER UNKNOWN ("
					+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_USER_UNKNOWN.getCode() + ")");

		int impuType = regData.getProperty("ImpuType") == null ? 0
				: EnumConverter.getImpuType(String.valueOf(regData.getProperty("ImpuType").getValue()));

		int userState = regData.getProperty("UserState") == null ? 1
				: EnumConverter.getUserState(String.valueOf(regData.getProperty("UserState").getValue()));

		String scscfName = null;

		if (impuType == CxConstants.Identity_Type_Public_User_Identity) {
			// Get SCSCF Name
			scscfName = regData.getProperty("ScscfId") == null ? null
					: String.valueOf(regData.getProperty("ScscfId").getValue());

		} else {
			LOG.info("Retrieving SCSCF name for Public Service Identity: Not implemented");
			// Not Implemented yet: TBC
		}

		switch (userState) {

		case CxConstants.IMPU_user_state_Registered:
		case CxConstants.IMPU_user_state_Auth_Pending:

			if (scscfName == null)
				throw new Exception(
						"DIAMETER UNABLE TO COMPLY (" + DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY + ")");

			response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
			responseAvps = response.getAvps();
			responseAvps.addAvp(Avp.SERVER_NAME, scscfName.getBytes(), vendorId, true, false);
			// LOG.info("A");
			break;

		case CxConstants.IMPU_user_state_Unregistered:

			if (originRequest == 1) {

				if (scscfName == null || scscfName.equals(""))
					throw new Exception("DIAMETER UNABLE TO COMPLY ("
							+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY + ")");

				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
				responseAvps = response.getAvps();
				responseAvps.addAvp(Avp.SERVER_NAME, scscfName.getBytes(), vendorId, true, false);
				// LOG.info("B");
			} else { // cannot fulfill the request
				throw new Exception(
						"DIAMETER UNABLE TO COMPLY (" + DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY + ")");
			}

			break;

		case CxConstants.IMPU_user_state_Not_Registered:

			if (originRequest == 1) {

				if (scscfName != null) {

					response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
					responseAvps = response.getAvps();
					responseAvps.addAvp(Avp.SERVER_NAME, scscfName.getBytes(), vendorId, true, false);
					// LOG.info("C");
				} else {
					throw new Exception("IMS DIAMETER UNREGISTERED SERVICE ("
							+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_UNREGISTERED_SERVICE.getCode()
							+ ")");
				}

			} else { // cannot fulfill the request
				throw new Exception("IMS DIAMETER ERROR IDENTITY NOT REGISTERED ("
						+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_IDENTITY_NOT_REGISTERED
								.getCode()
						+ ")");
			}
			break;
		}

		return response;
	}
}
