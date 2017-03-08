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
package com.vaadin.tests.components.menubar;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;

public class MenuBarRunsOutOfBrowser extends AbstractTestCase {

    @Override
    public void init() {
        setTheme("runo");
        LegacyWindow main = new LegacyWindow("Test");
        main.setSizeFull();
        setMainWindow(main);
        main.getContent().setSizeFull();

        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Test", new ThemeResource("icons/16/calendar.png"),
                null);
        menuBar.addItem("ABC", new ThemeResource("icons/16/document.png"),
                null);
        menuBar.addItem("123", new ThemeResource("icons/16/help.png"), null);

        main.addComponent(menuBar);
        ((VerticalLayout) main.getContent()).setComponentAlignment(menuBar,
                Alignment.TOP_RIGHT);

    }

    @Override
    protected String getDescription() {
        return "The menubar should be right aligned but not run out of the browser";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5894;
    }

}
