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

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.MenuBar;

public class MenuItemStyleRemoved extends AbstractReindeerTestUI {

    protected static final String MENUITEM_CLASS = "v-menubar-menuitem";

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();

        MenuBar.MenuItem first = menuBar.addItem("first", null, null);
        first.addItem("first sub-item 1", null, null);
        first.addItem("first sub-item 2", null, null);
        MenuBar.MenuItem second = menuBar.addItem("second", null, null);
        second.addItem("second sub-item 2", null, null);
        second.addItem("second sub-item 2", null, null);

        addComponent(menuBar);
        addButton("Add styles", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String method = "getElementsByClassName('" + MENUITEM_CLASS
                        + "')";
                WebBrowser webBrowser = Page.getCurrent().getWebBrowser();
                if (webBrowser.isIE()
                        && webBrowser.getBrowserMajorVersion() == 8) {
                    method = "querySelectorAll('." + MENUITEM_CLASS + "')";
                }
                JavaScript.getCurrent()
                        .execute("var x=document." + method + ";"
                                + " var i; for(i=0; i < x.length; i++)"
                                + " {x[i].className += ' custom-menu-item'};");
            }
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 17242;
    }

    @Override
    protected String getTestDescription() {
        return "MenuItem's custom class names removed when hovering";
    }
}
