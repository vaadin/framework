/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Window;

/**
 * Test UI for Window attached to the UI with not content.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class WindowInUiWithNoContent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // This is requires for the test
        setContent(null);

        Window window = new Window("window");
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Client UI component should not use VWindow as a content component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13127;
    }

}
