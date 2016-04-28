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
package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@Push(transport = Transport.STREAMING)
public class StreamingReconnectWhilePushing extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            private Label label;

            @Override
            public void buttonClick(ClickEvent event) {
                if (label == null) {
                    label = new Label();
                    label.setValue(getString(1000000));
                    addComponent(label);
                } else {
                    label.setValue("." + label.getValue());
                }

            }
        });
        addComponent(button);

    }

    protected String getString(int len) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % 100 == 0) {
                b.append("\n");
            } else {
                b.append('A');
            }

        }
        return b.toString();
    }

    @Override
    protected String getTestDescription() {
        return "Each push of the button sends about 1MB to the client. Press it a couple of times and a spinner will appear forever if reconnecting does not work.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13435;
    }

}
