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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.ScrollableGridLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;

@Widgetset(TestingWidgetSet.NAME)
public class GridLayoutFractionalSizeAndAlignment extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        widthTest();
        heightTest();
    }

    private void widthTest() {
        final GridLayout layout = new ScrollableGridLayout(1, 1);
        layout.setMargin(false);
        layout.setSpacing(true);

        layout.setWidth(525.04f, Unit.PIXELS);

        Button button = new Button("Button");

        layout.addComponent(button);
        layout.setComponentAlignment(button, Alignment.BOTTOM_RIGHT);

        addComponent(layout);
    }

    private void heightTest() {
        final GridLayout layout = new ScrollableGridLayout(1, 1);
        layout.setMargin(false);
        layout.setSpacing(true);

        layout.setWidth(525.04f, Unit.PIXELS);
        layout.setHeight(525.04f, Unit.PIXELS);

        Button button = new Button("Button");

        layout.addComponent(button);
        layout.setComponentAlignment(button, Alignment.BOTTOM_RIGHT);

        addComponent(layout);
    }
}
