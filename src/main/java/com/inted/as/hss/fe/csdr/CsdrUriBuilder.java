package com.inted.as.hss.fe.csdr;

import org.apache.olingo.client.api.Configuration;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.uri.URIBuilderImpl;
//import org.apache.olingo.client.core.uri.URIUtils;

public class CsdrUriBuilder extends URIBuilderImpl{

	private Configuration configuration;
	public CsdrUriBuilder(Configuration configuration, String serviceRoot) {
		
		super(configuration, serviceRoot);
		this.configuration = configuration;
		// TODO Auto-generated constructor stub
	}

	@Override
	public URIBuilder appendKeySegment(Object val){
		
		 // final String segValue = URIUtils.escape(val);
		  String segValue = val+""; 
		     segments.add(configuration.isKeyAsSegment()
		          ? new Segment(SegmentType.KEY_AS_SEGMENT, segValue)
		     : new Segment(SegmentType.KEY, "(" + segValue + ")"));
		 
	    return this;
	}
	
	
}
