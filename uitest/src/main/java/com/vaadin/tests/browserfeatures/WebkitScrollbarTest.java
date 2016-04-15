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

package com.vaadin.tests.browserfeatures;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class WebkitScrollbarTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout uiLayout = new VerticalLayout();
        uiLayout.setMargin(true);
        setContent(uiLayout);

        final VerticalLayout windowLayout = new VerticalLayout();

        final Window testWindow = new Window("WebKitFail", windowLayout);
        testWindow.setWidth(300, Unit.PIXELS);

        GridLayout gl = new GridLayout();
        gl.setHeight(null);
        gl.setWidth(100, Unit.PERCENTAGE);
        windowLayout.addComponent(gl);

        ListSelect listSelect = new ListSelect();
        listSelect.setWidth(100, Unit.PERCENTAGE);
        gl.addComponent(listSelect);
        gl.setMargin(true);

        final Button testButton = new Button("Open Window",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        UI.getCurrent().addWindow(testWindow);
                    }
                });
        uiLayout.addComponent(testButton);

    }

    @Override
    protected String getTestDescription() {
        return "When opening the window, it should NOT contain a horizontal"
                + " scrollbar and the vertical height should be proportional"
                + " to the list select component inside it.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11994;
    }

}
