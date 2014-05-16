/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.popupview;

import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

/**
 * Test UI for popup view with extension: extension is a part of getChildren()
 * collection but is not inside the getChildComponents() collection. Popup view
 * should use getChildComponents() to avoid exception when extension is returned
 * by getChildren().
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PopupViewWithExtension extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("label");
        PopupView view = new PopupView("small", label);

        Responsive.makeResponsive(view);

        addComponent(view);
    }

    @Override
    protected String getTestDescription() {
        return "PopupView should use getChildComponents() in the connector, not getChildren()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13503;
    }

}
