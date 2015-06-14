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

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

public class ResponseWritingErrorHandling extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                String message = event.getThrowable().getMessage();
                log(message);
            }
        };

        Button button = new Button("Throw in beforeClientResponse") {
            private boolean throwInBeforeClientResponse = false;
            {
                addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        throwInBeforeClientResponse = true;
                        // Make sure beforeClientResponse is called
                        markAsDirty();
                    }
                });
            }

            @Override
            public void beforeClientResponse(boolean initial) {
                if (throwInBeforeClientResponse) {
                    throwInBeforeClientResponse = false;
                    throw new RuntimeException("Button.beforeClientResponse");
                }
            }
        };
        button.setErrorHandler(errorHandler);

        addComponent(button);
    }

}
