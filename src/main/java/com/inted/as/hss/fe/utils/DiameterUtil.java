package com.inted.as.hss.fe.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.models.enums.ShDataReferences;
import com.inted.as.hss.fe.utils.auth.AuthenticationVector;

public class DiameterUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(DiameterUtil.class);

	
	
	public static String getSessionId(Message message) throws AvpDataException{
		
		Avp avp = findAvp(Avp.SESSION_ID, message);
		
		if(avp!=null)
			return avp.getUTF8String();
		
		return null;
	}
	
	public static int getUARFlags(Message message) throws AvpDataException{
		
		Avp avp = findAvp(Avp.UAR_FLAGS, message);
		
		if (avp != null){
			return avp.getInteger32();
		}
		return 0;
	}
	
	public static Avp getNextPublicIdentityAVP(Message message, Avp prevAvp){
		
		Iterator it;
		if (message.getAvps() != null){
			it = message.getAvps().iterator();
			while (it.hasNext()){
				Avp currAvp = (Avp) it.next();
				if (currAvp != null && currAvp.getCode() == DiameterConstants.AVPCode.IMS_PUBLIC_IDENTITY){
					if (currAvp.equals(prevAvp))
						continue;
					else{
						return currAvp;
					}
				}
			}
		}
		
		return null;
	}
	/*
	 * Vendor Specific Application ID
	 */
	public static long[] getVendorSpecificApplicationId(Message message)
			throws AvpDataException {

		long vsai[] = null;

		Avp avp = findAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, message);

		if (avp != null) {
			AvpSet set = avp.getGrouped();

			if (set.size() == 2) {
				long vendorId =set.getAvp(Avp.VENDOR_ID).getInteger64();
				long authAppId = set.getAvp(Avp.AUTH_APPLICATION_ID).getInteger64();

				vsai[0] = vendorId;
				vsai[1] = authAppId;
			}

		}
		return vsai;
	}
	
	public static int getOriginatingRequest(Message message) throws AvpDataException{
		
		Avp avp = findAvp(Avp.ORIGINATING_REQUEST, message);

		if (avp != null)
			return  avp.getInteger32();
		
		return -1;
	}
	
	/*
	 * Charging Info
	 */
	/*
	public static Avp createChargingInformation(Message message, ClientComplexValue cv){
		// Primary_ccf, secondary_ccf, primary_ecf, secondary_ecf
		
		String priCcf = cv.get("primary_ccf")==null? null: String.valueOf(cv.get("primary_ccf").getValue());
		String secCcf = cv.get("secondary_ccf")==null? null: String.valueOf(cv.get("secondary_ccf").getValue());
		String priEcf = cv.get("primary_ecf")==null? null: String.valueOf(cv.get("primary_ecf").getValue());
		String secEcf = cv.get("secondary_ecf")==null? null: String.valueOf(cv.get("secondary_ecf").getValue());
				
		
		AVP chargingInfoAVP = new AVP(DiameterConstants.AVPCode.IMS_CHARGING_INFORMATION, true, 
				DiameterConstants.Vendor.V3GPP);
		
		
		if (chargingInfo.getPri_ccf() != null && !chargingInfo.getPri_ccf().equals("")){
			AVP pri_ccf_AVP = new AVP(DiameterConstants.AVPCode.IMS_PRI_CHRG_COLL_FN_NAME, true, 
					DiameterConstants.Vendor.V3GPP);
			pri_ccf_AVP.setData(chargingInfo.getPri_ccf());
			chargingInfoAVP.addChildAVP(pri_ccf_AVP);
		}

		if (chargingInfo.getPri_ecf() != null && !chargingInfo.getPri_ecf().equals("")){
			AVP pri_ecf_AVP = new AVP(DiameterConstants.AVPCode.IMS_PRI_EVENT_CHARGING_FN_NAME, true, 
					DiameterConstants.Vendor.V3GPP);
			pri_ecf_AVP.setData(chargingInfo.getPri_ecf());
			chargingInfoAVP.addChildAVP(pri_ecf_AVP);
		}
		
		if (chargingInfo.getSec_ccf() != null && !chargingInfo.getSec_ccf().equals("")){
			AVP sec_ccf_AVP = new AVP(DiameterConstants.AVPCode.IMS_SEC_CHRG_COLL_FN_NAME, true, 
					DiameterConstants.Vendor.V3GPP);
			sec_ccf_AVP.setData(chargingInfo.getSec_ccf());
			chargingInfoAVP.addChildAVP(sec_ccf_AVP);
		}

		if (chargingInfo.getSec_ecf() != null && !chargingInfo.getSec_ecf().equals("")){
			AVP sec_ecf_AVP = new AVP(DiameterConstants.AVPCode.IMS_SEC_EVENT_CHARGING_FN_NAME, true, 
					DiameterConstants.Vendor.V3GPP);
			sec_ecf_AVP.setData(chargingInfo.getSec_ecf());
			chargingInfoAVP.addChildAVP(sec_ecf_AVP);
		}
		
	//	message.addAVP(chargingInfoAVP);
	}
	*/
	/*
	 * Request URI
	 */

	public static String getRequestUri(Message message){
		
		Avp cAvp= null;
		Avp avp = findAvp(DiameterConstants.AVPCode.IMS_SIP_AUTH_DATA_ITEM, message);

		if (avp == null)
			return null;
		
		try {
		AvpSet set = 	avp.getGrouped();
			cAvp =set.getAvp(DiameterConstants.AVPCode.AVP_ETSI_SIP_Authorization);
			
			if (cAvp == null)
				return null;
		} 
		catch (AvpDataException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			
			AvpSet set = cAvp.getGrouped();
			Avp uriAvp = set.getAvp(DiameterConstants.AVPCode.AVP_ETSI_Digest_URI);
			
			if (uriAvp == null)
				return null;
			
			return uriAvp.getUTF8String();
		}
		catch (AvpDataException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * Request Method
	 */
	
	public static String getRequestMethod(Message message){
		
		Avp cAvp= null;
		Avp avp =findAvp(DiameterConstants.AVPCode.IMS_SIP_AUTH_DATA_ITEM, message);

		if (avp == null)
			return null;
		
		try {
			AvpSet set = avp.getGrouped();
			cAvp = set.getAvp(DiameterConstants.AVPCode.AVP_ETSI_SIP_Authorization);
			
			if (cAvp == null)
				return null;
		} 
		catch (AvpDataException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			AvpSet set =cAvp.getGrouped();
			Avp uriAvp = set.getAvp(DiameterConstants.AVPCode.AVP_ETSI_Digest_Method);
			
			if (uriAvp == null)
				return null;
			
			return uriAvp.getUTF8String();
		}
		catch (AvpDataException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * Sip Number Auth Items
	 */

	public static int getSipNumAuthItems(Message message) throws AvpDataException {

		int avCount = -1;
		
		Avp avp = findAvp(Avp.SIP_NUMBER_AUTH_ITEMS, message);

		if (avp != null)
			avCount  = avp.getInteger32();
		
		return avCount;
	}

	/*
	 * SIP Auth Data Item A Grouped AVP Type
	 */

	public static Avp getSipAuthDataItem(Message message) {

		Avp avp = findAvp(Avp.SIP_AUTH_DATA_ITEM, message);

		return avp;
	}

	/*
	 * User Data AVP
	 */

	public static String getUserData(Message message) throws AvpDataException {

		String userData = null;

		Avp avp = findAvp(Avp.USER_DATA_SH, message);

		if (avp != null)
			userData = avp.getUTF8String();

		return userData;
	}

	/*
	 * User Authorization Type
	 */
	public static int getUserAuthType(Message message) throws AvpDataException {

		int authType = 0;

		Avp avp = findAvp(Avp.USER_AUTORIZATION_TYPE, message);

		if (avp != null)
			authType = avp.getInteger32();

		return authType;
	}

	/*
	 * Visited Network AVP
	 */
	public static String getVisitedNetworkId(Message message)
			throws AvpDataException {

		String visitedNetworkId = null;

		Avp avp = findAvp(Avp.VISITED_NETWORK_ID, message);

		if (avp != null)
			visitedNetworkId = avp.getUTF8String();

		//log.info("visitedNetworkId = "+visitedNetworkId);
		return visitedNetworkId;
	}

	/*
	 * User Data Already Available
	 */
	public static int getUserDataAlreadyAvailable(Message message) throws AvpDataException{
				
		Avp avp = findAvp(Avp.USER_DATA_ALREADY_AVAILABLE, message);
		
		if (avp != null){
			return avp.getInteger32();
		}
		
		return -1;
	}
	/*
	 * Server Assignment Type
	 */
	public static int getServerAssignmentType(Message message) throws AvpDataException{
		int sat = -1;
		
		Avp avp = findAvp(Avp.SERVER_ASSIGNMENT_TYPE,message);
		
		if (avp != null)
			sat = avp.getInteger32();
	
		return sat;
	}
	

	public static String getUserName(Message message) throws AvpDataException {

		String userName = null;

		Avp avp = findAvp(Avp.USER_NAME, message);

		if (avp != null)
			userName = avp.getUTF8String();

		return userName;
	}

	/*
	 * Auth-Session-State AVP Idle = 0,Pending = 1, Open =2, Disconnected =3
	 */

	public static int getAuthSessionState(Message message)
			throws AvpDataException {
		int authState = -1;

		Avp avp = findAvp(Avp.AUTH_SESSION_STATE, message);

		if (avp != null)
			authState = avp.getInteger32();

		return authState;
	}

	/*
	 * Authorized Application ID AVP
	 */
	public static long getAuthAppId(Message message) throws AvpDataException {
		long authAppId = -111111;

		Avp avp = findAvp(Avp.AUTH_APPLICATION_ID, message);

		if (avp != null)
			authAppId = avp.getInteger64();

		return authAppId;

	}
	/*
	 * Server Assignment Type
	 */
	

	/*
	 * Server Name AVP
	 */
	public static String getServerName(Message message) throws AvpDataException {

		String serverName = null;

		Avp avp = findAvp(Avp.SERVER_NAME, message);

		if (avp != null)
			serverName = avp.getUTF8String();

		return serverName;
	}

	/*
	 * Origin Host AVP
	 */
	public static String getOriginHost(Message message) throws AvpDataException {

		String originHost = null;

		Avp avp = findAvp(Avp.ORIGIN_HOST, message);

		if (avp != null)
			originHost = avp.getUTF8String();

		return originHost;
	}

	/*
	 * Origin Realm AVP
	 */

	public static String getOriginRealm(Message message)
			throws AvpDataException {

		String originRealm = null;

		Avp avp = findAvp(Avp.ORIGIN_REALM, message);

		if (avp != null)
			originRealm = avp.getUTF8String();

		return originRealm;
	}

	/*
	 * Destination Realm AVP
	 */
	public static String getDestinationRealm(Message message)
			throws AvpDataException {

		String destinationRealm = null;

		Avp avp = findAvp(Avp.DESTINATION_REALM, message);

		if (avp != null)
			destinationRealm = avp.getUTF8String();

		return destinationRealm;
	}

	/*
	 * Public Identity AVP
	 */
	public static String getPublicIdentity(Message message)
			throws AvpDataException {

		String publicIdentity = null;

		Avp avp = findAvp(Avp.PUBLIC_IDENTITY, message);

		if (avp != null) {

			publicIdentity = avp.getUTF8String();

		}
		return publicIdentity;
	}

	/*
	 * Data Reference AVPs
	 */
	public static List<ShDataReferences> getDataReferenceAvps(Message message)
			throws AvpDataException {
		List<ShDataReferences> result = new ArrayList<ShDataReferences>();

		// log.info("Counting all Data-Reference AVPs ");
		for (Avp avp : message.getAvps()) {

			if (avp.getCode() == Avp.DATA_REFERENCE) {

				// && avp.getInteger32() !=
				// ShCode.DATA_REFERENCE_INVALID.getValue()
				if (!result.contains(ShDataReferences.valueOf(avp
						.getInteger32()))) {

					result.add(ShDataReferences.valueOf(avp.getInteger32()));
				}

			}
		}
		// log.info(result.size()+" AVP(s) found!");
		return result;
	}

	public static Avp findAvp(int avpCode, Message msg) {
		// log.info("Searching through AVP Set for Code: ["+avpCode+"]");
		AvpSet avpSet = msg.getAvps();

		for (Avp avp : avpSet) {
			// log.info("avp = "+String.valueOf(avp));
			if (avp.getCode() == avpCode) { // log.info("avp MATCH =
											// "+String.valueOf(avp));
				return avp;
			}

		}
		return null;
	}

	

	public static void addAuthVectors(List avList, AvpSet avps, long appVendorId) {
		//LOG.info("ADD Auth Vectors!!!");
		
		if (avList == null) {
			return ;
		}
		AvpSet set = null;
		Iterator it = avList.iterator();
		int itemNo = 1;
		while (it.hasNext()) {
			AuthenticationVector av = (AuthenticationVector) it.next();
			//LOG.info("AD[1]");
			AvpSet authDataItemAvp = avps.addGroupedAvp(
					Avp.SIP_AUTH_DATA_ITEM, appVendorId, true, false);

			authDataItemAvp.addAvp(Avp.SIP_ITEM_NUMBER, itemNo++, appVendorId, true, false);
			
			//LOG.info("AD[2]");
			switch (av.getAuth_scheme()) {
			
			case CxConstants.Auth_Scheme_AKAv1:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_AKAv1_Name, appVendorId, true, false, true);
				break;
			case CxConstants.Auth_Scheme_AKAv2:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_AKAv2_Name, appVendorId, true, false, true);
				break;
			case CxConstants.Auth_Scheme_MD5:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_MD5_Name, appVendorId, true, false, true);
				break;
			case CxConstants.Auth_Scheme_Digest:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_Digest_Name, appVendorId, true, false, true);
				break;
			case CxConstants.Auth_Scheme_SIP_Digest:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_SIP_Digest_Name, appVendorId, true, false,
						true);
				break;
			case CxConstants.Auth_Scheme_HTTP_Digest_MD5:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_HTTP_Digest_MD5_Name, appVendorId, true,
						false, true);
				break;
			case CxConstants.Auth_Scheme_NASS_Bundled:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_NASS_Bundled_Name, appVendorId, true, false,
						true);
				break;
			case CxConstants.Auth_Scheme_Early:
				authDataItemAvp.addAvp(Avp.SIP_AUTHENTICATION_SCHEME,
						CxConstants.Auth_Scheme_Early_Name, appVendorId, true, false, true);
				break;
			}
		//	LOG.info("AD[3]");
			if (((av.getAuth_scheme() & CxConstants.Auth_Scheme_Early) != 0)) {
			//	LOG.info("AD[4]");
				String sip = av.getIp();
				if (sip != null) {
					byte fIpLen = 4;
					int poz = 0, k = 0;
					byte[] result = new byte[fIpLen];
					for (int i = 0; i < sip.length(); i++)
						if (sip.charAt(i) == '.' && k < fIpLen) {
							try {
								result[k++] = (byte) Integer.parseInt(sip
										.substring(poz, i));
							} catch (NumberFormatException nfe) {
								result[k++] = -1;
							}
							poz = i + 1;
						}
					if (k < fIpLen && poz < sip.length()) {
						try {
							result[k] = (byte) Integer.parseInt(sip
									.substring(poz));
						} catch (NumberFormatException nfe) {
							result[k] = -1;
						}
					}

					authDataItemAvp.addAvp(
							DiameterConstants.AVPCode.FRAMED_IP_ADDRESS,
							result, appVendorId, true, false);

				}
			} else if (((av.getAuth_scheme() & CxConstants.Auth_Scheme_NASS_Bundled) != 0)) {
			//	LOG.info("AD[5]");
				authDataItemAvp.addAvp(
						DiameterConstants.AVPCode.AVP_Line_Identifier,
						av.getIp(),appVendorId, true, false, true);
			} else if (((av.getAuth_scheme() & CxConstants.Auth_Scheme_Digest) != 0)) {
			//	LOG.info("AD[6]");
				authDataItemAvp.addAvp(
						DiameterConstants.AVPCode.AVP_CableLabs_Digest_Realm,
						av.getSipAuthenticate(), true, false);
				AvpSet authorizationAvp = avps
						.addGroupedAvp(
								DiameterConstants.AVPCode.AVP_CableLabs_SIP_Digest_Authenticate, appVendorId,
								true, false);
				authorizationAvp.addAvp(
						DiameterConstants.AVPCode.AVP_CableLabs_Digest_HA1,
						av.getSipAuthorization(), appVendorId, true, false);
				authDataItemAvp.addAvp(authorizationAvp);

			} else if (((av.getAuth_scheme() & CxConstants.Auth_Scheme_SIP_Digest) != 0)) {
				//LOG.info("AD[7]");
				AvpSet authorizationAvp = avps.addGroupedAvp(
						DiameterConstants.AVPCode.IMS_SIP_DIGEST_AUTHENTICATE,
						true, false);
				authorizationAvp.addAvp(DiameterConstants.AVPCode.DIGEST_HA1,
						av.getSipAuthorization(), appVendorId, true, false);
				authorizationAvp.addAvp(DiameterConstants.AVPCode.DIGEST_REALM,
						av.getSipAuthenticate(), appVendorId, true, false);
				authDataItemAvp.addAvp(authorizationAvp);

			} else if (((av.getAuth_scheme() & CxConstants.Auth_Scheme_HTTP_Digest_MD5) != 0)) {
				//LOG.info("AD[8]");
				authDataItemAvp.addAvp(
						DiameterConstants.AVPCode.AVP_ETSI_Digest_Realm,
						av.getSipAuthenticate(), appVendorId, true, false);

				AvpSet etsi_auth = getEtsiSipAuthenticate(av.getRealm(),
						av.getSipAuthenticate(), null,
						CxConstants.Auth_Scheme_HTTP_Digest_MD5_Name,
						av.getHA1(), avps, appVendorId);

				AvpSet etsi_auth_info = getEtsiSipAuthenticationInfo(av
						.getResult(), avps, appVendorId);

				if (etsi_auth != null)
					authDataItemAvp.addAvp(etsi_auth);

				if (etsi_auth_info != null)
					authDataItemAvp.addAvp(etsi_auth_info);

			} else {
				//LOG.info("AD[9]");
				//LOG.info("here at Authenticate");
				
				authDataItemAvp.addAvp(
						DiameterConstants.AVPCode.IMS_SIP_AUTHENTICATE,
						av.getSipAuthenticate(), appVendorId, true, false);
				
				authDataItemAvp.addAvp(
						DiameterConstants.AVPCode.IMS_SIP_AUTHORIZATION, 
						av.getSipAuthorization(), appVendorId, true, false);

				if (av.getConfidentialityityKey() != null)
					authDataItemAvp.addAvp(
							DiameterConstants.AVPCode.IMS_CONFIDENTIALITY_KEY,
							av.getConfidentialityityKey(), appVendorId, true, false);

				if (av.getIntegrityKey() != null)
					authDataItemAvp.addAvp(
							DiameterConstants.AVPCode.IMS_INTEGRITY_KEY,
							av.getIntegrityKey(), appVendorId,true, false);

			}
		//	LOG.info("AD[10]");
			// message.addAVP(authDataItem);
			set = authDataItemAvp;
		//	LOG.info("AuthVect SIZE: "+authDataItemAvp.size());
		}
		//return set;
	}

	public static AvpSet getEtsiSipAuthenticationInfo(byte[] r_auth, AvpSet avps, long appVendorId) {
		AvpSet etsiSipAuthAvp = avps.addGroupedAvp(
				DiameterConstants.AVPCode.AVP_ETSI_SIP_Authentication_Info, appVendorId,
				true, false);

		if (r_auth != null)
			etsiSipAuthAvp.addAvp(
					DiameterConstants.AVPCode.AVP_ETSI_Digest_Response_Auth, 
					r_auth, appVendorId, true, false);

		return etsiSipAuthAvp;
	}

	public static AvpSet getEtsiSipAuthenticate(String realm, byte[] nonce,
			String domain, String algorithm, byte[] auth_ha1,AvpSet avps, long appVendorId) {
		AvpSet etsiSipAuthAvp = avps.addGroupedAvp(
				DiameterConstants.AVPCode.AVP_ETSI_SIP_Authenticate, appVendorId, true,
				false);

		if (realm != null)
			etsiSipAuthAvp.addAvp(
					DiameterConstants.AVPCode.AVP_ETSI_Digest_Realm,
					realm.getBytes(), appVendorId, true, false);

		if (nonce != null)
			etsiSipAuthAvp.addAvp(
					DiameterConstants.AVPCode.AVP_ETSI_Digest_Nonce, nonce, appVendorId,
					true, false);

		if (domain != null)
			etsiSipAuthAvp.addAvp(
					DiameterConstants.AVPCode.AVP_ETSI_Digest_Domain,
					domain.getBytes(), appVendorId, true, false);

		if (algorithm != null)
			etsiSipAuthAvp.addAvp(
					DiameterConstants.AVPCode.AVP_ETSI_Digest_Algorithm,
					algorithm.getBytes(), appVendorId, true, false);

		if (auth_ha1 != null)
			etsiSipAuthAvp.addAvp(
					DiameterConstants.AVPCode.AVP_ETSI_Digest_HA1, auth_ha1,
					appVendorId, true, false);

		return etsiSipAuthAvp;
	}

}
