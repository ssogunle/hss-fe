package com.inted.as.hss.fe.csdr;

import java.net.URI;
import java.util.List;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
//import org.apache.olingo.client.api.communication.request.retrieve.v4.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.uri.QueryOption;
//import org.apache.olingo.client.api.v4.ODataClient;

//import org.apache.olingo.commons.api.domain.v4.ODataEntity;
//import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
//import org.apache.olingo.commons.api.domain.v4.ODataProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.models.SearchKey;
import com.inted.as.hss.fe.utils.ODataUtil;
//import com.thoughtworks.xstream.XStream;

public class CsdrClientProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(CsdrClientProcessor.class);

	private ODataClient client;
	String serviceRoot;

	public CsdrClientProcessor(ODataClient client, String serviceRoot) {
		this.client = client;
		this.serviceRoot = serviceRoot;

	}

	public ClientEntity fetchEntity(String entitySetName, List<SearchKey> keyParams) throws Exception {
		// LOG.info("Fetching Entity..");
		List<ClientEntity> entities = fetchEntities(entitySetName);
		// LOG.info("Entity Size: "+entities.size());
		return ODataUtil.findMatch(entities, keyParams);
	}

	public List<ClientEntity> fetchEntities(String entitySetName) {

		List<ClientEntity> entities = null;

		try {
			URI entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment(entitySetName).build();

			RetrieveRequestFactory rrf = client.getRetrieveRequestFactory();

			ODataEntitySetRequest<ClientEntitySet> request = rrf.getEntitySetRequest(entitySetUri);
			request.setAccept("application/json;odata.metadata=minimal");

			ODataRetrieveResponse<ClientEntitySet> response = request.execute();

			ClientEntitySet retrievedEntitySet = response.getBody();

			entities = retrievedEntitySet.getEntities();

		} catch (Exception ex) {
			LOG.info("Error fetching entities: " + ex);
		}
		return entities;
	}

	public ClientEntity fetchEntity(QueryOption opt, String entitySetName, String query, boolean expandEntity,
			String... expandItems) {

		ClientEntity et = null;
		URI entitySetUri = null;

		if (expandEntity)
			entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment(entitySetName)
					.addQueryOption(opt, query).expand(expandItems).build();
		else
			entitySetUri = client.newURIBuilder(serviceRoot).appendEntitySetSegment(entitySetName)
					.addQueryOption(opt, query).build();

		List<ClientEntity> entities = executeRequest(entitySetUri);

		LOG.info("E4");
		if (entities.size() == 1)
			et = entities.get(0);

		LOG.info("E5");
		return et;
	}

	public List<ClientEntity> fetchNavigProperty(String sourceEntitySetName, String targetEntitySetName, String key) {

		URI entitySetUri = new CsdrUriBuilder(client.getConfiguration(), serviceRoot)
				.appendEntitySetSegment(sourceEntitySetName).appendKeySegment(key)
				.appendNavigationSegment(targetEntitySetName).build();

		List<ClientEntity> entities = executeRequest(entitySetUri);

		return entities;
	}

	public List<ClientEntity> executeRequest(URI entitySetUri) {

		// LOG.info("URI :"+entitySetUri.toString());
		RetrieveRequestFactory rrf = client.getRetrieveRequestFactory();

		/// LOG.info("E1");
		ODataEntitySetRequest<ClientEntitySet> request = rrf.getEntitySetRequest(entitySetUri);
		request.setAccept("application/json;odata.metadata=minimal");

		// LOG.info("E2");
		ODataRetrieveResponse<ClientEntitySet> response = request.execute();

		// LOG.info("E3");
		ClientEntitySet retrievedEntitySet = response.getBody();

		return retrievedEntitySet.getEntities();
	}
}