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
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class ChangeHierarchyBeforeResponse extends AbstractTestUI {
    private CssLayout layout = new CssLayout() {
        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (initial) {
                addComponent(buttonToAdd);
                removeComponent(labelToRemove);
            }
        }
    };

    private Button buttonToAdd = new Button("Added from beforeClientResponse",
            new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    layout.addComponent(labelToRemove);
                }
            }) {
        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            setCaption("Add label to layout");
        }
    };

    private Label labelToRemove = new Label("Label to remove") {
        int count = 0;

        @Override
        public void beforeClientResponse(boolean initial) {
            super.beforeClientResponse(initial);
            if (initial) {
                count++;
                setValue("Initial count: " + count);
            }
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        layout.addComponent(labelToRemove);

        addComponent(layout);
    }

}
