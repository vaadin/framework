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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class UITabIndex extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Set tabIndex to -1", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(-1);
            }
        });
        addButton("Set tabIndex to 0", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(0);
            }
        });
        addButton("Set tabIndex to 1", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(1);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Tests tab index handling for UI";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11129;
    }

}
