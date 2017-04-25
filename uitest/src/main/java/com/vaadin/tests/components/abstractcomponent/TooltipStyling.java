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
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class TooltipStyling extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label defaultLabel = new Label(
                "I have a tooltip with default settings");
        defaultLabel.setDescription(
                "This long description should be shown with the application's default font and wrap to several lines as needed."
                        + "\n\nThis part should be on a separate line");
        defaultLabel.setId("default");
        addComponent(defaultLabel);

        Label htmlLabel = new Label("I have a tooltip with HTML contents");
        htmlLabel.setDescription(
                "This is regular text in a tooltip."
                        + "<pre>This is a pre tag inside a HTML tooltip. It should use a monospace font and by default not break to multiple lines.</pre>",
                ContentMode.HTML);
        htmlLabel.setId("html");
        addComponent(htmlLabel);
    }

    @Override
    protected String getTestDescription() {
        return "Tooltips should be shown with the regular application font and automatically wrap to multiple lines for long contents.<br />"
                + "&lt;pre> tag contents in a HTML tooltip should still behave according to browser defaults.";
    }

}
