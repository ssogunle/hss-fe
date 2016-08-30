package com.inted.as.hss.fe.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.models.SearchKey;

/**
 * 
 * @author Segun Sogunle
 *
 */
public class ODataUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ODataUtil.class);

	public static ClientEntity findMatch(List<ClientEntity> entities,
			List<SearchKey> keyParams) throws Exception {

		for (ClientEntity entity : entities) {
			boolean foundEntity = entityMatchesAllKeys(entity, keyParams);
			if (foundEntity) {
				return entity;
			}
		}

		return null;
	}

	public static boolean entityMatchesAllKeys(ClientEntity entity,
			List<SearchKey> keyParams) {

		
		
		for (SearchKey key : keyParams) {

			// IMPU-KEY
			if (key.getName().equals("impu")) {
			//	LOG.info("Searching for IMPU");
				boolean isMatch = 	matchesImpu(entity,key);
				
					if(!isMatch)
						return false;
			}
			// IMPI-KEY
			else if (key.getName().equals("impi")) {
			//	LOG.info("Searching for -  IMPI!");

				try {
						ClientComplexValue cv = entity.getProperty("ImsPrivateIdentity").getValue().asComplex();
						String impi = cv.get("Uri").getValue().toString();
	
						//LOG.info("Subscriber IMPI: " + impi);
						//LOG.info("KEY-IMPI: " + key.getValue());
						boolean matches = impi.equals(key.getValue());
	
					//	LOG.info("Matches[2]: "+matches);
			
						if (!matches)
							return false;
						
				} catch (NullPointerException e) {
					return false;
				}
			}
			
			else {
				//LOG.info("Searching Parameter: " + key.getName() + "!");
				String propValue = entity.getProperty(key.getName()).getValue()
						.toString();
				boolean matches = (propValue.equals(key.getValue()));

				if (!matches)
					return false;
			}

		}
		return true;
	}
	
	public static boolean matchesImpu(ClientEntity entity, SearchKey key){
		//ClientComplexValue cv = null;
		boolean matches = false;
		
		try {
	
			ClientCollectionValue ccv = entity.getProperty("ImsPublicIdentities").getCollectionValue();
				
			//LOG.info("Set Size: "+ccv.size());
			
			Iterator<ClientComplexValue> it =ccv.iterator();
			
					while(it.hasNext()){
							
							ClientComplexValue cv = it.next();
						
							String impu = String.valueOf(cv.get("SipUri").getValue());
						//	LOG.info("Subscriber IMPU: " + impu);
						//	LOG.info("KEY-IMPU: " + key.getValue());
							matches = impu.equals(key.getValue());
							//LOG.info("Matches: "+matches);
							
							if(matches)
								return true;
					}
		
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public static ClientComplexValue getImpu(String publicId, ClientEntity subsProfile){
		
		//LOG.info("Public ID: "+publicId);
		
		ClientCollectionValue ccv = subsProfile.getProperty("ImsPublicIdentities").getCollectionValue();
		
		Iterator<ClientComplexValue> it =ccv.iterator();
		
		while(it.hasNext()){
			
			ClientComplexValue cv = it.next();
		 String sipUri = cv.get("SipUri").getPrimitiveValue().toString();
		// LOG.info("Public ID[2]: "+sipUri);
		 if(sipUri.equals(publicId))
			 return cv;
		}
		
		return null;
	}
}
