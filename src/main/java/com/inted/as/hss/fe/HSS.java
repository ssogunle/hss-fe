package com.inted.as.hss.fe;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.StackType;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inted.as.hss.fe.cx.services.LirService;
import com.inted.as.hss.fe.cx.services.MarService;
import com.inted.as.hss.fe.cx.services.SarService;
import com.inted.as.hss.fe.cx.services.UarService;
import com.inted.as.hss.fe.sh.services.UdrService;
import com.inted.as.hss.fe.utils.DiameterConstants;
import com.inted.as.hss.fe.utils.DiameterUtil;
import com.inted.as.hss.fe.utils.FEUtil;
import com.inted.as.hss.fe.utils.auth.AuthenticationVector;

/**
 * @author Segun Sogunle Servlet implementation class HSS
 */

public class HSS extends HttpServlet implements NetworkReqListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(HSS.class);

	/*
	 * Defining HSS Interface Properties
	 */
	private static final String REALM = "open-ims.test";
	private static final long SH_VENDOR_ID = 10415;
	private static final long CX_VENDOR_ID = 10415;
	private static final long SH_APPLICATION_ID = 16777217;
	private static final long CX_APPLICATION_ID = 16777216;

	private ApplicationId shAuthAppId = ApplicationId.createByAuthAppId(SH_VENDOR_ID, SH_APPLICATION_ID);
	private ApplicationId cxAuthAppId = ApplicationId.createByAuthAppId(CX_VENDOR_ID, CX_APPLICATION_ID);
	
	// Dictionary, for informational purposes.
	private AvpDictionary dictionary = AvpDictionary.INSTANCE;
	
	/*
	 * Stack and session factory
	 */
	private Stack stack;
	private SessionFactory factory;
	// session used as handle for communication
	// private Session session;

	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HSS() {
		super();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		InputStream is = null;
		try {
			/*
			 * Parse dictionary, it is used for user friendly info.
			 */
			dictionary.parseDictionary(this.getClass().getClassLoader().getResourceAsStream("dictionary.xml"));

			this.stack = new StackImpl();

			/*
			 * Parse stack configuration
			 */
			is = this.getClass().getClassLoader().getResourceAsStream("config-server.xml");

			Configuration config = new XMLConfiguration(is);
			factory = stack.init(config);

			/*
			 * Register network req listener, even though we wont receive
			 * requests This has to be done to inform stack that we support
			 * application
			 */
			Network network = stack.unwrap(Network.class);
			network.addNetworkReqListener(this, this.shAuthAppId);
			network.addNetworkReqListener(this, this.cxAuthAppId);

			/*
			 * Print info about application
			 */
			Set<org.jdiameter.api.ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

			LOG.info("Diameter Stack  :: Supporting " + appIds.size() + " applications.");

			for (org.jdiameter.api.ApplicationId x : appIds) {
				LOG.info("Diameter Stack  :: Common :: " + x);
			}
			is.close();

			/*
			 * Verify Stack Driver
			 */
			MetaData metaData = stack.getMetaData();

			if (metaData.getStackType() != StackType.TYPE_SERVER || metaData.getMinorVersion() <= 0) {
				stack.destroy();
				LOG.error("Incorrect driver");
				throw new Exception("Incorrect Driver");
			}

			/*
			 * Launch the Diameter Protocol Stack
			 */
			stack.start();
			LOG.info("Stack initialization successfully completed.");
			FEUtil.displayStartup(stack);

		} catch (Exception e) {
			LOG.error("Error Occured; " + e);
			// e.printStackTrace();
			stack.destroy();
		}
	}

	public Answer processRequest(Request request) {

		Answer answer = null;
		AvpSet answerAvps = null;

		// Display the message received on the console
		// FEUtil.printMessage(request, false)
		try {
			switch (request.getCommandCode()) {

				case DiameterConstants.Command.UDR:
					LOG.info("User-Data-Request Identified");
					UdrService udr = new UdrService(request);
					String data = "";
					data = udr.getData();
					answer = request.createAnswer(DiameterConstants.ResultCode.DIAMETER_SUCCESS.getCode());
					answerAvps = answer.getAvps();
					answerAvps.addAvp(Avp.USER_DATA_SH, data.getBytes(), SH_VENDOR_ID, true, false);
					LOG.info("USER DATA : " + data);

				case DiameterConstants.Command.PUR:
					LOG.info("Profile-Update-Request identified: Not yet implemented");
					break;

				case DiameterConstants.Command.LIR:
					LOG.info("Location-Information-Request Identified");

					LirService lir = new LirService(request);
					answer = lir.getResponse();
					break;

				case DiameterConstants.Command.MAR:
					LOG.info("Multimedia-Auth-Request Identified");

					MarService mar = new MarService(request);
					answer = mar.getResponse();
					break;

				case DiameterConstants.Command.PPR:
					LOG.info("Push-Profile-Request: Not yet implemented");
					break;

				case DiameterConstants.Command.RTR:
					LOG.info("Registration-Termination-Request: Not yet implemented");
					break;

				case DiameterConstants.Command.SAR:
					LOG.info("Server-Assignment-Request Identified");

					SarService sar = new SarService(request);
					answer = sar.getResponse();
					break;

				case DiameterConstants.Command.UAR:
					LOG.info("User-Auth-Request Identified");

					UarService uar = new UarService(request);
					answer = uar.getResponse();
					break;

				default:
					LOG.error("Unsupported command code: " + request.getCommandCode());
					answer = request.createAnswer(5004);
					break;

				}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return answer;
	}
}