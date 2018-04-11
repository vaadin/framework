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
