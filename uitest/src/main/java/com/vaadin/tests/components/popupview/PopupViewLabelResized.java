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
package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

public class PopupViewLabelResized extends TestBase {

    @Override
    protected String getDescription() {
        return "When clicking on the popup view on the left, its size should not change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3483;
    }

    @Override
    protected void setup() {
        GridLayout gl = new GridLayout(3, 1);
        gl.setSizeFull();

        Label expander = new Label();
        gl.addComponent(expander, 1, 0);
        gl.setColumnExpandRatio(1, 1);

        gl.addComponent(new PopupView("Click here to popup", new Label("test")),
                0, 0);
        gl.addComponent(new PopupView("Click here to popup", new Label("test")),
                2, 0);

        addComponent(gl);
    }

}
