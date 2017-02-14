package com.vaadin.tests.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.testbench.parallel.ParallelTestSuite;
import com.vaadin.tests.integration.ServletIntegrationTests.ServletIntegrationTestSuite;

@RunWith(ServletIntegrationTestSuite.class)
public class ServletIntegrationTests {

    public static Set<String> notJSR356Compatible = new HashSet<>();
    public static Set<String> notWebsocketCompatible = new HashSet<>();

    static {
        notWebsocketCompatible.add("tomcat7apacheproxy");
        notWebsocketCompatible.add("weblogic10");
        notWebsocketCompatible.add("wildfly9-nginx");

        // Jetty 9 but no ws support by default
        notWebsocketCompatible.add("karaf4");

        notJSR356Compatible.add("jetty8");
        notJSR356Compatible.add("tomcat7");
    }

    public static class ServletIntegrationTestSuite extends ParallelTestSuite {

        public ServletIntegrationTestSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractIntegrationTest.class,
                    "com.vaadin.tests.integration", getIgnoredPackages());
        }

        private static String[] getIgnoredPackages() {
            List<String> ignoredPackages = new ArrayList<>();
            String serverName = System.getProperty("server-name");
            if (serverName == null) {
                serverName = "";
            }
            if (!serverName.equals("widfly9-nginx")) {
                ignoredPackages.add("com.vaadin.tests.integration.push");
            }
            if (notWebsocketCompatible.contains(serverName)) {
                ignoredPackages.add("com.vaadin.tests.integration.websocket");
            } else if (notJSR356Compatible.contains(serverName)) {
                ignoredPackages
                        .add("com.vaadin.tests.integration.websocket.jsr356");
            }

            return ignoredPackages.toArray(new String[ignoredPackages.size()]);
        }
    }
}
