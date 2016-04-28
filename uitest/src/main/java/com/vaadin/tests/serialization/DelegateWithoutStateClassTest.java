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

package com.vaadin.tests.serialization;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.DelegateWithoutStateClassComponent;

@Widgetset(TestingWidgetSet.NAME)
public class DelegateWithoutStateClassTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DelegateWithoutStateClassComponent c = new DelegateWithoutStateClassComponent();
        c.setRows(10);
        addComponent(c);
    }

    @Override
    protected String getTestDescription() {
        return "The height of the text area should be 10 rows if @DelegateToWidget works properly for widget subclasses even if there is no state subclass.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9561);
    }

}
