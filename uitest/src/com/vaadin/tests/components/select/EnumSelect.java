/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.select;

import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Tree;

public class EnumSelect extends AbstractTestUIWithLog {

    public enum Constant {
        SOME_VALUE, SOME_OTHER_VALUE, FOO, BAR;
    }

    @Override
    protected void setup(VaadinRequest request) {

        setLocale(new Locale("fi", "FI"));
        ComboBox cb = new ComboBox();
        cb.setFilteringMode(FilteringMode.CONTAINS);
        for (Constant c : Constant.values()) {
            cb.addItem(c);
        }
        addComponent(cb);

        NativeSelect ns = new NativeSelect();
        for (Constant c : Constant.values()) {
            ns.addItem(c);
        }
        addComponent(ns);

        Tree t = new Tree();
        t.addItem(Constant.SOME_OTHER_VALUE);
        t.addItem(2500.12);
        t.setParent(2500.12, Constant.SOME_OTHER_VALUE);

        addComponent(t);

    }

    @Override
    protected String getTestDescription() {
        return "Test formatting captions with enum converters in selection components";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11433;
    }

}
