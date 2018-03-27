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
package com.vaadin.tests.widgetset.server.gwtrpc;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;

/**
 * Test the GWT RPC with Vaadin DevMode. See #11709.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Widgetset(TestingWidgetSet.NAME)
public class GwtRpc extends AbstractTestUI {

    /**
     * Id of the button triggering the test case.
     */
    public final static String BUTTON_ID = "gwtRpcButton";

    @Override
    protected void setup(VaadinRequest request) {
        GwtRpcButton button = new GwtRpcButton();
        button.setId(BUTTON_ID);
        button.setCaption("Press me");

        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Cannot call RPC in development mode";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11709;
    }

}
