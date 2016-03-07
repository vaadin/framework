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
package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;

public class TimingInfoReported extends AbstractTestUIWithLog {

    private String reportTimings = "setTimeout(function() {"
            + "report(window.vaadin.clients[Object.keys(window.vaadin.clients)].getProfilingData());"
            + "},0);";

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getJavaScript().addFunction("report",
                new JavaScriptFunction() {

                    @Override
                    public void call(JsonArray arguments) {
                        log("Got: " + arguments.toJson());
                        JsonArray values = arguments.getArray(0);

                        if (values.length() != 5) {
                            log("ERROR: expected 5 values, got "
                                    + values.length());
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
                    }
                });
        getPage().getJavaScript().execute(reportTimings);
        Button b = new Button("test request", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getPage().getJavaScript().execute(reportTimings);

            }
        });
        addComponent(b);
    }
}
