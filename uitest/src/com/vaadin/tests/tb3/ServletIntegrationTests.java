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

package com.vaadin.tests.tb3;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.integration.AbstractServletIntegrationTest;
import com.vaadin.tests.integration.ServletIntegrationJSR356WebsocketUITest;
import com.vaadin.tests.integration.ServletIntegrationWebsocketUITest;
import com.vaadin.tests.tb3.ServletIntegrationTests.ServletIntegrationTestSuite;

@RunWith(ServletIntegrationTestSuite.class)
public class ServletIntegrationTests {

    public static Set<String> notJSR356Compatible = new HashSet<String>();
    public static Set<String> notWebsocketCompatible = new HashSet<String>();
    static {

        notJSR356Compatible.add("jetty7");
        notJSR356Compatible.add("jetty8");
        notJSR356Compatible.add("tomcat7");
        notJSR356Compatible.add("tomcat7apacheproxy");
        notJSR356Compatible.add("osgi"); // Karaf 3, Jetty 8

        // In theory GF3 could work but in reality broken
        notWebsocketCompatible.add("glassfish3");
        notWebsocketCompatible.add("jboss4");
        notWebsocketCompatible.add("jboss5");
        notWebsocketCompatible.add("jboss6");
        notWebsocketCompatible.add("jboss7");
        notWebsocketCompatible.add("tomcat6");
        notWebsocketCompatible.add("tomcat7apacheproxy");
        notWebsocketCompatible.add("weblogic10");

        // Requires an update to 8.5.5 and a fix for
        // https://dev.vaadin.com/ticket/16354
        // https://developer.ibm.com/answers/questions/186066/websocket-paths-using-uri-templates-do-not-work-pr/
        notWebsocketCompatible.add("websphere8");

        // If a server does not support any kind of websockets it does not
        // support JSR-356 either..
        notJSR356Compatible.addAll(notWebsocketCompatible);
    }

    public static class ServletIntegrationTestSuite extends TB3TestSuite {
        public ServletIntegrationTestSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractServletIntegrationTest.class,
                    "com.vaadin.tests.integration", new String[] {},
                    new ServletTestLocator());
        }
    }

    public static class ServletTestLocator extends TB3TestLocator {
        @Override
        protected <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
                String basePackage, String[] ignoredPackages)
                throws IOException {
            List<Class<? extends T>> allClasses = super.findClasses(baseClass,
                    basePackage, ignoredPackages);
            String serverName = System.getProperty("server-name");

            if (notJSR356Compatible.contains(serverName)) {
                allClasses
                        .remove(ServletIntegrationJSR356WebsocketUITest.class);
            }

            if (notWebsocketCompatible.contains(serverName)) {
                allClasses.remove(ServletIntegrationWebsocketUITest.class);
            }
            return allClasses;
        }
    }
}
