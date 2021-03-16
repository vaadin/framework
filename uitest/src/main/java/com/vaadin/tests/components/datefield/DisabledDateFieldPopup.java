/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
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

public class DisabledDateFieldPopup extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateField field = new DateField();
        field.setEnabled(false);
        addComponent(field);
    }

    @Override
    protected String getTestDescription() {
        return "Don't open popup calendar if datefield is disabled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13508;
    }

}
