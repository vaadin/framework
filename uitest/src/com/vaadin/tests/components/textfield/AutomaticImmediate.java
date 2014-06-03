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
package com.vaadin.tests.components.textfield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

/**
 * Test to verify fields become implicitly "immediate" when adding value change
 * listener to them.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AutomaticImmediate extends AbstractTestUIWithLog {

    /**
     * 
     */
    static final String BUTTON = "button";
    /**
     * 
     */
    static final String EXPLICIT_FALSE = "explicit-false";
    /**
     * 
     */
    static final String FIELD = "field";
    /**
     * 
     */
    static final String LISTENER_TOGGLE = "listener-toggle";

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        final TextField textField = new TextField() {

            /*
             * (non-Javadoc)
             * 
             * @see com.vaadin.ui.AbstractField#fireValueChange(boolean)
             */
            @Override
            protected void fireValueChange(boolean repaintIsNotNeeded) {
                log("fireValueChange");
                super.fireValueChange(repaintIsNotNeeded);
            }
        };
        textField.setId(FIELD);

        final ValueChangeListener listener = new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                log("Value changed: " + event.getProperty().getValue());
            }
        };

        final CheckBox checkBox = new CheckBox("Toggle listener");
        checkBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (checkBox.getValue()) {
                    textField.addValueChangeListener(listener);
                } else {
                    textField.removeValueChangeListener(listener);
                }
            }
        });
        checkBox.setId(LISTENER_TOGGLE);

        Button b = new Button(
                "setImmediate(false), sets explicitly false and causes server roundtrip",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        textField.setImmediate(false);
                    }
                });
        b.setId(EXPLICIT_FALSE);

        Button b2 = new Button("Hit server, causes server roundtrip",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                    }
                });
        b2.setId(BUTTON);

        addComponent(textField);
        addComponent(checkBox);
        addComponent(b);
        addComponent(b2);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Field should be immediate automatically if it has value change listener";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 8029;
    }

}
