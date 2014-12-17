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
package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is used for testing that a tooltip is not positioned partially
 * outside the browser window when there is enough space to display it.
 * 
 * @author Vaadin Ltd
 */
public class TooltipPosition extends AbstractTestUI {

    public static final int NUMBER_OF_BUTTONS = 5;

    @Override
    protected void setup(VaadinRequest request) {
        // These tooltip delay settings can be removed once #13854 is resolved.
        getTooltipConfiguration().setOpenDelay(0);
        getTooltipConfiguration().setQuickOpenDelay(0);
        getTooltipConfiguration().setCloseTimeout(1000);

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setHeight(UI.getCurrent().getPage().getBrowserWindowHeight(),
                Unit.PIXELS);
        addComponent(layout);
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            Button button = new Button("Button");
            button.setDescription(generateTooltipText());
            layout.addComponent(button);
        }
    }

    private String generateTooltipText() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            result.append("This is the line ").append(i)
                    .append(" of the long tooltip text.<br>");
        }
        return result.toString();
    }

    @Override
    public String getTestDescription() {
        return "The tooltips of the buttons should not be clipped when there is enough space to display them.";
    }

    @Override
    public Integer getTicketNumber() {
        return 15129;
    }
}
