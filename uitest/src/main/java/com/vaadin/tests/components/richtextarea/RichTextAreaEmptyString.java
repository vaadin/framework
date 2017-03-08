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

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaEmptyString extends TestBase {

    @Override
    protected String getDescription() {
        return "Test the value of a rich text area. Visually empty area should return \"\"";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8004;
    }

    @Override
    protected void setup() {
        final RichTextArea area = new RichTextArea();

        final Label l = new Label(area.getValue(), ContentMode.PREFORMATTED);
        l.setCaption("Value recieved from RichTextArea:");

        final Button b = new Button("get area value", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l.setValue(area.getValue());
            }
        });

        addComponent(area);
        addComponent(b);
        addComponent(l);
    }

}
