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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class WindowWithInvalidCloseListener extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window w = new Window("Close me");
        w.addCloseListener(new CloseListener() {

            @Override
            public void windowClose(CloseEvent e) {
                throw new RuntimeException(
                        "Close listener intentionally failed");
            }
        });
        addWindow(w);
    }

    @Override
    protected String getTestDescription() {
        return "The window has a close listener which throws an exception. This should not prevent the window from being closed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10779;
    }

}
