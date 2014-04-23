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
package com.vaadin.tests.requesthandlers;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.PrivateTB3Configuration;

public class UnsupportedBrowserHandlerUserAgents {
    /*
     * This test doesn't use testbench, but it's still in the uitest source
     * folder since it should be run with the testing server deployed.
     */

    @Test
    public void ie7NotSupported() {
        String response = requestWithUserAgent("Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 5.2; WOW64; .NET CLR 2.0.50727)");
        Assert.assertTrue("IE7 should not be supported",
                response.contains("your browser is not supported"));
    }

    @Test
    public void ie9Supported() {
        String response = requestWithUserAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 7.1; Trident/5.0)");
        Assert.assertFalse("IE9 should be supported",
                response.contains("your browser is not supported"));
    }

    @Test
    public void unknownSupported() {
        String response = requestWithUserAgent("Very strange user agent, like wat");
        Assert.assertFalse("Unkonwn user agent should be supported",
                response.contains("your browser is not supported"));
    }

    private String requestWithUserAgent(String userAgent) {
        try {
            String url = "http://"
                    + PrivateTB3Configuration.getConfiguredDeploymentHostname()
                    + ":"
                    + PrivateTB3Configuration.getConfiguredDeploymentPort()
                    + "/run/"
                    + com.vaadin.tests.components.ui.UIInitTest.class.getName()
                    + "/";

            HttpURLConnection connection = (HttpURLConnection) new URL(url)
                    .openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            String response = IOUtils.toString(connection.getInputStream());
            connection.disconnect();

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
