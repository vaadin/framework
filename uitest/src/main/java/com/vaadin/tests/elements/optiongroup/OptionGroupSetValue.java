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
package com.vaadin.tests.elements.optiongroup;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroupSetValue extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        OptionGroup group = new OptionGroup();
        group.addItem("item1");
        group.addItem("item2");
        group.addItem("item3");
        addComponent(group);
    }

    @Override
    protected String getTestDescription() {
        return "Test OptionGroup element setValue() and SelectByText()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14918;
    }

}
