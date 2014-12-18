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
package com.vaadin.tests.serialization;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.LayoutDetector;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.JavaScript;

@Widgetset(TestingWidgetSet.NAME)
public class NoLayout extends AbstractTestUI {
    private final LayoutDetector layoutDetector = new LayoutDetector();

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(layoutDetector);

        CheckBox uiPolling = new CheckBox("UI polling enabled");
        uiPolling.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() == Boolean.TRUE) {
                    setPollInterval(100);
                } else {
                    setPollInterval(-1);
                }
            }
        });
        addComponent(uiPolling);

        addComponent(new Button("Change regular state",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        event.getButton().setCaption(
                                String.valueOf(System.currentTimeMillis()));
                    }
                }));
        addComponent(new Button("Change @NoLayout state",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        event.getButton().setDescription(
                                String.valueOf(System.currentTimeMillis()));
                    }
                }));
        addComponent(new Button("Do regular RPC", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                JavaScript.eval("");
            }
        }));

        addComponent(new Button("Do @NoLayout RPC", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layoutDetector.doNoLayoutRpc();
            }
        }));

        addComponent(new Button("Update LegacyComponent",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        // Assumes UI is a LegacyComponent
                        markAsDirty();
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        return "Checks which actions trigger a layout phase";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(12936);
    }

}
