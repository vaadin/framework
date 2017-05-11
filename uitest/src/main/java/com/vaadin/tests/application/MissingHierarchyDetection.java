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
package com.vaadin.tests.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.SelectiveRenderer;

public class MissingHierarchyDetection extends AbstractReindeerTestUIWithLog {

    private boolean isChildRendered = true;
    private BrokenCssLayout brokenLayout = new BrokenCssLayout();

    private CssLayout normalLayout = new CssLayout(
            new Label("Normal layout child"));
    private List<LogRecord> pendingErrors = Collections
            .synchronizedList(new ArrayList<>());

    public class BrokenCssLayout extends CssLayout
            implements SelectiveRenderer {
        public BrokenCssLayout() {
            setCaption("Broken layout");
            Label label = new Label("Child component");
            label.setId("label");
            addComponent(label);
        }

        @Override
        public boolean isRendered(Component childComponent) {
            return isChildRendered;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        // Catch log messages so we can see if there is an error
        Logger vaadinServiceLogger = Logger
                .getLogger(VaadinService.class.getName());
        vaadinServiceLogger.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {
                if (record.getThrown() instanceof AssertionError) {
                    pendingErrors.add(record);
                    vaadinServiceLogger.removeHandler(this);
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
        addComponent(brokenLayout);
        addComponent(normalLayout);
        addComponent(new Button("Toggle properly", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toggle(true);
            }
        }));
        addComponent(
                new Button("Toggle improperly", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        toggle(false);
                    }
                }));
        addComponent(new Button("Check for errors", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (!pendingErrors.isEmpty()) {
                    log(pendingErrors.remove(0).getThrown().getMessage());
                } else {
                    log("No errors");
                }
            }
        }));
    }

    private void toggle(boolean properly) {
        isChildRendered = !isChildRendered;
        if (properly) {
            brokenLayout.markAsDirtyRecursive();
        }

        normalLayout.getComponent(0).setVisible(isChildRendered);
        // Must also have a state change of the layout to trigger special case
        // related to optimizations
        normalLayout.setCaption("With child: " + isChildRendered);
    }
}
