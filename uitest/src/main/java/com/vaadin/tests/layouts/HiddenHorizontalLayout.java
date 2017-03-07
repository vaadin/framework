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
package com.vaadin.tests.layouts;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class HiddenHorizontalLayout extends TestBase {

    @Override
    protected String getDescription() {
        return "Test to verify that toggling layout visibility works properly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3183;
    }

    @Override
    public void setup() {

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        getLayout().addComponent(vl);

        final HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setHeight("30px");
        hl.addComponent(new Label("label1"));
        hl.addComponent(new Label("label2"));
        hl.addComponent(new Label("label3"));
        hl.addComponent(new Label("label4"));
        vl.addComponent(hl);

        Label l = new Label(
                "Steps to reproduce with Vaadin 6.0.1:<br/>"
                        + "1. set browser size smaller than fullscreen<br/>"
                        + "2. Refresh page with browser<br/>"
                        + "3. Click \"toggle layout visibility\"<br>"
                        + "4. Resize browser window to full <br/>"
                        + "5. Click \"toggle layout visibility\"<br/>",
                ContentMode.HTML);
        vl.addComponent(l);
        Button b = new Button("toggle layout visibility",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        hl.setVisible(!hl.isVisible());
                    }

                });
        vl.addComponent(b);
    }

}
