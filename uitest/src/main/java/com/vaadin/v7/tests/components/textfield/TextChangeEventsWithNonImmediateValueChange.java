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
package com.vaadin.v7.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.TextField;

public class TextChangeEventsWithNonImmediateValueChange extends TestBase {
    Log l = new Log(5);

    @Override
    protected void setup() {

        TextField tf = new TextField("Default");

        TextChangeListener inputEventListener = new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                l.log("Text change event, text content currently:'"
                        + event.getText() + "' Cursor at index:"
                        + event.getCursorPosition());
            }
        };

        tf.addTextChangeListener(inputEventListener);

        // tf.setImmediate(true); // works when this is set

        tf.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                l.log("Value change: '" + event.getProperty().getValue() + "'");
            }
        });

        getLayout().addComponent(tf);

        getLayout().addComponent(l);
    }

    @Override
    protected String getDescription() {
        return "Type a, pause for a second, type ENTER,  type a. Text field should not forget the last textchange event right after valuechange (enter)."
                + "<br />Then press backspace. The text field should send a text change event even though the text in the field is the same as the field's value";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6866;
    }

}
