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
package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.InlineDateTimeField;

public class DisabledInlineDateTimeField extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        InlineDateTimeField df = new InlineDateTimeField("Disabled");
        LocalDateTime date = LocalDateTime.of(2014, 6, 5, 11, 34);
        df.setValue(date);
        df.setEnabled(false);
        addComponent(df);

        df = new InlineDateTimeField("Read-only");
        df.setValue(date);
        df.setReadOnly(true);
        addComponent(df);
    }

    @Override
    protected String getTestDescription() {
        return "Testing disabled and read-only modes of InlineDateField.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10262;
    }

}
