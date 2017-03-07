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
package com.vaadin.tests.push;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

@PreserveOnRefresh
@Push
public class PushWithPreserveOnRefresh extends AbstractReindeerTestUI {

    private Log log = new Log(5);
    private int times = 0;

    @Override
    protected void setup(VaadinRequest request) {
        // Internal parameter sent by vaadinBootstrap.js,
        addComponent(new Label("window.name: " + request.getParameter("v-wn")));
        addComponent(new Label("UI id: " + getUIId()));
        addComponent(log);

        addButton("click me", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Button has been clicked " + (++times) + " times");
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Refreshing the browser window should preserve the state and push should continue to work";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13620);
    }
}
