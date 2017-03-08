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
package com.vaadin.tests.components.abstractfield;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

public class FieldFocusOnClick extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new TextField(null, "TextField"));
        addComponent(new CheckBox("CheckBox"));
        addComponent(
                new OptionGroup(null, Arrays.asList("Option 1", "Option 2")));
        addComponent(new NativeButton("NativeButton"));
    }

    @Override
    protected String getTestDescription() {
        return "Webkit doesn't focus non-text input elements when clicked";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11854;
    }
}
