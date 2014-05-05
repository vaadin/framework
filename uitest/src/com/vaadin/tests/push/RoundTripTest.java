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
package com.vaadin.tests.push;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.RoundTripTester;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

@Widgetset(TestingWidgetSet.NAME)
public class RoundTripTest extends AbstractTestUI {

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
        start.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                roundTripTester.start(
                        (Integer) testDuration.getConvertedValue(),
                        (Integer) payloadSize.getConvertedValue());
            }
        });
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
