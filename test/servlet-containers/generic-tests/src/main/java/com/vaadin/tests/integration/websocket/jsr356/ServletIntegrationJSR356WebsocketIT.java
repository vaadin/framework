/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.integration.websocket.jsr356;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assume;

import com.vaadin.tests.integration.websocket.ServletIntegrationWebsocketIT;

public class ServletIntegrationJSR356WebsocketIT
        extends ServletIntegrationWebsocketIT {
    // Uses the test method declared in the super class

    private static final Set<String> nonJSR356Servers = new HashSet<>();

    static {
        nonJSR356Servers.add("jetty8");
    }

    @Override
    public void setup() throws Exception {
        Assume.assumeFalse("This server does not support JSR356",
                nonJSR356Servers.contains(System.getProperty("server-name")));

        super.setup();
    }

    @Override
    protected String getTestPath() {
        return super.getTestPath().replace("/run/", "/run-jsr356/");
    }
}
