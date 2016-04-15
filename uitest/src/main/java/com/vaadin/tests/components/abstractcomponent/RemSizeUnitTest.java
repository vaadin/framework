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

package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WebBrowser;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class RemSizeUnitTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("My height is 10.5 x 5  rem");
        label.setHeight("5rem");
        label.setWidth(10.5f, Unit.REM);

        // Rem not supported in ie8, fake using pixels
        WebBrowser webBrowser = getPage().getWebBrowser();
        if (webBrowser.isIE() && webBrowser.getBrowserMajorVersion() == 8) {
            label.setHeight("80px");
            label.setWidth("168px");
        }

        addComponent(label);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that REM units are properly applied to the DOM";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11279);
    }

}
