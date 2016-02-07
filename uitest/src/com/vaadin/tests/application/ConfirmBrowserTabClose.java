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
package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ConfirmBrowserTabClose extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        // To test the behavior, do
        // 1. Open the test in the browser
        // 2. Close the browser tab/window
        // 3. Choose to stay on the page after all
        // 4. Click the button
        // There should be no error
        Button b = new Button("Say hello", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Hello");
            }
        });
        addComponent(b);
        getPage().getJavaScript().eval(
                "window.addEventListener('beforeunload', function (e) {"
                        + "var confirmationMessage = 'Please stay!';"
                        + "e.returnValue = confirmationMessage;"
                        + "return confirmationMessage;" + "});");
    }
}
