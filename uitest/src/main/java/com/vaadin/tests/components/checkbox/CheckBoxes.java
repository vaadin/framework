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
package com.vaadin.tests.components.checkbox;

import java.util.Date;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.CheckBox;

public class CheckBoxes extends ComponentTestCase<CheckBox> {

    private ThemeResource SMALL_ICON = new ThemeResource(
            "../runo/icons/16/ok.png");
    private ThemeResource LARGE_ICON = new ThemeResource(
            "../runo/icons/64/document.png");
    private ThemeResource LARGE_ICON_NOCACHE = new ThemeResource(
            "../runo/icons/64/document.png?" + new Date().getTime());

    @Override
    protected Class<CheckBox> getTestClass() {
        return CheckBox.class;
    }

    @Override
    protected void initializeComponents() {

        setTheme("tests-tickets");
        CheckBox cb;

        cb = createCheckBox("CheckBox with normal text");
        addTestComponent(cb);

        cb = createCheckBox("CheckBox with large text");
        cb.setStyleName("large");
        addTestComponent(cb);

        cb = createCheckBox("CheckBox with normal text and small icon",
                SMALL_ICON);
        addTestComponent(cb);
        cb = createCheckBox("CheckBox with large text and small icon",
                SMALL_ICON);
        cb.setStyleName("large");
        addTestComponent(cb);

        cb = createCheckBox("CheckBox with normal text and large icon",
                LARGE_ICON);
        addTestComponent(cb);
        cb = createCheckBox("CheckBox with large text and large icon",
                LARGE_ICON_NOCACHE);
        cb.setStyleName("large");
        addTestComponent(cb);

    }

    private CheckBox createCheckBox(String caption, Resource icon) {
        CheckBox cb = createCheckBox(caption);
        cb.setIcon(icon);

        return cb;
    }

    private CheckBox createCheckBox(String caption) {
        return new CheckBox(caption);
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for CheckBoxes in different configurations";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
