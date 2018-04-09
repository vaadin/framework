package com.vaadin.tests.push;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.RoundTripTester;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.TextField;

@Widgetset(TestingWidgetSet.NAME)
public class RoundTripTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final RoundTripTester roundTripTester = new RoundTripTester();
        final TextField payloadSize = new TextField("Payload size (bytes)");
        payloadSize.setConverter(Integer.class);
        payloadSize.setConvertedValue(10000);
        if (request.getParameter("payload") != null) {
            payloadSize.setValue(request.getParameter("payload"));
        }
        addComponent(payloadSize);
        final TextField testDuration = new TextField("Test duration (ms)");
        testDuration.setConverter(Integer.class);
        testDuration.setConvertedValue(10000);
        addComponent(testDuration);
        if (request.getParameter("duration") != null) {
            testDuration.setValue(request.getParameter("duration"));
        }

        Button start = new Button("Start test");
        start.addClickListener(event -> roundTripTester.start(
                (Integer) testDuration.getConvertedValue(),
                (Integer) payloadSize.getConvertedValue()));
        addComponent(roundTripTester);
        addComponent(start);

        if (request.getParameter("go") != null) {
            start.click();
        }
    }

    @Override
    protected String getTestDescription() {
        return "Tests how many roundtrips per second you can get using the given package size";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11370;
    }

}
