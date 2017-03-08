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

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutWithRelativeSizeComponentsInitiallyHidden
        extends TestBase {

    @Override
    protected String getDescription() {
        return "Size calculations fail if expanded component is relative sized "
                + "and initially invisible and when becoming visible at the "
                + "same time some other component size changes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4608;
    }

    @Override
    protected void setup() {

        VerticalLayout verticalLayout = getLayout();
        verticalLayout.setHeight("500px");

        final Label bar = new Label("Bar");
        bar.setSizeUndefined();
        final Label foobar = new Label("FooBar");
        foobar.setSizeFull();
        foobar.setVisible(false);

        bar.setHeight("100px");

        // bar.setHeight("100px");
        bar.setVisible(false);

        Button b = new Button(
                "Click to set bar visible. Button should stay visible.");
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                bar.setVisible(true);
                foobar.setVisible(true);
            }
        });

        verticalLayout.addComponent(bar);
        verticalLayout.addComponent(foobar);
        verticalLayout.setExpandRatio(foobar, 1);
        verticalLayout.addComponent(b);
    }

}
