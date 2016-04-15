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
package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;

/**
 * Test UI for $v-textfield-bevel value in TextField component.
 * 
 * @author Vaadin Ltd
 */
@Theme("tests-valo-textfield-bevel")
public class TextFieldBevel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TextField field = new TextField();
        addComponent(field);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14634;
    }

    @Override
    protected String getTestDescription() {
        return "Set v-bevel to 'false' should unset 'v-textfield-bevel' value.";
    }

    @Theme("valo")
    public static class ValoDefaultTextFieldBevel extends TextFieldBevel {

    }

}
