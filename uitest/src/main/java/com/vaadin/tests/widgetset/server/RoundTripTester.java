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
package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.tests.widgetset.client.RoundTripTesterRpc;
import com.vaadin.ui.AbstractComponent;

public class RoundTripTester extends AbstractComponent {
    private long testStart = 0;
    private long testEnd = 0;

    public RoundTripTester() {
        registerRpc(new RoundTripTesterRpc() {
            @Override
            public void ping(int nr, String payload) {
                if (System.currentTimeMillis() < testEnd) {
                    getRpcProxy(RoundTripTesterRpc.class).ping(nr + 1, payload);
                } else {
                    getRpcProxy(RoundTripTesterRpc.class).done();
                }
            }

            @Override
            public void done() {
            }
        });
    }

    public void start(long testDuration, int payloadSize) {
        testStart = System.currentTimeMillis();
        testEnd = testStart + testDuration;
        getRpcProxy(RoundTripTesterRpc.class).ping(1,
                generatePayload(payloadSize));
    }

    private String generatePayload(int payloadSize) {
        StringBuilder sb = new StringBuilder();
        while (payloadSize > 10000) {
            payloadSize -= 10000;
            sb.append(LoremIpsum.get(10000));
        }
        sb.append(LoremIpsum.get(payloadSize));
        return sb.toString();
    }

}
