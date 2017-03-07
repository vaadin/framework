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
package com.vaadin.tests.components.ui;

import java.net.URI;

import com.vaadin.server.Page;
import com.vaadin.server.Page.PopStateEvent;
import com.vaadin.server.Page.PopStateListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class PushStateAndReplaceState extends AbstractReindeerTestUI {

    private final Label locationLabel = new Label();
    private CheckBox replace;

    @Override
    protected void setup(VaadinRequest request) {
        locationLabel.setId("locationLabel");
        addComponent(locationLabel);
        updateLabel();

        getPage().addPopStateListener(new PopStateListener() {

            @Override
            public void uriChanged(PopStateEvent event) {
                Notification.show("Popstate event");
                updateLabel();
            }
        });

        replace = new CheckBox("replace");
        replace.setId("replace");
        addComponent(replace);

        addComponent(createButton("test", "Move to ./test",
                Page.getCurrent().getLocation().toString() + "/test"));
        addComponent(createButton("X", "Move to X", "X"));
        addComponent(createButton("root_X", "Move to /X", "/X"));
    }

    private Button createButton(String id, String caption,
            final String newUri) {
        Button button = new Button(caption, new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (replace.getValue()) {
                    getPage().replaceState(newUri);
                } else {
                    getPage().pushState(newUri);
                }
                updateLabel();
            }
        });

        button.setId(id);

        return button;
    }

    private void updateLabel() {
        URI location = getPage().getLocation();
        locationLabel.setValue("Current Location: " + location.toString());
    }

    @Override
    public String getTestDescription() {
        return "Modern web framework shouldn't force you to use hashbang style urls for deep linking";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
