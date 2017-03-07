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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class WebBrowserSize extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Label screenSizeLabel = new Label("n/a");
        screenSizeLabel.setCaption("Screen size");

        final Label browserSizeLabel = new Label("n/a");
        browserSizeLabel.setCaption("Client (browser window) size");

        final Button update = new Button("Refresh", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                screenSizeLabel.setValue(getBrowser().getScreenWidth() + " x "
                        + getBrowser().getScreenHeight());
                browserSizeLabel.setValue(getPage().getBrowserWindowWidth()
                        + " x " + getPage().getBrowserWindowHeight());
            }
        });

        addComponent(update);
        addComponent(screenSizeLabel);
        addComponent(browserSizeLabel);

    }

    @Override
    protected String getTestDescription() {
        return "Verifies that browser sizes are reported correctly. Note that client width differs depending on browser decorations.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5655;
    }

}
