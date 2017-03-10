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
package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class LayoutComponentGroup extends AbstractTestUI {

    private VerticalLayout container;

    @Override
    protected void setup(VaadinRequest request) {

        addComponent(new Label(
                "Test contains alternating rows with Button and Upload"));
        container = new VerticalLayout();
        container.setSizeUndefined();
        container.setSpacing(false);
        container.setId("container");

        createLayout("button", new Button("Upload"));
        createLayout("upload", new Upload());
        createLayout("button", new Button("Before"), new Button("Upload"));
        createLayout("upload", new Button("Before"), new Upload());
        createLayout("button", new Button("Before"), new Button("Upload"),
                new Button("After"));
        createLayout("upload", new Button("Before"), new Upload(),
                new Button("After"));

        addComponent(container);

    }

    private void createLayout(String info, Component... components) {
        CssLayout mainMenuLayout = new CssLayout();
        mainMenuLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        for (Component c : components) {
            mainMenuLayout.addComponent(c);
        }
        container.addComponent(mainMenuLayout);

    }

}
