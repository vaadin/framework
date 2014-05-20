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
package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ComboBoxCursorPositionReset extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull();
        setContent(root);

        ComboBox combo = new ComboBox();
        combo.setImmediate(true);
        root.addComponent(combo);
        combo.addItem("Hello World");
        combo.addItem("Please click on the text");

        combo.setValue("Please click on the text");
        Label gap = new Label();
        root.addComponent(gap);
        root.setExpandRatio(gap, 1);

    }

    @Override
    protected String getTestDescription() {
        return "Clicking on the text in the ComboBox should position the caret where you clicked, not cause it to jump to the start or the end";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11152;
    }

}
