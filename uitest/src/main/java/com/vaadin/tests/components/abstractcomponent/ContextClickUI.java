/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ContextClickUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final ContextClickListener listener = new ContextClickListener() {

            @Override
            public void contextClick(ContextClickEvent event) {
                log("Received context click at (" + event.getClientX() + ", "
                        + event.getClientY() + ")");
            }
        };
        getUI().addContextClickListener(listener);

        addComponent(new Button("Remove listener", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getUI().removeContextClickListener(listener);
            }
        }));
    }
}
