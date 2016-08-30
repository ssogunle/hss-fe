package com.inted.as.hss.fe;

/**
 * @author Segun Sogunle
 * Dynamically Launches the HSS-FE
 * 
 */
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

public class AppDaemon implements ServletContextListener {
	Thread th = null;

	public void contextInitialized(ServletContextEvent sce) {
		Thread th = new Thread() {
			public void run() {
				
				try {
					new HSS().service(null, null);
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		th.setDaemon(true);
		th.start();

	}

	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		th.stop();
	}

}
