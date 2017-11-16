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
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class RichTextAreaRelativeHeightResize extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setHeight("300px");

        RichTextArea richTextArea = new RichTextArea();
        richTextArea.setSizeFull();
        layout.addComponent(richTextArea);

        addComponent(layout);
        addComponent(new Button("Increase height",
                event -> layout.setHeight("400px")));

    }

    @Override
    protected String getTestDescription() {
        return "Tests that a RichTextArea with dynamic height "
                + "updates its editor elements height on resize";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11320;
    }

}
