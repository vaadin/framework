/* 
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.vaadin.launcher.util.BrowserLauncher;

/**
 * Class for running Jetty servlet container within Eclipse project.
 * 
 */
public class DevelopmentServerLauncher {

    private static final String KEYSTORE = "uitest/src/com/vaadin/launcher/keystore";
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
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("org.eclipse.jetty.util.log.class",
                JavaUtilLog.class.getName());

        assertAssertionsEnabled();

        //
        // Pass-through of arguments for Jetty
        final Map<String, String> serverArgs = parseArguments(args);
        if (!serverArgs.containsKey("shutdownPort")) {
            serverArgs.put("shutdownPort", "8889");
        }

        int port = Integer.parseInt(serverArgs.get("shutdownPort"));
        if (port > 0) {
            try {
                // Try to notify another instance that it's time to close
                Socket socket = new Socket((String) null, port);
                // Wait until the other instance says it has closed
                socket.getInputStream().read();
                // Then tidy up
                socket.close();
            } catch (IOException e) {
                // Ignore if port is not open
            }
        }

        // Start Jetty
        System.out.println("Starting Jetty servlet container.");
        String url;
        try {
            url = runServer(serverArgs, "Development Server Mode");
            // Start Browser
            if (serverArgs.containsKey("gui") && url != null) {
                System.out.println("Starting Web Browser.");

                // Open browser into application URL
                BrowserLauncher.openBrowser(url);
            }
        } catch (Exception e) {
            // NOP exception already on console by jetty
        }
    }

    private static void assertAssertionsEnabled() {
        try {
            assert false;

            System.err.println("You should run "
                    + DevelopmentServerLauncher.class.getSimpleName()
                    + " with assertions enabled. Add -ea as a VM argument.");
        } catch (AssertionError e) {
            // All is fine
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
        assignDefault(serverArgs, "slowdown", "/run/APP/PUBLISHED/js_label.js");

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
                        + port
                        + "\n-------------------------------------------------\n");

        final Server server = new Server();

        final Connector connector = new SelectChannelConnector();

        connector.setPort(port);
        if (serverArgs.containsKey("withssl")) {
            final SslSocketConnector sslConnector = new SslSocketConnector();
            sslConnector.setPort(8444);
            SslContextFactory sslFact = sslConnector.getSslContextFactory();
            sslFact.setTrustStore(KEYSTORE);
            sslFact.setTrustStorePassword("password");
            sslFact.setKeyStorePath(KEYSTORE);
            sslFact.setKeyManagerPassword("password");
            sslFact.setKeyStorePassword("password");
            server.setConnectors(new Connector[] { connector, sslConnector });
        } else {
            server.setConnectors(new Connector[] { connector });
        }

        final WebAppContext webappcontext = new WebAppContext();
        webappcontext.setContextPath(serverArgs.get("context"));
        webappcontext.setWar(serverArgs.get("webroot"));
        server.setHandler(webappcontext);

        // --slowdown=/run/APP/PUBLISHED/*,/other/path/asd.jpg
        // slows down specified paths
        if (serverArgs.containsKey("slowdown")) {
            String[] paths = serverArgs.get("slowdown").split(",");
            for (String p : paths) {
                System.out.println("Slowing down: " + p);
                webappcontext.addFilter(SlowFilter.class, p,
                        EnumSet.of(DispatcherType.REQUEST));
            }
        }
        // --cache=/run/APP/PUBLISHED/*,/other/path/asd.jpg
        // caches specified paths
        if (serverArgs.containsKey("cache")) {
            String[] paths = serverArgs.get("cache").split(",");
            for (String p : paths) {
                System.out.println("Enabling cache for: " + p);
                webappcontext.addFilter(CacheFilter.class, p,
                        EnumSet.of(DispatcherType.REQUEST));
            }
        }

        // --autoreload=all --autoreload=WebContent/classes,other/path
        // --scaninterval=1
        // Configure Jetty to auto-reload when a any class is compiled in
        // any folder included in the list of folders passed as arguments
        // or in the entire classpath if the keyworkd all is passed.
        if (serverArgs.containsKey("autoreload")) {
            int interval = 1;
            if (serverArgs.containsKey("scaninterval")) {
                interval = Integer.parseInt(serverArgs.get("scaninterval"));
            }

            List<File> classFolders = new ArrayList<File>();
            String[] paths = serverArgs.get("autoreload").split(",");
            if (paths.length == 1 && "all".equals(paths[0])) {
                ClassLoader cl = server.getClass().getClassLoader();
                for (URL u : ((URLClassLoader) cl).getURLs()) {
                    File f = new File(u.getPath());
                    if (f.isDirectory()) {
                        classFolders.add(f);
                    }
                }
            } else {
                for (String p : paths) {
                    File f = new File(p);
                    if (f.isDirectory()) {
                        classFolders.add(f);
                    }
                }
            }
            if (!classFolders.isEmpty()) {
                System.out
                        .println("Enabling context auto-reload.\n Scan interval: "
                                + interval + " secs.\n Scanned folders: ");
                for (File f : classFolders) {
                    System.out.println("  " + f.getAbsolutePath());
                    webappcontext.setExtraClasspath(f.getAbsolutePath());
                }
                System.out.println("");

                Scanner scanner = new Scanner();
                scanner.setScanInterval(interval);

                scanner.setRecursive(true);
                scanner.addListener(new Scanner.BulkListener() {
                    @Override
                    public void filesChanged(List<String> filenames)
                            throws Exception {
                        webappcontext.stop();
                        server.stop();
                        webappcontext.start();
                        server.start();
                    }
                });
                scanner.setReportExistingFilesOnStartup(false);
                scanner.setFilenameFilter(new FilenameFilter() {
                    @Override
                    public boolean accept(File folder, String name) {
                        return name.endsWith(".class");
                    }
                });

                scanner.setScanDirs(classFolders);
                scanner.start();
                server.getContainer().addBean(scanner);
            }
        }

        // Read web.xml to find all configured servlets
        webappcontext.start();

        try {
            server.start();

            if (serverArgs.containsKey("shutdownPort")) {
                int shutdownPort = Integer.parseInt(serverArgs
                        .get("shutdownPort"));
                final ServerSocket serverSocket = new ServerSocket(
                        shutdownPort, 1, InetAddress.getByName("127.0.0.1"));
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            System.out
                                    .println("Waiting for shutdown signal on port "
                                            + serverSocket.getLocalPort());
                            // Start waiting for a close signal
                            Socket accept = serverSocket.accept();
                            // First stop listening to the port
                            serverSocket.close();

                            // Start a thread that kills the JVM if
                            // server.stop() doesn't have any effect
                            Thread interruptThread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                        if (!server.isStopped()) {
                                            System.out
                                                    .println("Jetty still running. Closing JVM.");
                                            dumpThreadStacks();
                                            System.exit(-1);
                                        }
                                    } catch (InterruptedException e) {
                                        // Interrupted if server.stop() was
                                        // successful
                                    }
                                }
                            };
                            interruptThread.setDaemon(true);
                            interruptThread.start();

                            // Then stop the jetty server
                            server.stop();

                            interruptThread.interrupt();

                            // Send a byte to tell the other process that it can
                            // start jetty
                            OutputStream outputStream = accept
                                    .getOutputStream();
                            outputStream.write(0);
                            outputStream.flush();
                            // Finally close the socket
                            accept.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }.start();

            }
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

    /**
     * Sleeps for 2-5 seconds when serving resources that matches given
     * pathSpec. --slowdown=/run/APP/PUBLISHED/*,/other/path/asd.jpg
     */
    public static class SlowFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // TODO Auto-generated method stub
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException {

            String path = ((HttpServletRequest) request).getPathInfo();
            long delay = Math.round(Math.random() * 3000) + 2000;
            System.out.println("Delaying " + path + " for " + delay);

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Delay interrupted for " + path);
            } finally {
                System.out.println("Resuming " + path);
            }

            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
            // TODO Auto-generated method stub
        }

    }

    /**
     * Adds "Expires" and "Cache-control" headers when serving resources that
     * match given pathSpec, in order to cache resource for CACHE_MINUTES.
     * --cache=/run/APP/PUBLISHED/*,/other/path/asd.jpg
     */
    public static class CacheFilter implements Filter {

        private static final int CACHE_MINUTES = 1;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // TODO Auto-generated method stub
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException {

            String path = ((HttpServletRequest) request).getPathInfo();
            System.out.println("Caching " + path + " for " + CACHE_MINUTES
                    + " minutes");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, CACHE_MINUTES);

            String expires = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
                    .format(calendar.getTime());

            ((HttpServletResponse) response).setHeader("Expires", expires);
            ((HttpServletResponse) response).setHeader("Cache-Control",
                    "max-age=" + (CACHE_MINUTES * 60));

            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
            // TODO Auto-generated method stub
        }
    }

    private static void dumpThreadStacks() {
        for (Entry<Thread, StackTraceElement[]> entry : Thread
                .getAllStackTraces().entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackTraceElements = entry.getValue();

            System.out.println(thread.getName() + " - " + thread.getState());
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                System.out.println("    at " + stackTraceElement.toString());
            }
            System.out.println();
        }
    }

}
