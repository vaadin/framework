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

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;

public class CheckboxIcon extends TestBase {

    @Override
    protected String getDescription() {
        return "The icon of a Checkbox component should have the same cursor as the text and should be clickable. The tooltip should appear when hovering the checkbox, the icon or the caption.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setup() {
        CheckBox checkbox = new CheckBox("A checkbox");
        checkbox.setIcon(new ThemeResource("../runo/icons/32/calendar.png"));
        checkbox.setDescription("Tooltip for checkbox");

        addComponent(checkbox);
    }

}
