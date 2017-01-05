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
package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

/**
 * Test UI for disbaled label.
 *
 * @author Vaadin Ltd
 */
public class DisabledLabel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label enabled = new Label("enabled");
        enabled.addStyleName("my-enabled");
        addComponent(enabled);

        Label disabled = new Label("disabled");
        disabled.setEnabled(false);
        disabled.addStyleName("my-disabled");

        addComponent(disabled);
    }

    @Override
    protected Integer getTicketNumber() {
        return 15489;
    }

    @Override
    protected String getTestDescription() {
        return "Disabled label should be visibly disabled (has an opacity).";
    }

}
