/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.vaadin.tests.integration.AbstractIntegrationTest;
import com.vaadin.tests.integration.ServletIntegrationJSR356WebsocketUITest;
import com.vaadin.tests.integration.ServletIntegrationWebsocketUITest;
import com.vaadin.tests.tb3.ServletIntegrationTests.ServletIntegrationTestSuite;

@RunWith(ServletIntegrationTestSuite.class)
public class ServletIntegrationTests {

    public static Set<String> notJSR356Compatible = new HashSet<String>();
    public static Set<String> notWebsocketCompatible = new HashSet<String>();
    static {

        notJSR356Compatible.add("jetty8");
        notJSR356Compatible.add("tomcat7");
        notJSR356Compatible.add("tomcat7apacheproxy");

        notWebsocketCompatible.add("tomcat7apacheproxy");
        notWebsocketCompatible.add("weblogic10");
        notWebsocketCompatible.add("wildfly9-nginx");

        // Jetty 9 but no ws support by default
        notWebsocketCompatible.add("karaf4");

        // If a server does not support any kind of websockets it does not
        // support JSR-356 either..
        notJSR356Compatible.addAll(notWebsocketCompatible);
    }

    public static class ServletIntegrationTestSuite extends TB3TestSuite {
        public ServletIntegrationTestSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractIntegrationTest.class,
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
