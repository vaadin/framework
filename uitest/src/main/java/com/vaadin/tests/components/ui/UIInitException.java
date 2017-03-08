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
package com.vaadin.tests.components.ui;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

public class UIInitException extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setErrorHandler(new ErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                addComponent(new Label("An exception occurred: "
                        + event.getThrowable().getMessage()));

            }
        });
        throw new RuntimeException("Catch me if you can");
    }

    @Override
    protected String getTestDescription() {
        return "Throwing an exception in application code during a browser details request should show a sensible message in the client";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8243);
    }

}
