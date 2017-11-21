/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.serialization;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;

public class ChangeStateWhenReattaching extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("Reattach and remove caption",
                event -> {
                    Button b = event.getButton();
                    removeComponent(b);
                    addComponent(b);
                    b.setCaption(null);
                });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking the button should remove its caption, even though it is also reattached.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10532);
    }

}
