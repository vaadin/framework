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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class RelativeChildrenWithoutExpand extends AbstractReindeerTestUI {

    private final String loremIpsum = "This is a label without expand but with relative width that shouldn't get any space at all. ";

    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalLayout horizontalExpand = new HorizontalLayout();

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Label(getTestDescription()));
        vl.setSizeFull();

        // Replacing default AbstractTestUI content to get the right expansions
        setContent(vl);

        HorizontalLayout verticalExpand = new HorizontalLayout();
        verticalExpand.addComponent(
                new Button("Add relative child", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        horizontalExpand.addComponent(new Label(loremIpsum), 0);
                    }
                }));
        vl.addComponent(verticalExpand);
        vl.setExpandRatio(verticalExpand, 1);

        horizontalExpand.setWidth("100%");
        vl.addComponent(horizontalExpand);

        Label lblExpandRatio1 = new Label(
                "This is an expanding label that will get all of the normal space in the component.");
        horizontalExpand.addComponent(lblExpandRatio1);
        horizontalExpand.setExpandRatio(lblExpandRatio1, 1);
    }

    @Override
    protected String getTestDescription() {
        return "HorizontalLayout containing relatively sized components that are not expanded should not cause infinite layout loops when scrollbars appear. Add children until the entire space is filled up.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10222);
    }
}
