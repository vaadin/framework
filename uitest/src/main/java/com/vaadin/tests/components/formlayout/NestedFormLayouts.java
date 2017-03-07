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
package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

public class NestedFormLayouts extends AbstractReindeerTestUI {

    private FormLayout outer;
    private FormLayout inner1;
    private FormLayout inner2;
    private FormLayout inner21;
    private FormLayout inner3;
    private FormLayout inner31;
    private FormLayout inner4;

    @Override
    protected void setup(VaadinRequest request) {
        outer = new FormLayout();
        outer.setSizeUndefined();
        outer.setWidth("100%");

        inner1 = new FormLayout();
        inner1.addComponent(new Label("Test"));
        inner1.addComponent(new Label("Test2"));
        outer.addComponent(inner1);

        outer.addComponent(new Label("Test"));
        outer.addComponent(new Label("Test2"));

        inner2 = new FormLayout();
        inner2.addComponent(new Label("Test"));
        inner2.addComponent(new Label("Test2"));
        inner21 = new FormLayout();
        inner21.addComponent(new Label("Test"));
        inner21.addComponent(new Label("Test2"));
        inner2.addComponent(inner21);
        outer.addComponent(inner2);

        inner3 = new FormLayout();
        inner3.addComponent(new Label("Test"));
        inner3.addComponent(new Label("Test2"));
        // this layout never gets spacing or margin
        inner31 = new FormLayout();
        inner31.addComponent(new Label("Test"));
        inner31.addComponent(new Label("Test2"));
        inner31.setSpacing(false);
        inner31.setMargin(false);
        inner3.addComponent(inner31);
        outer.addComponent(inner3);

        inner4 = new FormLayout();
        inner4.addComponent(new Label("Test"));
        inner4.addComponent(new Label("Test2"));
        outer.addComponent(inner4);

        addComponent(outer);

        final CheckBox spacingCheckBox = new CheckBox("Spacings", false);
        spacingCheckBox.setId("spacings");
        spacingCheckBox.addValueChangeListener(
                event -> setLayoutSpacing(spacingCheckBox.getValue()));
        addComponent(spacingCheckBox);

        final CheckBox marginCheckBox = new CheckBox("Margins", false);
        marginCheckBox.setId("margins");
        marginCheckBox.addValueChangeListener(
                event -> setLayoutMargin(marginCheckBox.getValue()));
        addComponent(marginCheckBox);

        setLayoutSpacing(false);
        setLayoutMargin(false);
    }

    private void setLayoutSpacing(boolean value) {
        outer.setSpacing(value);
        inner1.setSpacing(value);
        inner2.setSpacing(value);
        inner21.setSpacing(value);
        inner3.setSpacing(value);
        inner4.setSpacing(value);
    }

    private void setLayoutMargin(boolean value) {
        outer.setMargin(value);
        inner1.setMargin(value);
        inner2.setMargin(value);
        inner21.setMargin(value);
        inner3.setMargin(value);
        inner4.setMargin(value);
    }

    @Override
    protected String getTestDescription() {
        return "Excess padding applied in FormLayouts nested as first or last rows in a FormLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9427;
    }

}
