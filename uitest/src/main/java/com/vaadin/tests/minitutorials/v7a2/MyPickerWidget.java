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
package com.vaadin.tests.minitutorials.v7a2;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

public class MyPickerWidget extends ComplexPanel {

    public static final String CLASSNAME = "mypicker";

    private final TextBox textBox = new TextBox();
    private final PushButton button = new PushButton("...");

    public MyPickerWidget() {
        setElement(Document.get().createDivElement());
        setStylePrimaryName(CLASSNAME);

        textBox.setStylePrimaryName(CLASSNAME + "-field");
        button.setStylePrimaryName(CLASSNAME + "-button");

        add(textBox, getElement());
        add(button, getElement());

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Calendar picker not yet supported!");
            }
        });
    }

    public void setButtonText(String buttonText, boolean adjustSpace) {
        if (buttonText == null || buttonText.length() == 0) {
            buttonText = "...";
        }
        button.setText(buttonText);

        if (adjustSpace) {
            adjustButtonSpace(button.getOffsetWidth());
        }
    }

    public void adjustButtonSpace(int width) {
        getElement().getStyle().setPaddingRight(width, Unit.PX);
        button.getElement().getStyle().setMarginRight(-width, Unit.PX);
    }
}
