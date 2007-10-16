package com.itmill.toolkit.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import winstone.Launcher;
import winstone.Logger;

/**
 * Class for running Winstone servlet container within Eclipse project.
 * 
 */
public class ITMillToolkitServer {

	private final static String serverPort = "8888";

	/**
	 * Main function for running Winstone Launcher.
	 * 
	 * Any command line Arguments are passed through to Winstone.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Pass-through of arguments to winstone launcher
		Map serverArgs = parseArguments(args);

		String url = runServer(serverArgs);

		// Open browser into application URL
		if (url != null) {
			BrowserControl.openBrowser(url);
		}

	}

	/**
	 * Run the server with specified arguments.
	 * 
	 * @param serverArgs
	 * @return
	 */
	protected static String runServer(Map serverArgs) {

		// Add help for System.out
		System.out
				.println("-------------------------------------------------\n"
						+ "Starting IT Mill Toolkit examples.\n"
						+ "Please go to http://localhost:"
						+ serverPort
						+ "\nif your web browser is not automatically started."
						+ "\n-------------------------------------------------\n");

		// Assign default values for some arguments
		assignDefault(serverArgs, "webroot", "WebContent");
		assignDefault(serverArgs, "httpPort", serverPort);
		assignDefault(serverArgs, "ajp13Port", "-1");
		assignDefault(serverArgs, "controlPort", "-1");
		assignDefault(serverArgs, "httpListenAddress", "127.0.0.1");

		try {
			Launcher.initLogger(serverArgs);
			new Launcher(serverArgs);
			Logger.setCurrentDebugLevel(Logger.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "http://localhost:" + serverArgs.get("httpPort");
	}

	/**
	 * Assign default value for given key.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 */
	private static void assignDefault(Map map, String key, String value) {
		if (!map.containsKey(key)) {
			map.put(key, value);
		}
	}

	/**
	 * Parse all command line arguments into a map.
	 * 
	 * Arguments format "key=value" are put into map.
	 * 
	 * @param args
	 * @return map of arguments key value pairs.
	 */
	protected static Map parseArguments(String[] args) {
		Map map = new HashMap();
		for (int i = 0; i < args.length; i++) {
			int d = args[i].indexOf("=");
			if (d > 0 && d < args[i].length() && args[i].startsWith("--")) {
				String name = args[i].substring(2, d);
				String value = args[i].substring(d + 1);
				map.put(name, value);
			}
		}
		return map;
	}

}
