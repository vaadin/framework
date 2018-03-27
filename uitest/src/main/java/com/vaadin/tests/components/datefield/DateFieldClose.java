/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;

public class DateFieldClose extends AbstractTestUI {

    static final String DATEFIELD_ID = "datefield";

    @Override
    protected void setup(VaadinRequest request) {
        final DateField df = new DateField();
        df.setId(DATEFIELD_ID);
        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "A click on the button should open a calendar and a second click should close it.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14086;
    }

}
