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

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

public class PopupViewClickShortcut extends TestBase {

    private Window sub = new Window("Table", makeTable("Subwindow", KeyCode.S));

    private Log log = new Log(5);

    @Override
    protected void setup() {
        sub.center();
        getMainWindow().addWindow(sub);
        addComponent(log);
        addComponent(new PopupView("Show popup table",
                makeTable("Popup", KeyCode.P)));
        addComponent(makeTable("Main window", KeyCode.M));
        ((ComponentContainer) sub.getContent()).addComponent(new PopupView(
                "Show popup table", makeTable("Subwindow popup", KeyCode.U)));
    }

    private ComponentContainer makeTable(final String caption, int keyCode) {
        final Table t = new Table();
        t.setSelectable(true);
        t.setHeight("200px");
        t.setWidth("200px");
        t.addContainerProperty("foo", String.class, "foo");
        for (int i = 0; i < 5; i++) {
            t.addItem(new String[] { "foo " + i }, i);
        }

        final Layout l = new VerticalLayout();
        l.setCaption(caption);
        l.setWidth(null);

        Button b = new Button(
                "Submit " + caption + " (Ctrl+Alt+" + (char) keyCode + ")",
                new Button.ClickListener() {
                    private int i = 5;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("Submitted from "
                                + event.getButton().getParent().getCaption());
                        t.addItem(new String[] { "added " + i++ }, i);
                    }
                });

        b.setClickShortcut(keyCode, ModifierKey.CTRL, ModifierKey.ALT);

        l.addComponent(t);
        l.addComponent(b);

        return l;
    }

    @Override
    protected String getDescription() {
        return "Enter ClickShortcut does not work with PopupView";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8193;
    }

}
