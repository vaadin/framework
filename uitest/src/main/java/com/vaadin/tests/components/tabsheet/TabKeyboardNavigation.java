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
package com.vaadin.tests.components.tabsheet;

import java.util.ArrayList;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

/**
 * Test if the click and key tab selection in a tabsheet generate the correct
 * focus/blur events.
 *
 * The solution was broken in ticket (#14304)
 *
 * @since
 * @author Vaadin Ltd
 */
public class TabKeyboardNavigation extends AbstractReindeerTestUI {

    int index = 1;
    ArrayList<Component> tabs = new ArrayList<>();
    TabSheet ts = new TabSheet();
    Log focusblur = new Log(10);

    @Override
    protected void setup(VaadinRequest request) {
        ts.setWidth("500px");
        ts.setHeight("500px");

        ts.addFocusListener(new FocusListener() {
            @Override
            public void focus(FocusEvent event) {
                focusblur.log("Tabsheet focused!");
            }
        });

        ts.addBlurListener(new BlurListener() {
            @Override
            public void blur(BlurEvent event) {
                focusblur.log("Tabsheet blurred!");
            }
        });

        for (int i = 0; i < 5; ++i) {
            addTab();
        }

        Button addTab = new Button("Add a tab", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addTab();
            }
        });
        Button focus = new Button("Focus tabsheet", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ts.focus();
            }
        });

        addComponent(addTab);
        addComponent(focus);

        TextField tf = new TextField();
        addComponent(tf);
        addComponent(focusblur);
        addComponent(ts);
        tf = new TextField();
        addComponent(tf);
    }

    @Override
    protected String getTestDescription() {
        return "The tab bar should be focusable and arrow keys should switch tabs. The del key should close a tab if closable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5100;
    }

    public final static String LABEL_ID = "sheetLabel";

    public final static String labelID(int index) {
        return LABEL_ID + index;
    }

    private Tab addTab() {
        VerticalLayout content = new VerticalLayout();
        content.setMargin(false);
        content.setSpacing(false);
        tabs.add(content);
        Label label = new Label("Tab " + index);
        label.setId(labelID(index));
        content.addComponent(label);
        content.addComponent(new TextField());
        Tab tab = ts.addTab(content, "Tab " + index, null);
        if (index == 2) {
            tab.setClosable(true);
        }
        if (index == 4) {
            tab.setEnabled(false);
        }
        index++;
        return tab;
    }
}
