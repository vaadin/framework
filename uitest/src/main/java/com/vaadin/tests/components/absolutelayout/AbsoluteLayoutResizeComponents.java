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
package com.vaadin.tests.components.absolutelayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Test UI with different cases for component size changes
 */
public class AbsoluteLayoutResizeComponents extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbsoluteLayout layout = new AbsoluteLayout();

        addStartWithFullWidth(layout);
        addStartWithDefinedWidth(layout);
        addStartWithDefinedWidthAbsoluteLayout(layout);

        setContent(layout);
    }

    /**
     * Build test layout for #8255
     */
    private void addStartWithFullWidth(AbsoluteLayout layout) {
        final Panel full = new Panel(
                new CssLayout(new Label("Start Width 100%")));
        full.setWidth("100%");
        full.setId("expanding-panel");

        layout.addComponent(full, "right:0;top:10px;");
        layout.addComponent(expandButton(full), "left: 10x; top: 50px;");
    }

    /**
     * Build test layout for #8256
     */
    private void addStartWithDefinedWidth(AbsoluteLayout layout) {
        final Panel small = new Panel(
                new CssLayout(new Label("Start Width 250px")));
        small.setWidth("250px");
        small.setId("small-panel");

        layout.addComponent(small, "right:0;top:100px;");
        layout.addComponent(expandButton(small), "left: 10x; top: 150px;");
    }

    /**
     * Build test layout for #8257
     */
    private void addStartWithDefinedWidthAbsoluteLayout(AbsoluteLayout layout) {
        AbsoluteLayout layoutExpading = new AbsoluteLayout();
        layoutExpading.setWidth("250px");
        layoutExpading.addComponent(
                new Panel(new CssLayout(new Label("Start Width 250px"))));
        layoutExpading.setId("absolute-expanding");

        layout.addComponent(layoutExpading, "right:0;top:200px;");
        layout.addComponent(expandButton(layoutExpading),
                "left: 10x; top: 250px;");
    }

    /**
     * Create size change button for component
     *
     * @param component Component to controll with button
     * @return Created Expand Button
     */
    private Button expandButton(Component component) {
        Button button = new Button("Change Size",
                clickEvent -> resizeComponent(component));
        button.setId(component.getId() + "-button");
        return button;
    }

    private void resizeComponent(Component component) {
        if (component.getWidthUnits().equals(Unit.PERCENTAGE)) {
            component.setWidth("250px");
        } else {
            component.setWidth("100%");
        }
    }
}
