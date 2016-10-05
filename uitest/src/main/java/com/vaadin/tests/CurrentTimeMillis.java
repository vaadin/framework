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
package com.vaadin.tests;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

/**
 * Test UI (empty) to check high resolution time availability in browser.
 *
 * @author Vaadin Ltd
 */
public class CurrentTimeMillis extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // no need to add anything
    }

    @Override
    protected Integer getTicketNumber() {
        return 14716;
    }

    @Override
    protected String getTestDescription() {
        return "Use high precision time is available instead of Date.getTime().";
    }
}
