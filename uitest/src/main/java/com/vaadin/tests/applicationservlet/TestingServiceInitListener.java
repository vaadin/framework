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
package com.vaadin.tests.applicationservlet;

import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.server.ConnectorIdGenerator;
import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.VaadinServiceInitListener;

public class TestingServiceInitListener implements VaadinServiceInitListener {

    private static AtomicInteger initCount = new AtomicInteger();
    private static AtomicInteger requestCount = new AtomicInteger();
    private static AtomicInteger connectorIdCount = new AtomicInteger();

    @Override
    public void serviceInit(ServiceInitEvent event) {
        initCount.incrementAndGet();

        event.addRequestHandler((session, request, response) -> {
            requestCount.incrementAndGet();
            return false;
        });

        event.addConnectorIdGenerator(connectorIdGenerationEvent -> {
            connectorIdCount.incrementAndGet();
            return ConnectorIdGenerator
                    .generateDefaultConnectorId(connectorIdGenerationEvent);
        });
    }

    public static int getInitCount() {
        return initCount.get();
    }

    public static int getRequestCount() {
        return requestCount.get();
    }

    public static int getConnectorIdCount() {
        return connectorIdCount.get();
    }

}
