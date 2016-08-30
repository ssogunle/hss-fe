package com.inted.as.hss.fe.utils;

import java.util.ArrayList;
import java.util.Properties;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Peer;
import org.jdiameter.api.Stack;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.HSS;


public class FEUtil {

	private static final Logger log = LoggerFactory.getLogger(HSS.class);
	
	public static void displayStartup(Stack stack){
		
		 Properties sysProps = System.getProperties();

	      String osLine = sysProps.getProperty("os.name") + "/" + sysProps.getProperty("os.arch");
	      String javaLine = sysProps.getProperty("java.vm.vendor") + " " + sysProps.getProperty("java.vm.name") 
	      + " " + sysProps.getProperty("java.vm.version");
	      
	      Peer localPeer = stack.getMetaData().getLocalPeer();

	      //String diameterLine = localPeer.getProductName() + " (" +  localPeer.getUri() + " @ " + localPeer.getRealmName() + ")";
	      String diameterLine =  localPeer.getUri() + " @ " + localPeer.getRealmName();
	      
	      String ODataLine= "OASIS OData Version 4.0.0";
	      
	      log.info("-------------------------------------------------------------------------");
	      log.info("-------------------------------------------------------------------------");
	      log.info("\t\t\t Inted HSS Front End Service                ");
	      log.info(" 			                                               ");
	      log.info(" System : "+osLine+"                                       ");
	      log.info(" 	                                                       ");
	      log.info(" Java : "+javaLine+"                                       ");
	      log.info(" 			                                               ");
	      log.info(" Diameter Local Peer: "+diameterLine+"                     ");
	      log.info(" 			                                               ");
	      log.info(" OData : "+ODataLine+"                                     ");
	      log.info(" 			                                               ");
	      log.info("-------------------------------------------------------------------------");
	      log.info("-------------------------------------------------------------------------");
	      
	      
	}
	
	
	public static void printMessage(Message message, boolean sending) {

		log.info((sending?"Sending ":"Received ") 
				+ (message.isRequest() ? "Request: " : "Answer: ") + message.getCommandCode()
				+ "\nEnd-to-End Identifier:" 	+ message.getEndToEndIdentifier() 
				+ "\nHop-by-Hop Identifier:" + message.getHopByHopIdentifier() 
				+ "\nApplication ID:" + message.getApplicationId());
		
		log.info("AVPS["+message.getAvps().size()+"]: \n");
		
		try {
			printAvps(message.getAvps());
		} catch (AvpDataException e) {
			log.error("Error occured while trying to print Avps \n"+e);
		}
	
	}

private static void printAvps(AvpSet avpSet) throws AvpDataException {
	printAvpsAux(avpSet, 0);
}


private static void printAvpsAux(AvpSet avpSet, int level) throws AvpDataException {
	String prefix = "                      ".substring(0, level * 2);

	for (Avp avp : avpSet) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avp.getCode(), avp.getVendorId());

		if (avpRep != null && avpRep.getType().equals("Grouped")) {
			log.info(prefix + "<avp name=\"" + avpRep.getName() 
			+ "\" code=\"" + avp.getCode() 
			+ "\" vendor=\"" + avp.getVendorId() + "\">");
			
			printAvpsAux(avp.getGrouped(), level + 1);
			
			log.info(prefix + "</avp>");
			
		} else if (avpRep != null) {
		
			String value = "";

			if (avpRep.getType().equals("Integer32"))
				value = String.valueOf(avp.getInteger32());
			else if (avpRep.getType().equals("Integer64") || avpRep.getType().equals("Unsigned64"))
				value = String.valueOf(avp.getInteger64());
			else if (avpRep.getType().equals("Unsigned32"))
				value = String.valueOf(avp.getUnsigned32());
			else if (avpRep.getType().equals("Float32"))
				value = String.valueOf(avp.getFloat32());
			else
				value = avp.getUTF8String();

			log.info(prefix + "<avp name=\"" + avpRep.getName() 
			+ "\" code=\"" + avp.getCode() 
			+ "\" vendor=\"" + avp.getVendorId()
			+ "\" value=\"" + value + "\" />");
		}
	}
}
}
