package com.itmill.toolkit.launcher;

import java.util.Map;

import com.google.gwt.dev.GWTShell;

/**
 * Class for starting Google Web Toolkit (GWT) Hosted mode with without embedded
 * tomcat (-noserver). Instead of tomcat we use external servlet container
 * (Jetty).
 * 
 * NOTE: you must edit Eclipse launcher and following parameters there.
 * 
 * For program arguments:
 * 
 * -noserver -out WebContent/ITMILL/widgetsets http://localhost:8080/
 * 
 * 
 * And for VM arguments:
 * 
 * -XstartOnFirstThread -Xms256M -Xmx512M
 * 
 * TODO: how to add *.launch files automatically when importing Eclipse
 * workspace?
 * 
 */
public class ITMillHostedLaunch extends ITMillStandardLaunch {

	private final static String serverPort = "8080";

	/**
	 * Main function for staring GWTShell and running Jetty.
	 * 
	 * Command line Arguments are passed through to Jetty, see runServer method
	 * for options.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Pass-through of arguments for Jetty
		Map serverArgs = parseArguments(args);

		String url = runServer(serverArgs);

		// Start GWTShell

		GWTShell.main(args);

		// Open browser into application URL
		if (url != null) {
			BrowserLauncher.openBrowser(url);
		}

	}

}
