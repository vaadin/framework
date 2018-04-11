package com.vaadin.tests.widgetset.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Duration;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.RoundTripTester;

@Connect(RoundTripTester.class)
public class RoundTripTesterConnector extends AbstractComponentConnector {

    private double lastPrintedTime = -1;
    private int receivedPings = 0;
    private List<Double> throughputData = new ArrayList<>();
    private int payloadSize = 0;

    @Override
    protected void init() {
        super.init();
        registerRpc(RoundTripTesterRpc.class, new RoundTripTesterRpc() {

            @Override
            public void ping(int nr, String payload) {
                getRpcProxy(RoundTripTesterRpc.class).ping(nr + 1, payload);
                payloadSize = payload.length();

                double now = Duration.currentTimeMillis();
                if (lastPrintedTime == -1) {
                    lastPrintedTime = now;
                    return;
                }
                receivedPings++;

                if (now - lastPrintedTime > 1000) {
                    double roundtripsPerSecond = receivedPings
                            / (now - lastPrintedTime) * 1000;
                    throughputData.add(roundtripsPerSecond);
                    getWidget().setText(
                            roundtripsPerSecond + " roundtrips/second");

                    lastPrintedTime = now;
                    receivedPings = 0;
                }

            }

            @Override
            public void done() {
                String result = "Test results for payload of size "
                        + payloadSize + ":";
                double max = -1;
                double min = 1239482038939.0;
                double avg = 0;

                for (Double throughput : throughputData) {
                    if (throughput > max) {
                        max = throughput;
                    }
                    if (throughput < min) {
                        min = throughput;
                    }

                    avg += throughput;
                }
                avg /= throughputData.size();

                for (Double throughput : throughputData) {
                    result += "<br/>" + formatThroughput(throughput);
                }
                result += "<br/>Max: " + formatThroughput(max);
                result += "<br/>Min: " + formatThroughput(min);
                result += "<br/>Average: " + formatThroughput(avg);
                getWidget().setHTML(result);
                getRpcProxy(RoundTripTesterRpc.class).done();
            }

            private String formatThroughput(double throughput) {
                return throughput + " roundtrips / second";
            }
        });
    }

    @Override
    public HTML getWidget() {
        return (HTML) super.getWidget();
    }

}
