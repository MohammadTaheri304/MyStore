package io.zino.mystore.uiEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.zino.mystore.ConfigMgr;

public class UIEngine {

	public static UIEngine instance = new UIEngine();
	final static Logger logger = LogManager.getLogger(ConfigMgr.class);
	
	public static UIEngine getInstance() {
		return instance;
	}

	private UIEngine() {
		final int port = Integer.parseInt(ConfigMgr.getInstance().get("UIEngine.port"));
		Server server = new Server(port);
		ServletContextHandler handler = new ServletContextHandler(server, "/mystore");
		handler.addServlet(HomePageServlet.class, "/");
		handler.addServlet(AllKeyPageServlet.class, "/keys");
		try {
			server.start();
		} catch (Exception e) {
			logger.error(e);
		}
		System.out.println("UIEngine Started! " + System.currentTimeMillis());
	}
}
