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

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@Theme("valo")
public class MaximizeRestoreWindowWithManagedLayout extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout gridLayout = new GridLayout(1, 2);
        TextField textField = new TextField();
        textField.setCaption("textfield");
        textField.setWidth("100%");
        gridLayout.addComponent(textField, 0, 1);
        gridLayout.setSizeFull();

        Window window = new Window();
        window.setWidth("400px");
        window.setHeight("300px");
        window.center();
        window.setResizable(true);
        window.setContent(gridLayout);
        addWindow(window);
    }

}
