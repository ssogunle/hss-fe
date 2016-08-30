package com.inted.as.hss.fe.csdr;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.olingo.client.api.ODataClient;
//import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;

import org.apache.olingo.client.api.domain.ClientComplexValue;
//import org.apache.olingo.commons.api.data.Entity;
//import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.client.api.domain.ClientEntity;

import org.apache.olingo.client.api.uri.QueryOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.cx.models.TApplicationServer;
import com.inted.as.hss.fe.cx.models.TIMSSubscription;
import com.inted.as.hss.fe.cx.models.TInitialFilterCriteria;
import com.inted.as.hss.fe.cx.models.TPublicIdentity;
import com.inted.as.hss.fe.cx.models.TSePoTri;
import com.inted.as.hss.fe.cx.models.TServiceProfile;
import com.inted.as.hss.fe.cx.models.TTrigger;
import com.inted.as.hss.fe.models.SearchKey;
import com.inted.as.hss.fe.models.enums.ShDataReferences;
import com.inted.as.hss.fe.sh.models.TShData;
import com.inted.as.hss.fe.sh.models.TShIMSData;

public class CsdrClient {

	private static final Logger LOG = LoggerFactory.getLogger(CsdrClient.class);

	// ODATA EDM Namespace
	private static final String NAMESPACE = "com.inted.csdr";
	// OData Service URL
	private final String serviceRoot = "http://localhost:8080/csdr/svc/";
	// Create OData V4 Client
	private ODataClient client;
	// Interface between CSDR and FE
	private CsdrClientProcessor p;
	// Search Parameters
	List<SearchKey> keyParams;
	// Sh-Data XML Class
	TShData shData;
	// Sh-IMS-Data
	TShIMSData shImsData;

	public CsdrClient() {
		client = ODataClientFactory.getClient();
		p = new CsdrClientProcessor(client, serviceRoot);
	}

	public boolean identitiesExist(String impi, String impu) {

		URI entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment("RegisteredIdentities")
				.filter("Impi eq '" + impi + "' and Impu eq '" + impu + "'").build();
		List<ClientEntity> set = p.executeRequest(entitySetUri);

		if (set.size() == 1)
			return true;

		return false;
	}

	public boolean roamingSupported(String networkId) {
		String query = "Name eq '" + networkId + "'";
		ClientEntity network = p.fetchEntity(QueryOption.FILTER, "VisitedNetworks", query, false);
		return network == null ? false : true;
	}

	public ClientEntity fetchRegisteredSet(String impi, String impu) {

		URI entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment("RegisteredIdentities")
				.filter("Impi eq '" + impi + "' and Impu eq '" + impu + "'").build();
		List<ClientEntity> set = p.executeRequest(entitySetUri);

		if (set.size() == 1)
			return set.get(0);

		return null;
	}
	
	public ClientEntity fetchRegisteredSet(String impu) {

		URI entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment("RegisteredIdentities")
				.filter("Impu eq '" + impu + "'").build();
		List<ClientEntity> set = p.executeRequest(entitySetUri);

		if (set.size() == 1)
			return set.get(0);

		return null;
	}

	public ClientEntity fetchProfile(String impi) {
		URI entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment("SubscriptionProfiles")
				.expand("MergedData").filter("Impi eq '" + impi + "'").build();
		List<ClientEntity> set = p.executeRequest(entitySetUri);

		if (set.size() == 1)
			return set.get(0);

		return null;
	}

	public void updateRegisteredUserInfo(String scscfName, String userState) {

		// Registered Identity :scscfname, userState:AuthPending
		// SubscriptionProfile : scscfName, impuUserState;AuthPending
	}

	public void updateRegisteredUserInfo(String userState) {

		// Registered Identity : userState:AuthPending
		// SubscriptionProfile : impuUserState;AuthPending

	}

	public void updateSqn(String impi, String sqn) {
		// Registered Identity; AuthSqn
		// SubscriptionProfile : impiSqn
	}

	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------
	// -------------------------------------------------------

	public ClientEntity getVisitedNetwork(String vnName) throws Exception {
		keyParams = new ArrayList<SearchKey>();
		// LOG.info("Searching for Visited Networks Name....");
		// Fetch Visited Network
		keyParams.add(new SearchKey("name", vnName));
		ClientEntity et = p.fetchEntity("VisitedNetworks", keyParams);

		if (et == null)
			LOG.info("No visited network with such name: " + vnName);
		return et;
	}

	public ClientEntity getServiceProfile(String serviceProfileName) throws Exception {

		keyParams = new ArrayList<SearchKey>();
		// fetch ServiceProfile
		keyParams.add(new SearchKey("name", serviceProfileName));

		ClientEntity et = p.fetchEntity("ServiceProfiles", keyParams);

		return et;
	}

	public ClientEntity getIfc(String ifcName) throws Exception {

		keyParams = new ArrayList<SearchKey>();
		// fetch IFC
		keyParams.add(new SearchKey("name", ifcName));

		ClientEntity et = p.fetchEntity("IfcSet", keyParams);

		return et;

	}

	public ClientEntity getTriggerPoint(String tpName) throws Exception {

		keyParams = new ArrayList<SearchKey>();
		// fetch TriggerPoint
		keyParams.add(new SearchKey("name", tpName));

		ClientEntity et = p.fetchEntity("TriggerPoints", keyParams);

		return et;

	}

	public ClientEntity getServicePointTrigger(int id) throws Exception {

		keyParams = new ArrayList<SearchKey>();
		// fetch TriggerPoint
		keyParams.add(new SearchKey("Id", id + ""));

		ClientEntity et = p.fetchEntity("ServicePointTriggers", keyParams);

		return et;

	}

	public boolean updateUserStatus(String impu) throws Exception {

		// fetch IMS Subscription
		keyParams.add(new SearchKey("impu", impu));
		ClientEntity et = p.fetchEntity("ImsSubscriptions", keyParams);

		return false;
	}

	public String fetchCxData(ClientEntity imsu) throws Exception {

		String spName = imsu.getProperty("service_profile_name") == null ? null
				: String.valueOf(imsu.getProperty("service_profile_name").getValue());
		/*
		 * Get ServiceProfile
		 */

		ClientEntity spE = getServiceProfile(spName);
		String asName = null;
		Short profilePartInd = -1;
		int sptId = -1;
		Boolean tpCnf = null;

		if (spE != null) {

			String ifcName = spE.getProperty("ifc_name") == null ? null
					: spE.getProperty("ifc_name").getValue().toString();

			ClientEntity ifcE = getIfc(ifcName);

			if (ifcE != null) {

				asName = spE.getProperty("application_server_name") == null ? null
						: spE.getProperty("application_server_name").getValue().toString();

				profilePartInd = spE.getProperty("profile_part_indicator") == null ? 2
						: Short.parseShort(spE.getProperty("profile_part_indicator").getValue().toString());

				String tpName = spE.getProperty("trigger_point_name") == null ? null
						: spE.getProperty("trigger_point_name").getValue().toString();

				ClientEntity tpE = getTriggerPoint(tpName);
				/*
				 * if(tpE!=null){
				 * 
				 * tpCnf = spE.getProperty("condition_type_cnf") == null ? null
				 * : Boolean.parseBoolean(spE
				 * .getProperty("condition_type_cnf").getValue().toString());
				 * 
				 * sptId = spE.getProperty("spt_id") == null ? -1 :
				 * Integer.parseInt(spE
				 * .getProperty("spt_id").getValue().toString());
				 * 
				 * 
				 * }
				 */
			}
		}

		// "conditionNegated", "group", "requestURI", "method", "sipHeader",
		// "sessionCase", "sessionDescription"
		TSePoTri cxSpt = new TSePoTri();
		// cxSpt.getGroup().set(0, 1);
		cxSpt.setConditionNegated(false);
		cxSpt.setMethod("INVITE");
		cxSpt.setRequestURI("*");
		// cxSpt.setSessionCase("");
		// cxSpt.setSessionDescription(value);
		// cxSpt.setSIPHeader(value);

		// "conditionTypeCNF", list:"spt",
		TTrigger cxTp = new TTrigger();
		cxTp.setConditionTypeCNF(false);
		cxTp.getSPT().add(cxSpt);

		// "serverName", "defaultHandling", "serviceInfo",
		TApplicationServer cxAppServer = new TApplicationServer();

		// cxAppServer.setDefaultHandling(value);

		// String scscfName = imsu.getProperty("scscf_name") == null ? null
		// : String.valueOf(imsu.getProperty("scscf_name").getValue());
		cxAppServer.setServerName(asName);

		// cxAppServer.setServiceInfo(value);

		// "priority", "triggerPoint", "applicationServer",
		// "profilePartIndicator",
		TInitialFilterCriteria cxIfc = new TInitialFilterCriteria();
		cxIfc.setPriority(0);
		cxIfc.setApplicationServer(cxAppServer);

		if (profilePartInd != -1)
			cxIfc.setProfilePartIndicator(profilePartInd);

		cxIfc.setTriggerPoint(cxTp);

		// "barringIndication", "identity",
		TPublicIdentity cxImpu = new TPublicIdentity();

		ClientComplexValue impuCv = imsu.getProperty("impu") == null ? null
				: imsu.getProperty("impu").getComplexValue();

		String impuUri = impuCv.get("sip_uri") == null ? null : impuCv.get("sip_uri").getValue().toString();
		Boolean barring = impuCv.get("barring_indication") == null ? false
				: Boolean.parseBoolean(impuCv.get("barring_indication").getValue().toString());

		cxImpu.setIdentity(impuUri);
		cxImpu.setBarringIndication(barring);

		// "publicIdentity", "coreNetworkServicesAuthorization",
		// "initialFilterCriteria",
		TServiceProfile cxServiceProfile = new TServiceProfile();

		cxServiceProfile.getPublicIdentity().add(cxImpu);
		// cxServiceProfile.setCoreNetworkServicesAuthorization(null);
		cxServiceProfile.getInitialFilterCriteria().add(cxIfc);

		// "privateID","serviceProfile",
		TIMSSubscription cxImsu = new TIMSSubscription();

		ClientComplexValue impiCv = imsu.getProperty("impi") == null ? null
				: imsu.getProperty("impi").getComplexValue();

		String impiUri = impiCv.get("identifier") == null ? null : impiCv.get("identifier").getValue().toString();

		cxImsu.setPrivateID(impiUri);
		cxImsu.getServiceProfile().add(cxServiceProfile);

		return xmlToString(cxImsu);
	}

	public String fetchUdrData(List<ShDataReferences> dataReferences, String impu) throws Exception {

		String userData = null;
		shData = new TShData();
		shImsData = new TShIMSData();

		// LOG.info("IMPU: " + impu);
		// Find the Overall XML Schema Java code for user data

		keyParams = new ArrayList<SearchKey>();

		// fetch IMS Subscription
		keyParams.add(new SearchKey("impu", impu));
		ClientEntity et = p.fetchEntity("ImsSubscriptions", keyParams);

		for (ShDataReferences dataRef : dataReferences) {

			if (dataRef == ShDataReferences.DATA_REFERENCE_IMS_USER_STATE) {

				// UserStatus -> impu -> Implicit RegSet -> ImsSubscription

				// Add to the XML Object
				if (et != null) {
					// LOG.info("Found state!");

					String us = null;

					try {
						us = et.getProperty("ims_user_state").getValue().toString();
					} catch (NullPointerException e) {
						LOG.error("IMS User State is Null");
					}

					// LOG.info("IMS User State: " + us);

					// short userState = DiameterUtil.userStateInt(us);

					// shImsData.setIMSUserState(userState);
				}

			}

			if (dataRef == ShDataReferences.DATA_REFERENCE_SCSCF_NAME) {
				// SCSF-Name ->PrefScscfSet-> ImsSubscription

				// Fetch SCSCF Name
				// Add to the XML Object
				if (et != null) {
					LOG.info("Found scscf!");

					String scscfName = null;
					try {
						LOG.info("SCSCF Name: " + et.getProperty("scscf_name").getValue());
						scscfName = et.getProperty("scscf_name").getValue().toString();
					} catch (NullPointerException e) {
						LOG.error("SCSCF Name is Null");
					}

					// LOG.info("SCSCF Name: " + scscfName);
					shImsData.setSCSCFName(scscfName);
				}

			}

			if (dataRef == ShDataReferences.DATA_REFERENCE_IFC) {
				// IFC -> ServiceProfile ->
				// p.fetchEntities("ServiceProfiles"); //The collection that
				// contains IFC
				// fetch Subscription
				// Fetch IFC
				// Add to the schema
			}

		}

		LOG.info("Sh-IMS-Data: <scscf>" + shImsData.getSCSCFName() + "</scscf>, <state>" + shImsData.getIMSUserState()
				+ "</state>");

		shData.setShIMSData(shImsData);

		/*
		 * Convert XML to String Return the String
		 */

		return xmlToString(shData);
	}

	public String xmlToString(TShData shData) {
		String xmlStr = null;
		try {
			JAXBContext context = JAXBContext.newInstance(TShData.class);
			Marshaller m = context.createMarshaller();

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To
																			// format
																			// XML

			StringWriter sw = new StringWriter();
			m.marshal(shData, sw);
			xmlStr = sw.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return xmlStr;
	}

	public String xmlToString(TIMSSubscription cxImsu) {
		String xmlStr = null;
		try {
			JAXBContext context = JAXBContext.newInstance(TIMSSubscription.class);
			Marshaller m = context.createMarshaller();

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To
																			// format
																			// XML

			StringWriter sw = new StringWriter();
			m.marshal(cxImsu, sw);
			xmlStr = sw.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return xmlStr;
	}

}
