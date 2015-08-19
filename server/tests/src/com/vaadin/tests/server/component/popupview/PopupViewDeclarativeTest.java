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
package com.vaadin.tests.server.component.popupview;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignContext;

public class PopupViewDeclarativeTest extends DeclarativeTestBase<PopupView> {

    @Test
    public void testEmptyPopupView() {
        PopupView component = new PopupView();
        Component popup = component.getContent().getPopupComponent();
        String design = "<v-popup-view><popup-content>"
                + new DesignContext().createElement(popup)
                + "</popup-content></v-popup-view>";
        testWrite(design, component);
        testRead(design, component);
    }

    @Test
    public void testVisiblePopupDesign() {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("300px");
        verticalLayout.setHeight("400px");

        PopupView component = new PopupView("Click <u>here</u> to open",
                verticalLayout);
        component.setHideOnMouseOut(true);
        component.setPopupVisible(true);
        // hide-on-mouse-out is true by default. not seen in design
        String design = "<v-popup-view popup-visible='true'>" //
                + "Click <u>here</u> to open"
                + "<popup-content>"
                + new DesignContext().createElement(verticalLayout)
                + "</popup-content>" //
                + "</v-popup-view>";
        testWrite(design, component);
        testRead(design, component);
    }

    @Test
    public void testHideOnMouseOutDisabled() {
        final Label label = new Label("Foo");
        PopupView component = new PopupView("Click Me!", label);
        component.setHideOnMouseOut(false);
        String design = "<v-popup-view hide-on-mouse-out='false'>" //
                + "Click Me!"
                + "<popup-content>"
                + new DesignContext().createElement(label) + "</popup-content>" //
                + "</v-popup-view>";
        testWrite(design, component);
        testRead(design, component);
    }
}
