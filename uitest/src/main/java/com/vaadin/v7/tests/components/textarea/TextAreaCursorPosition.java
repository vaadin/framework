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
package com.vaadin.v7.tests.components.textarea;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class TextAreaCursorPosition extends TestBase {

    private TextField cursorPosition = new TextField("Cursor position");

    @Override
    public void setup() {
        Label label = new Label(
                "Test of calculation of cursor position of TextArea");
        TextArea textArea = new TextArea();
        addListener(textArea);
        addComponent(label);
        addComponent(textArea);
        addComponent(cursorPosition);
        cursorPosition.setValue("?");
        addComponent(new Button("Force position update"));
    }

    public void addListener(AbstractField newField) {
        AbstractTextField newTextField = (AbstractTextField) newField;
        newTextField.setValueChangeMode(ValueChangeMode.EAGER);
        newTextField.addValueChangeListener(event -> {
            AbstractTextField component = (AbstractTextField) event.getSource();
            cursorPosition
                    .setValue(String.valueOf(component.getCursorPosition()));
        });
    }

    @Override
    protected String getDescription() {
        return "Writing something in the field updates the cursor position field. The position field can also be updated using the button.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7726;
    }

}
