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
package com.vaadin.tests.components.textarea;

import com.vaadin.event.UIEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * Ticket #14080
 *
 * - The bug happen on push event.<br/>
 * - The changes in the DOM are css related.<br/>
 * - It seems like when the class attribute is set on push, the textarea revert
 * to the height defined by the rows attribute.<br/>
 * - The size is reseted on onStateChange where the size is set to the one from
 * the state. And it's because, when the user changes the text, at the next poll
 * the state will confirm the change of the text, but the width and height
 * didn't change in the state either client or server before the fix.
 *
 * @since
 * @author Vaadin Ltd
 */
public class TextAreaSizeResetted extends AbstractTestUI {

    public static final int TEXTAREAHEIGHT = 200;
    public static final int TEXTAREAWIDTH = 200;

    CssLayout layout = new CssLayout() {
        @Override
        protected String getCss(Component c) {
            if (c instanceof TextArea) {
                return "resize:both";
            }

            return super.getCss(c);
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        setPollInterval(500); // Short polling like 100ms jams up the TestBench
                              // waitForVaadin -functionality.

        final Label pollIndicator = new Label();
        pollIndicator.setId("pollIndicator");

        final TextField textField = new TextField("height");

        final TextArea textArea = new TextArea();
        textArea.setHeight(TEXTAREAHEIGHT + "px");
        textArea.setWidth(TEXTAREAWIDTH + "px");
        textArea.setValue("This is a text.");

        Button button = new Button("Change Height", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                textArea.setHeight(textField.getValue());
            }
        });

        addComponent(layout);

        layout.addComponent(textArea);
        layout.addComponent(textField);
        layout.addComponent(button);
        layout.addComponent(pollIndicator);

        addPollListener(new UIEvents.PollListener() {
            @Override
            public void poll(UIEvents.PollEvent event) {
                pollIndicator.setValue(String.valueOf(System
                        .currentTimeMillis()));
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "TextArea width/height change when user resize it, change the text then a poll come.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14080;
    }

}
