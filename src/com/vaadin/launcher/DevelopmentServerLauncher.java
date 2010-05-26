/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.launcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.vaadin.launcher.util.BrowserLauncher;

/**
 * Class for running Jetty servlet container within Eclipse project.
 * 
 */
public class DevelopmentServerLauncher {

    private final static int serverPort = 8888;

    /**
     * Main function for running Jetty.
     * 
     * Command line Arguments are passed through to Jetty, see runServer method
     * for options.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Pass-through of arguments for Jetty
        final Map<String, String> serverArgs = parseArguments(args);

        // Start Jetty
        System.out.println("Starting Jetty servlet container.");
        final String url = runServer(serverArgs, "Development Server Mode");

        // Start Browser
        if (!serverArgs.containsKey("nogui") && url != null) {
            System.out.println("Starting Web Browser.");

            // Open browser into application URL
            BrowserLauncher.openBrowser(url);
        }

    }

    /**
     * Run the server with specified arguments.
     * 
     * @param serverArgs
     * @return
     * @throws Exception
     * @throws Exception
     */
    protected static String runServer(Map<String, String> serverArgs,
            String mode) throws Exception {

        // Assign default values for some arguments
        assignDefault(serverArgs, "webroot", "WebContent");
        assignDefault(serverArgs, "httpPort", "" + serverPort);
        assignDefault(serverArgs, "context", "");

        int port = serverPort;
        try {
            port = Integer.parseInt(serverArgs.get("httpPort"));
        } catch (NumberFormatException e) {
            // keep default value for port
        }

        // Add help for System.out
        System.out
                .println("-------------------------------------------------\n"
                        + "Starting Vaadin in "
                        + mode
                        + ".\n"
                        + "Running in http://localhost:"
                        + serverPort
                        + "\n-------------------------------------------------\n");

        final Server server = new Server();

        final Connector connector = new SelectChannelConnector();

        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

        final WebAppContext webappcontext = new WebAppContext();
        String path = DevelopmentServerLauncher.class.getPackage().getName()
                .replace(".", File.separator);
        webappcontext.setDefaultsDescriptor(path + File.separator
                + "jetty-webdefault.xml");
        webappcontext.setContextPath(serverArgs.get("context"));
        webappcontext.setWar(serverArgs.get("webroot"));
        server.setHandler(webappcontext);

        try {
            server.start();
        } catch (Exception e) {
            server.stop();
            throw e;
        }

        return "http://localhost:" + port + serverArgs.get("context");
    }

    /**
     * Assign default value for given key.
     * 
     * @param map
     * @param key
     * @param value
     */
    private static void assignDefault(Map<String, String> map, String key,
            String value) {
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
    protected static Map<String, String> parseArguments(String[] args) {
        final Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            final int d = args[i].indexOf("=");
            if (d > 0 && d < args[i].length() && args[i].startsWith("--")) {
                final String name = args[i].substring(2, d);
                final String value = args[i].substring(d + 1);
                map.put(name, value);
            }
        }
        return map;
    }

}
