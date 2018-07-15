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

		Server server = new Server(7070);
		ServletContextHandler handler = new ServletContextHandler(server, "/mystore");
		handler.addServlet(HomePageServlet.class, "/");
		try {
			server.start();
		} catch (Exception e) {
			logger.error(e);
		}
		System.out.println("UIEngine Started! " + System.currentTimeMillis());
	}
}
