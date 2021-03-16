/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.SelectiveRenderer;

public class MissingHierarchyDetection extends AbstractTestUIWithLog {

    private boolean isChildRendered = true;
    private BrokenCssLayout brokenLayout = new BrokenCssLayout();

    private CssLayout normalLayout = new CssLayout(
            new Label("Normal layout child"));
    private List<LogRecord> pendingErrors = Collections
            .synchronizedList(new ArrayList<LogRecord>());

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
        final Logger vaadinSessionLogger = Logger
                .getLogger(VaadinSession.class.getName());
        vaadinSessionLogger.addHandler(new Handler() {

            @Override
            public void publish(LogRecord record) {
                if (record.getThrown() instanceof AssertionError) {
                    pendingErrors.add(record);
                    vaadinSessionLogger.removeHandler(this);
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
