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
package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

public class ButtonTooltips extends AbstractReindeerTestUI {

    public static final String shortDescription = "Another";
    public static final String longDescription = "long descidescidescpription";

    @Override
    protected String getTestDescription() {
        return "Button tooltip's size gets messed up if moving from one tooltip to another before a timer expires.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8454;
    }

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        Button button = new Button("One");
        button.setDescription(longDescription);
        Button button2 = new Button("Two");
        button2.setDescription(shortDescription);
        vl.addComponent(button);
        vl.addComponent(button2);
        vl.setComponentAlignment(button, Alignment.TOP_RIGHT);
        vl.setComponentAlignment(button2, Alignment.TOP_RIGHT);
        addComponent(vl);

    }
}
