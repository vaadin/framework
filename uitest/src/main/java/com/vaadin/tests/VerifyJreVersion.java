/*
 * Copyright 2000-2020 Vaadin Ltd.
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

/**
 *
 */
package com.vaadin.tests;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class VerifyJreVersion extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String jreVersion = "Using Java " + System.getProperty("java.version")
                + " by " + System.getProperty("java.vendor");
        Label jreVersionLabel = new Label(jreVersion);
        jreVersionLabel.setId("jreVersionLabel");

        addComponent(jreVersionLabel);
    }

    @Override
    protected String getTestDescription() {
        return "Test used to detect when the JRE used to run these tests have changed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11835);
    }

}
