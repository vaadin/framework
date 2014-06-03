/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

/**
 * Test UI for issue #13488, where scrolling to the next page with pagelength 0
 * would break the rendering of any page except the first.
 * 
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class ComboboxPageLengthZeroScroll extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox combobox = new ComboBox("New items enabled:");
        combobox.setPageLength(0);

        for (int i = 0; i++ < 10;) {
            combobox.addItem("1 AMERICAN SAMOA " + i);
            combobox.addItem("ANTIGUA AND BARBUDA " + i);
        }

        getLayout().addComponent(combobox);
        getLayout().addComponent(new Button("dummy"));
    }

    @Override
    protected String getTestDescription() {
        return "Scrolling with pagelength == 0 previously resulted in broken style, should be fixed now";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13488;
    }

}
