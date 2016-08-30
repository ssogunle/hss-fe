package com.inted.as.hss.fe.cx.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.csdr.CsdrClient;
import com.inted.as.hss.fe.utils.CxConstants;
import com.inted.as.hss.fe.utils.DiameterConstants;
import com.inted.as.hss.fe.utils.DiameterUtil;

import com.inted.as.hss.fe.utils.auth.AuthenticationVector;
import com.inted.as.hss.fe.utils.auth.DigestAKA;
import com.inted.as.hss.fe.utils.auth.HexCodec;
import com.inted.as.hss.fe.utils.auth.MD5Util;
import com.inted.as.hss.fe.utils.auth.Milenage;

public class MarService {

	private static final Logger LOG = LoggerFactory.getLogger(MarService.class);

	private Request r;
	private CsdrClient client;
	private final long vendorId = 10415; // CX VENDOR ID

	// Authentication & Authorization Elements
	private byte[] authorization;
	private int authScheme;

	// HSS-PROPERTY
	private int IND_LEN = 5;
	private boolean USE_AK = false;
	private int delta = 268435456;
	private int L = 32;

	public MarService(Request r) {
		this.r = r;
		client = new CsdrClient();
	}

	public Answer getResponse() throws Exception {

		Answer response = null;
		AvpSet responseAvps = null;

		// 1. Fetch Mandatory AVPs
		String privateId = DiameterUtil.getUserName(r);
		String publicId = DiameterUtil.getPublicIdentity(r);
		Avp authDataItem = DiameterUtil.getSipAuthDataItem(r);
		String serverName = DiameterUtil.getServerName(r);

		int count = 0;
		if (publicId == null) {
			LOG.warn("Missing Avp: Public-Identity");
			count++;
		}

		if (privateId == null) {
			LOG.warn("Missing Avp: UserName");
			count++;
		}

		if (authDataItem == null) {
			LOG.warn("Missing Avp: Auth-Data-Item");
			count++;
		}

		if (serverName == null) {
			LOG.warn("Missing Avp: Server-Name");
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

		// 4. Get AuthScheme AND/OR Authorization values
		fetchAuthData(authDataItem);

		// 5. Check authScheme
		if (authScheme == -1)
			throw new Exception("DIAMETER MISSING AVP(" + DiameterConstants.ResultCode.DIAMETER_MISSING_AVP.getCode()
					+ ") : IMS SIP AUTHENTICATION SCHEME");

		// 6. Check if AuthScheme is supported
		int impiAuthScheme = regData.getProperty("AuthScheme") == null ? -1
				: getAuthSchemeInt(regData.getProperty("AuthScheme").getValue().toString());

		if (authScheme != impiAuthScheme)
			throw new Exception("IMS DIAMETER ERROR AUTH SCHEME NOT SUPPORTED ("
					+ DiameterConstants.ExperimentalResultCode.RC_IMS_DIAMETER_ERROR_AUTH_SCHEME_NOT_SUPPORTED.getCode()
					+ ")");

		// 7. Fetch SCSCF Name
		String scscfName = String.valueOf(regData.getProperty("ScscfId"));

		// 8. Fetch Authentication Data
		String SK = String.valueOf(regData.getProperty("AuthKey").getValue());
		String OP = String.valueOf(regData.getProperty("AuthOp").getValue());
		String AMF = String.valueOf(regData.getProperty("AuthAmf").getValue());
		String SQN = String.valueOf(regData.getProperty("AuthSqn").getValue());

		if (SK == null)
			throw new Exception("No Secret Key found");
		byte[] secretKey = HexCodec.getBytes(SK.getBytes(), CxConstants.Auth_Parm_Secret_Key_Size);

		OP = (OP == null) ? "00000000000000000000000000000000" : OP;
		byte[] opC = Milenage.generateOpC(secretKey, OP.getBytes());

		AMF = (AMF == null) ? "0000" : AMF;
		byte[] amf = AMF.getBytes();

		SQN = (SQN == null) ? "000000000000" : SQN;

		// 9. Check Authorization
		if (authorization != null) {

			if (scscfName.equals(serverName)) {

				AuthenticationVector av = null;

				if ((av = synchronize(secretKey, opC, amf, SQN)) == null)
					throw new Exception("DIAMETER UNABLE TO COMPLY ("
							+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");

				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());

				responseAvps = response.getAvps();
				responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
				responseAvps.addAvp(Avp.PUBLIC_IDENTITY, publicId.getBytes(), vendorId, true, false);
				// The number of auth items (is 1 for synch)
				responseAvps.addAvp(Avp.SIP_NUMBER_AUTH_ITEMS, 1, vendorId, true, false);

				List avList = new LinkedList();
				avList.add(av);
				DiameterUtil.addAuthVectors(avList, responseAvps, vendorId);

				return response;
			}
		}

		// 10. Check Registration Status

		// 10.1 Fetch User registration State
		String state = regData.getProperty("UserState") == null ? "NotRegistered"
				: String.valueOf(regData.getProperty("UserState").getValue());

		int userState = -1;

		if (state.equals("NotRegistered"))
			userState = 0;
		else if (state.equals("Registered"))
			userState = 1;
		else if (state.equals("UnRegistered"))
			userState = 2;
		else if (state.equals("AuthenticationPending"))
			userState = 3;

		// 10.2 Get number of SIP AuthVector Items
		int avCount = DiameterUtil.getSipNumAuthItems(r);
		if (avCount == -1)
			throw new Exception("DIAMETER MISSING AVP (" + DiameterConstants.ResultCode.DIAMETER_MISSING_AVP.getCode()
					+ "): SIP-Num-Auth-Items");

		// 10.3 Get Origin Host
		String originHost = DiameterUtil.getOriginHost(r);
		if (originHost == null)
			throw new Exception("DIAMETER MISSING AVP (" + DiameterConstants.ResultCode.DIAMETER_MISSING_AVP.getCode()
					+ "): Originating-Host");

		// 10.4 Get Destination Realm
		String destRealm = DiameterUtil.getDestinationRealm(r);

		// 10.5 Get Request URI
		String requestUri = DiameterUtil.getRequestUri(r);
		if (requestUri == null || requestUri.length() == 0)
			requestUri = destRealm;

		// 10.6 Get Request Method
		String method = DiameterUtil.getRequestMethod(r);
		if (method == null || method.length() == 0)
			method = "REGISTER";

		// 10.7 Get Line Identifier and IP address: waste of space
		String lineIdentifier = String.valueOf(regData.getProperty("AuthLineId").getValue());
		String ipAddress = String.valueOf(regData.getProperty("AuthIpAddress").getValue());

		switch (userState) {

		case CxConstants.IMPU_user_state_Registered:

			if (scscfName == null) {
				LOG.error("User is registered but has no S-CSCF name stored - HSS database consistency error");
				throw new Exception(
						"DIAMETER UNABLE TO COMPLY (" + DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY + ")");
			}

			if (!scscfName.equals(serverName)) {

				// client.updateRegisteredUserInfo(serverName,true); TBC

				List avList = generateAuthVectors(authScheme, avCount, privateId, SK, OP, AMF, SQN, lineIdentifier,
						ipAddress, destRealm, requestUri, method);

				if (avList != null) {
					response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
					responseAvps = response.getAvps();

					// Add UserName and PublicId
					responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
					responseAvps.addAvp(Avp.PUBLIC_IDENTITY, publicId.getBytes(), vendorId, true, false);

					// Add AuthVector List
					DiameterUtil.addAuthVectors(avList, responseAvps, vendorId);

					// Add SIPNumberAuthItems
					responseAvps.addAvp(Avp.SIP_NUMBER_AUTH_ITEMS, avList.size(), vendorId, true, false);
				} else {
					LOG.error("DIAMETER UNABLE TO COMPLY ("
							+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");
					response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode());
				}

			} else {
				List avList = generateAuthVectors(authScheme, avCount, privateId, SK, OP, AMF, SQN, lineIdentifier,
						ipAddress, destRealm, requestUri, method);

				if (avList != null) {
					response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
					responseAvps = response.getAvps();

					// Add UserName and PublicId
					responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
					responseAvps.addAvp(Avp.PUBLIC_IDENTITY, publicId.getBytes(), vendorId, true, false);

					// Add AuthVector List
					DiameterUtil.addAuthVectors(avList, responseAvps, vendorId);

					// Add SIPNumberAuthItems
					responseAvps.addAvp(Avp.SIP_NUMBER_AUTH_ITEMS, avCount, vendorId, true, false);

				} else {
					LOG.error("DIAMETER UNABLE TO COMPLY ("
							+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");
					response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode());
				}
			}
			break;

		case CxConstants.IMPU_user_state_Unregistered:
		case CxConstants.IMPU_user_state_Not_Registered:
		case CxConstants.IMPU_user_state_Auth_Pending:

			// if (userState == CxConstants.IMPU_user_state_Unregistered) {
			// TBC: Reset User State
			// }

			if (scscfName == null || scscfName.equals("") || !scscfName.equals(serverName)) {
				// clientupdateRegisteredUserInfo(String serverName)
			}
			List avList = generateAuthVectors(authScheme, avCount, privateId, SK, OP, AMF, SQN, lineIdentifier,
					ipAddress, destRealm, requestUri, method);

			if (avList != null) {
				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
				responseAvps = response.getAvps();

				// Add UserName and PublicId
				responseAvps.addAvp(Avp.USER_NAME, privateId.getBytes(), vendorId, true, false);
				responseAvps.addAvp(Avp.PUBLIC_IDENTITY, publicId.getBytes(), vendorId, true, false);

				// Add AuthVector List
				DiameterUtil.addAuthVectors(avList, responseAvps, vendorId);

				// Add SIPNumberAuthItems
				responseAvps.addAvp(Avp.SIP_NUMBER_AUTH_ITEMS, avCount, vendorId, true, false);
			} else {
				LOG.error("DIAMETER UNABLE TO COMPLY ("
						+ DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode() + ")");
				response = r.createAnswer(DiameterConstants.ResultCode.DIAMETER_UNABLE_TO_COMPLY.getCode());
			}
			break;

		}
		return response;
	}

	private AuthenticationVector generateAuthVector(int authScheme, String impiUri, String key, String op, String amf,
			String SQN, String lineIdentifier, String ipAddress, String realm, String uri, String method) {

		byte[] secretKey;

		switch (authScheme) {

		case CxConstants.Auth_Scheme_MD5:
			LOG.debug("Auth-Scheme is Digest-MD5");

			SecureRandom randomAccess;
			try {
				randomAccess = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}

			byte[] randBytes = new byte[16];
			randomAccess.setSeed(System.currentTimeMillis());
			randomAccess.nextBytes(randBytes);

			AuthenticationVector av = new AuthenticationVector(authScheme, randBytes, key.getBytes());

			return av;

		case CxConstants.Auth_Scheme_AKAv1:
		case CxConstants.Auth_Scheme_AKAv2:
			// Authentication Scheme is AKAv1 or AKAv2
			LOG.debug("Auth-Scheme is Digest-AKA");

			secretKey = HexCodec.getBytes(key.getBytes(), CxConstants.Auth_Parm_Secret_Key_Size);

			// Generate opC
			byte[] opC;
			try {
				opC = Milenage.generateOpC(secretKey, op.getBytes());
			} catch (InvalidKeyException e1) {
				e1.printStackTrace();
				return null;
			}

			byte[] sqn;
			try {
				sqn = HexCodec.decode(SQN);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			sqn = DigestAKA.getNextSQN(sqn, this.IND_LEN);
			byte[] copySqnHe = new byte[6];
			int k = 0;
			for (int i = 0; i < 6; i++, k++) {
				copySqnHe[k] = sqn[i];
			}

			av = DigestAKA.getAuthenticationVector(authScheme, secretKey, opC, amf.getBytes(), copySqnHe);

			if (av != null) {
				// TBC: client.updateSqn(String impi,HexCodec.encode(sqn))
			}
			return av;

		case CxConstants.Auth_Scheme_Digest:
			// Authentication Scheme is Digest
			LOG.debug("Auth-Scheme is Digest");

			secretKey = key.getBytes();
			byte[] ha1 = null;

			if (realm == null)
				realm = impiUri.substring(impiUri.indexOf("@") + 1);
			ha1 = MD5Util.av_HA1(impiUri.getBytes(), realm.getBytes(), secretKey);

			byte[] ha1_hexa = HexCodec.encode(ha1).getBytes();

			av = new AuthenticationVector(authScheme, realm.getBytes(), ha1_hexa);
			return av;

		case CxConstants.Auth_Scheme_SIP_Digest:
			// Authentication Scheme is SIP Digest
			LOG.debug("Auth-Scheme is SIP Digest");

			secretKey = key.getBytes();

			if (realm == null)
				realm = impiUri.substring(impiUri.indexOf("@") + 1);

			ha1 = MD5Util.av_HA1(impiUri.getBytes(), realm.getBytes(), secretKey);

			ha1_hexa = HexCodec.encode(ha1).getBytes();
			av = new AuthenticationVector(authScheme, realm.getBytes(), ha1_hexa);

			return av;

		case CxConstants.Auth_Scheme_HTTP_Digest_MD5:
			// Authentication Scheme is HTTP_Digest_MD5
			LOG.debug("Auth-Scheme is HTTP_Digest_MD5");

			ha1 = null;
			secretKey = key.getBytes();

			if (realm == null)
				realm = impiUri.substring(impiUri.indexOf("@") + 1);

			ha1 = MD5Util.av_HA1(impiUri.getBytes(), realm.getBytes(), secretKey);

			try {
				randomAccess = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}

			byte[] authenticate = new byte[16];
			randomAccess.setSeed(System.currentTimeMillis());
			randomAccess.nextBytes(authenticate);
			byte[] ha1_hex = HexCodec.encode(ha1).getBytes();
			byte[] authenticate_hex = HexCodec.encode(authenticate).getBytes();
			byte[] result = MD5Util.calcResponse(ha1_hex, authenticate,
					method.getBytes(), /* from request */
					uri.getBytes()); /* from request */

			av = new AuthenticationVector(authScheme, uri, authenticate_hex, ha1_hex, result);

			return av;

		case CxConstants.Auth_Scheme_NASS_Bundled:
			// Authentication Scheme is NASS_Bundled
			LOG.debug("Auth-Scheme is NASS_BUNDLED");
			av = new AuthenticationVector(authScheme, lineIdentifier);
			return av;

		case CxConstants.Auth_Scheme_Early:
			LOG.debug("Auth-Scheme is Early IMS");
			av = new AuthenticationVector(authScheme, ipAddress);
			return av;
		}

		return null;
	}

	private List generateAuthVectors(int authScheme, int avCnt, String impiUri, String key, String op, String amf,
			String SQN, String lineIdentifier, String ipAddress, String realm, String uri, String method) {

		List avList = null;
		AuthenticationVector av = null;

		for (long ix = 0; ix < avCnt; ix++) {
			av = generateAuthVector(authScheme, impiUri, key, op, amf, SQN, lineIdentifier, ipAddress, realm, uri,
					method);
			if (av == null)
				break;
			if (avList == null)
				avList = new LinkedList();
			avList.add(av);
		}
		return avList;
	}

	private AuthenticationVector synchronize(byte[] secretKey, byte[] opC, byte[] amf, String sqn)
			throws InvalidKeyException {

		try {
			// LOG.info("HH1");
			// sqnHE - represent the SQN from the HSS
			// sqnMS - represent the SQN from the client side
			byte[] sqnHe = null;

			try {
				sqnHe = HexCodec.decode(sqn);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			// LOG.info("HH2");
			sqnHe = DigestAKA.getNextSQN(sqnHe, IND_LEN);

			byte[] auts = new byte[14];
			int k = 0;
			for (int i = authorization.length - auts.length; i < authorization.length; i++, k++) {
				auts[k] = authorization[i];
			}
			// LOG.info("HH3");
			byte[] rand = new byte[16];
			k = 0;
			for (int i = 0; i < 16; i++, k++) {
				rand[k] = authorization[i];
			}
			// LOG.info("HH4");
			byte[] ak = null;
			if (this.USE_AK) {
				ak = Milenage.f5star(secretKey, rand, opC);
			}

			byte[] sqnMs = new byte[6];
			k = 0;
			if (this.USE_AK) {
				for (int i = 0; i < 6; i++, k++) {
					sqnMs[k] = (byte) (auts[i] ^ ak[i]);
				}
				LOG.warn("USE_AK is enabled and will be used in Milenage algorithm!");
			} else {
				for (int i = 0; i < 6; i++, k++) {
					sqnMs[k] = auts[i];
				}
				LOG.warn("USE_AK is NOT enabled and will NOT be used in Milenage algorithm!");
			}
			// LOG.info("HH5");
			if (DigestAKA.SQNinRange(sqnMs, sqnHe, this.IND_LEN, this.delta, this.L)) {
				LOG.info("The new generated SQN value shall be accepted on the client, abort synchronization!");
				k = 0;
				byte[] copySqnHe = new byte[6];
				for (int i = 0; i < 6; i++, k++) {
					copySqnHe[k] = sqnHe[i];
				}

				AuthenticationVector aVector = DigestAKA.getAuthenticationVector(authScheme, secretKey, opC, amf,
						copySqnHe);

				return aVector;
			}
			// LOG.info("HH6");
			// Perform sync
			byte xmac_s[] = Milenage.f1star(secretKey, rand, opC, sqnMs, amf);
			byte mac_s[] = new byte[8];
			k = 0;
			for (int i = 6; i < 14; i++, k++) {
				mac_s[k] = auts[i];
			}

			for (int i = 0; i < 8; i++) {
				if (xmac_s[i] != mac_s[i]) {
					LOG.error("XMAC and MAC are different! User not authorized in performing synchronization!");
					return null;
				}
			}
			// LOG.info("HH7");
			sqnHe = sqnMs;
			sqnHe = DigestAKA.getNextSQN(sqnHe, this.IND_LEN);
			LOG.info("Synchronization of SQN_HE with SQN_MS was completed successfully!");

			byte[] copySqnHe = new byte[6];
			k = 0;
			for (int i = 0; i < 6; i++, k++) {
				copySqnHe[k] = sqnHe[i];
			}
			AuthenticationVector aVector = DigestAKA.getAuthenticationVector(authScheme, secretKey, opC, amf,
					copySqnHe);

			// TBC: client.updateSqn(String impi,HexCodec.encode(sqnHe))

			return aVector;

		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieve Authentication Scheme and Authorization Data
	 * 
	 * @throws AvpDataException
	 */

	private void fetchAuthData(Avp authDataItem) throws AvpDataException {

		AvpSet avpSet = authDataItem.getGrouped();
		Avp[] avpList = avpSet.asArray();

		for (Avp avp : avpList) {

			if (avp.getCode() == DiameterConstants.AVPCode.IMS_SIP_AUTHENTICATION_SCHEME) {

				String data = new String(avp.getUTF8String());

				// LOG.info("Auth Scheme: " + data);
				switch (data) {

				case CxConstants.Auth_Scheme_AKAv1_Name:
					// LOG.info("Auth Scheme: 1");
					authScheme = CxConstants.Auth_Scheme_AKAv1;
					break;

				case CxConstants.Auth_Scheme_AKAv2_Name:
					// LOG.info("Auth Scheme: 2");
					authScheme = CxConstants.Auth_Scheme_AKAv2;
					break;

				case CxConstants.Auth_Scheme_MD5_Name:
					// LOG.info("Auth Scheme: 4");
					authScheme = CxConstants.Auth_Scheme_MD5;
					break;

				case CxConstants.Auth_Scheme_Digest_Name:
					// LOG.info("Auth Scheme: 8");
					authScheme = CxConstants.Auth_Scheme_Digest;
					break;

				case CxConstants.Auth_Scheme_HTTP_Digest_MD5_Name:
					// LOG.info("Auth Scheme: 16");
					authScheme = CxConstants.Auth_Scheme_HTTP_Digest_MD5;
					break;

				case CxConstants.Auth_Scheme_Early_Name:
					// LOG.info("Auth Scheme: 32");
					authScheme = CxConstants.Auth_Scheme_Early;
					break;

				case CxConstants.Auth_Scheme_NASS_Bundled_Name:
					// LOG.info("Auth Scheme: 64");
					authScheme = CxConstants.Auth_Scheme_NASS_Bundled;
					break;

				case CxConstants.Auth_Scheme_SIP_Digest_Name:
					// LOG.info("Auth Scheme: 128");
					authScheme = CxConstants.Auth_Scheme_SIP_Digest;
					break;

				case CxConstants.Auth_Scheme_Unknown_Name:
				case CxConstants.Auth_Scheme_Unknown_Name_2:
					// authScheme = impi.getDefault_auth_scheme())
				}

			} else if (avp.getCode() == DiameterConstants.AVPCode.IMS_SIP_AUTHORIZATION) {
				authorization = avp.getOctetString();
			}
		}

	}

	private int getAuthSchemeInt(String authScheme) {

		if (authScheme.equals("AKAv1"))
			return 1;
		else if (authScheme.equals("AKAv2"))
			return 2;
		else if (authScheme.equals("Auth_Scheme_MD5"))
			return 4;
		else if (authScheme.equals("Digest"))
			return 8;
		else if (authScheme.equals("HTTP_Digest_MD5"))
			return 16;
		else if (authScheme.equals("Early"))
			return 32;
		else if (authScheme.equals("NASS_Bundled"))
			return 64;
		else if (authScheme.equals("SIP_Digest"))
			return 128;

		return 0;
	}
}
