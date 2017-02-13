package com.vaadin.tests.integration;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.testbench.parallel.ParallelTestSuite;
import com.vaadin.tests.integration.ServletIntegrationTests.ServletIntegrationTestSuite;

@RunWith(ServletIntegrationTestSuite.class)
public class ServletIntegrationTests {
    public static class ServletIntegrationTestSuite extends ParallelTestSuite {

        public ServletIntegrationTestSuite(Class<?> klass)
                throws InitializationError, IOException {
            super(klass, AbstractIntegrationTest.class,
                    "com.vaadin.tests.integration", getIgnoredPackages());
        }

        private static String[] getIgnoredPackages() {
            String serverName = System.getProperty("server-name");
            if (serverName != null && serverName.equals("widfly9-nginx")) {
                return new String[] {};
            }
            return new String[] { "com.vaadin.tests.integration.push" };
        }
    }
}
