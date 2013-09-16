/*
 * Copyright 2000-2013 Vaadind Ltd.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Provides values for parameters which depend on where the test is run.
 * Parameters should be configured in work/eclipse-run-selected-test.properties.
 * A template is available in uitest/.
 * 
 * @author Vaadin Ltd
 */
public abstract class PrivateTB3Configuration extends ScreenshotTB3Test {
    private static final String HOSTNAME_PROPERTY = "com.vaadin.testbench.deployment.hostname";
    private final Properties properties = new Properties();

    public PrivateTB3Configuration() {
        File file = new File("work", "eclipse-run-selected-test.properties");
        if (file.exists()) {
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getProperty(String name) {
        String property = properties.getProperty(name);
        if (property == null) {
            property = System.getProperty(name);
        }

        return property;
    }

    @Override
    protected String getScreenshotDirectory() {
        String screenshotDirectory = getProperty("com.vaadin.testbench.screenshot.directory");
        if (screenshotDirectory == null) {
            throw new RuntimeException(
                    "No screenshot directory defined. Use -Dcom.vaadin.testbench.screenshot.directory=<path>");
        }
        return screenshotDirectory;
    }

    @Override
    protected String getHubHostname() {
        return "tb3-hub.intra.itmill.com";
    }

    @Override
    protected String getDeploymentHostname() {
        String hostName = getProperty(HOSTNAME_PROPERTY);

        if (hostName == null || "".equals(hostName)) {
            hostName = findAutoHostname();
        }

        return hostName;
    }

    /**
     * Tries to automatically determine the IP address of the machine the test
     * is running on.
     * 
     * @return An IP address of one of the network interfaces in the machine.
     * @throws RuntimeException
     *             if there was an error or no IP was found
     */
    private String findAutoHostname() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();
                if (!current.isUp() || current.isLoopback()
                        || current.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress current_addr = addresses.nextElement();
                    if (current_addr.isLoopbackAddress()) {
                        continue;
                    }
                    String hostAddress = current_addr.getHostAddress();
                    if (hostAddress.startsWith("192.168.")) {
                        return hostAddress;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        throw new RuntimeException(
                "No compatible (192.168.*) ip address found.");
    }

}
