/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4);
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.popupview;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

/**
 *
 * @author Vaadin Ltd
 */
public class PopupViewCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        FormLayout layout = new FormLayout();
        addComponent(layout);
        Label label = new Label("Label");
        PopupView popup = new PopupView("Popup short text", label);
        popup.setCaption("Popup Caption:");
        layout.addComponent(popup);
    }

    @Override
    protected String getTestDescription() {
        return "Caption for popup view should be shown by layout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10618;
    }

}
