package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

import elemental.json.JsonArray;

public class TimingInfoReported extends AbstractTestUIWithLog {

    private String reportTimings = "setTimeout(function() {"
            + "report(window.vaadin.clients[Object.keys(window.vaadin.clients)].getProfilingData());"
            + "},0);";

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getJavaScript().addFunction("report", arguments -> {
            log("Got: " + arguments.toJson());
            JsonArray values = arguments.getArray(0);

            if (values.length() != 5) {
                log("ERROR: expected 5 values, got " + values.length());
                return;
            }

            for (int i = 0; i < values.length(); i++) {
                if (i < 0 || i > 10000) {
                    log("ERROR: expected value " + i
                            + " to be between 0 and 10000, was "
                            + values.getNumber(i));
                    return;
                }
            }
            log("Timings ok");
        });
        getPage().getJavaScript().execute(reportTimings);
        Button b = new Button("test request",
                event -> getPage().getJavaScript().execute(reportTimings));
        addComponent(b);
    }
}
