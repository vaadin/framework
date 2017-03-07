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

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("serial")
public class ScrollbarsInNestedTabsheets extends TestBase {

    @Override
    public void setup() {
        setTheme("chameleon");
        final Label l = new Label("Select Sub Tab 2");
        final TabSheet t = new TabSheet();
        final TabSheet t2 = getTabSheet();
        t.addTab(t2, "Main Tab");
        addComponent(l);
        addComponent(t);
    }

    private TabSheet getTabSheet() {
        final TabSheet t = new TabSheet();
        t.addTab(getDummyLayout1(), "Sub Tab 1");
        t.addTab(getDummyLayout2(), "Sub Tab 2");

        return t;
    }

    private Layout getDummyLayout1() {
        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new TestDateField("Date"));

        return l;
    }

    private Layout getDummyLayout2() {
        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new TestDateField("Date"));
        l.addComponent(new TextField("TextField"));

        return l;
    }

    @Override
    protected String getDescription() {
        return "Nested tabsheets show unwanted scrollbars with Chameleon theme when the inner tabsheet is resized";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8625;
    }

}
