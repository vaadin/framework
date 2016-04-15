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
package com.vaadin.tests.components.popupview;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;

/**
 * Test UI to check availability of shortcut action listener in the popup view
 * oeverlay component.
 * 
 * @author Vaadin Ltd
 */
public class PopupViewShortcutActionHandler extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new PopupView(new DemoPoupView()));
    }

    @Override
    protected String getTestDescription() {
        return "Shortcut listener search should be executed in the end "
                + "of request (after legacy UIDL request handling).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14275;
    }

    private class DemoPoupView implements PopupView.Content {

        @Override
        public String getMinimizedValueAsHTML() {
            return "Click Me";
        }

        @Override
        public Component getPopupComponent() {
            TextField field = new TextField("Enter text");
            field.setImmediate(true);
            field.addShortcutListener(new ShortcutListener("SearchAction",
                    ShortcutAction.KeyCode.ENTER, null) {
                private static final long serialVersionUID = 1L;

                @Override
                public void handleAction(Object sender, Object target) {
                    Label label = new Label(
                            "shortcut addedEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    label.addStyleName("shortcut-result");
                    addComponent(label);
                }
            });
            return field;
        }

    }

}
