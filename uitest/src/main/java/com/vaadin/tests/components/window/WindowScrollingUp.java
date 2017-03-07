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
package com.vaadin.tests.components.window;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.v7.ui.Table;

public class WindowScrollingUp extends AbstractTestCase {

    @Override
    protected String getDescription() {
        return "Scroll down, click 'up' and the view should scroll to the top";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4206;
    }

    @Override
    public void init() {
        Table table = new Table();
        table.setPageLength(50);

        final Button up = new Button("up");
        up.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                up.getUI().setScrollTop(0);
            }
        });

        setMainWindow(new LegacyWindow(""));
        getMainWindow().addComponent(table);
        getMainWindow().addComponent(up);

    }
}
