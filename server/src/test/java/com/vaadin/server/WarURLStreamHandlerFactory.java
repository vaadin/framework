package com.vaadin.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Permission;

/**
 * Test factory for URL stream protocol handlers, needed for WAR handling in
 * {@link VaadinServletTest}. Cherry-picked from Flow, some of the
 * implementation details are not needed for Vaadin 8 at the moment, but they
 * are left in because they aren't interfering either.
 */
public class WarURLStreamHandlerFactory
        implements URLStreamHandlerFactory, Serializable {

    private static final String WAR_PROTOCOL = "war";

    // Singleton instance
    private static volatile WarURLStreamHandlerFactory instance = null;

    private final boolean registered;

    /**
     * Obtain a reference to the singleton instance. It is recommended that
     * callers check the value of {@link #isRegistered()} before using the
     * returned instance.
     *
     * @return A reference to the singleton instance
     */
    public static WarURLStreamHandlerFactory getInstance() {
        getInstanceInternal(true);
        return instance;
    }

    private static WarURLStreamHandlerFactory getInstanceInternal(
            boolean register) {
        // Double checked locking. OK because instance is volatile.
        if (instance == null) {
            synchronized (WarURLStreamHandlerFactory.class) {
                if (instance == null) {
                    instance = new WarURLStreamHandlerFactory(register);
                }
            }
        }
        return instance;
    }

    private WarURLStreamHandlerFactory(boolean register) {
        // Hide default constructor
        // Singleton pattern to ensure there is only one instance of this
        // factory
        registered = register;
        if (register) {
            URL.setURLStreamHandlerFactory(this);
        }
    }

    public boolean isRegistered() {
        return registered;
    }

    /**
     * Register this factory with the JVM. May be called more than once. The
     * implementation ensures that registration only occurs once.
     *
     * @return <code>true</code> if the factory is already registered with the
     *         JVM or was successfully registered as a result of this call.
     *         <code>false</code> if the factory was disabled prior to this
     *         call.
     */
    public static boolean register() {
        return getInstanceInternal(true).isRegistered();
    }

    /**
     * Prevent this this factory from registering with the JVM. May be called
     * more than once.
     *
     * @return <code>true</code> if the factory is already disabled or was
     *         successfully disabled as a result of this call.
     *         <code>false</code> if the factory was already registered prior to
     *         this call.
     */
    public static boolean disable() {
        return !getInstanceInternal(false).isRegistered();
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {

        // Tomcat's handler always takes priority so applications can't override
        // it.
        if (WAR_PROTOCOL.equals(protocol)) {
            return new WarHandler();
        }

        // Unknown protocol
        return null;
    }

    public static class WarHandler extends URLStreamHandler
            implements Serializable {

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new WarURLConnection(u);
        }

        @Override
        protected void setURL(URL u, String protocol, String host, int port,
                String authority, String userInfo, String path, String query,
                String ref) {
            if (path.startsWith("file:") && !path.startsWith("file:/")) {
                /*
                 * Work around a problem with the URLs in the security policy
                 * file. On Windows, the use of ${catalina.[home|base]} in the
                 * policy file results in codebase URLs of the form file:C:/...
                 * when they should be file:/C:/...
                 *
                 * For file: and jar: URLs, the JRE compensates for this. It
                 * does not compensate for this for war:file:... URLs.
                 * Therefore, we do that here
                 */
                path = "file:/" + path.substring(5);
            }
            super.setURL(u, protocol, host, port, authority, userInfo, path,
                    query, ref);
        }

    }

    public static class WarURLConnection extends URLConnection
            implements Serializable {

        private final URLConnection wrappedJarUrlConnection;
        private boolean connected;

        protected WarURLConnection(URL url) throws IOException {
            super(url);
            URL innerJarUrl = warToJar(url);
            wrappedJarUrlConnection = innerJarUrl.openConnection();
        }

        @Override
        public void connect() throws IOException {
            if (!connected) {
                wrappedJarUrlConnection.connect();
                connected = true;
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            connect();
            return wrappedJarUrlConnection.getInputStream();
        }

        @Override
        public Permission getPermission() throws IOException {
            return wrappedJarUrlConnection.getPermission();
        }

        @Override
        public long getLastModified() {
            return wrappedJarUrlConnection.getLastModified();
        }

        @Override
        public int getContentLength() {
            return wrappedJarUrlConnection.getContentLength();
        }

        @Override
        public long getContentLengthLong() {
            return wrappedJarUrlConnection.getContentLengthLong();
        }

        public static URL warToJar(URL warUrl) throws MalformedURLException {
            // Assumes that the spec is absolute and starts war:file:/...
            String file = warUrl.getFile();
            if (file.contains("*/")) {
                file = file.replaceFirst("\\*/", "!/");
            } else if (file.contains("^/")) {
                file = file.replaceFirst("\\^/", "!/");
            }

            return new URL("jar", warUrl.getHost(), warUrl.getPort(), file);
        }
    }
}