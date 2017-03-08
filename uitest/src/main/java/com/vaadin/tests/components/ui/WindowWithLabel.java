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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowWithLabel extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setContent(new Label("UI"));
        Window window = new Window("A window");
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Resize the window. It should work.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10375;
    }

}
