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
package com.vaadin.tests.components.richtextarea;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.RichTextArea;
import com.vaadin.v7.shared.ui.progressindicator.ProgressIndicatorServerRpc;
import com.vaadin.v7.ui.ProgressIndicator;

public class RichTextAreaUpdateWhileTyping extends AbstractReindeerTestUI {

    private RichTextArea rta;

    @Override
    protected void setup(VaadinRequest request) {

        // Progress indicator for changing the value of the RTA
        ProgressIndicator pi = new ProgressIndicator() {
            {
                registerRpc(new ProgressIndicatorServerRpc() {

                    @Override
                    public void poll() {
                        rta.markAsDirty();
                    }
                });
            }
        };
        pi.setHeight("0px");
        addComponent(pi);

        rta = new RichTextArea();
        rta.setId("rta");
        addComponent(rta);
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 11741;
    }
}
