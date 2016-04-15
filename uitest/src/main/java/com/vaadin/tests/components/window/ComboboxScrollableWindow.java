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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ComboboxScrollableWindow extends AbstractTestUI {

    static final String WINDOW_ID = "window";
    static final String COMBOBOX_ID = "combobox";

    @Override
    protected void setup(VaadinRequest request) {

        Window w = new Window();
        w.setId(WINDOW_ID);
        w.setWidth("300px");
        w.setHeight("300px");
        w.center();

        VerticalLayout content = new VerticalLayout();
        w.setContent(content);
        content.setHeight("1000px");
        ComboBox cb = new ComboBox();
        cb.setId(COMBOBOX_ID);
        content.addComponent(cb);
        content.setComponentAlignment(cb, Alignment.BOTTOM_CENTER);

        addWindow(w);

    }

    @Override
    protected String getTestDescription() {
        return "The combo box in the bottom of the scrollable window should remain visible when it is clicked.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12736;
    }

}
