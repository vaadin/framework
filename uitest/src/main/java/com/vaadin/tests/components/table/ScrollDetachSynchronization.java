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
package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class ScrollDetachSynchronization extends TestBase {

    @Override
    public void setup() {
        getMainWindow().setContent(buildLayout());
    }

    @Override
    protected String getDescription() {
        return "Scrolling, then detaching, a table causes out of sync on IE";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6970;
    }

    private Layout buildLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setSizeUndefined();
        Button first = new Button("First layout");
        Button second = new Button("Second layout");
        first.setId("FirstButton");
        second.setId("SecondButton");
        buttonBar.addComponent(first);
        buttonBar.addComponent(second);
        mainLayout.addComponent(buttonBar);

        final HorizontalLayout firstLayout = buildTestLayout(true);
        final HorizontalLayout secondLayout = buildTestLayout(false);

        mainLayout.addComponent(firstLayout);
        mainLayout.setExpandRatio(firstLayout, 1);

        first.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (mainLayout.getComponent(1).equals(secondLayout)) {
                    mainLayout.replaceComponent(secondLayout, firstLayout);
                    mainLayout.setExpandRatio(firstLayout, 1);
                }
            }
        });
        second.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (mainLayout.getComponent(1).equals(firstLayout)) {
                    mainLayout.replaceComponent(firstLayout, secondLayout);
                    mainLayout.setExpandRatio(secondLayout, 1);
                }
            }
        });
        return mainLayout;
    }

    private HorizontalLayout buildTestLayout(boolean first) {
        String which = first ? "First" : "Second";

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setId(which + "Layout");

        Table t = new Table();
        t.addContainerProperty("name", String.class, null);
        for (int i = 0; i < 10; i++) {
            String id = which + " " + i;
            t.addItem(new String[] { id }, id);
        }
        t.setId(which + "Table");
        t.setItemCaptionPropertyId("name");
        t.setSizeFull();

        hl.addComponent(t);

        return hl;
    }
}
