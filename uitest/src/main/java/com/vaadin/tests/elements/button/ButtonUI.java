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
package com.vaadin.tests.elements.button;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 */
@SuppressWarnings("serial")
public class ButtonUI extends AbstractTestUI {

    public static final String TEXT_FIELD_ID = "testTextfield";
    public static final String LABEL_ID = "testLabel";
    public static String QUITE_BUTTON_ID = "quiteButton";
    public static String QUITE_BUTTON_NO_CAPTION_ID = "quiteButton2";
    public static String NORMAL_BUTTON_ID = "normalButton";

    final TextField testedField = new TextField();
    final Label testedLabel = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        testedField.setId(TEXT_FIELD_ID);
        addComponent(testedField);
        testedLabel.setId(LABEL_ID);
        addComponent(testedLabel);

        testedField.setValue("");

        Button quiteButton = new Button("Quite Button");
        quiteButton.setId(QUITE_BUTTON_ID);
        quiteButton.addStyleName(ValoTheme.BUTTON_QUIET);
        addListener(quiteButton, "Clicked");

        Button quiteButtonNoCaption = new Button("");
        quiteButtonNoCaption.setId(QUITE_BUTTON_NO_CAPTION_ID);
        quiteButtonNoCaption.addStyleName(ValoTheme.BUTTON_QUIET);
        quiteButtonNoCaption.setIcon(FontAwesome.ANDROID);
        addListener(quiteButtonNoCaption, "Clicked");

        addComponent(quiteButton);
        addComponent(quiteButtonNoCaption);
        addComponent(addButtonWithDelay());

    }

    private Button addButtonWithDelay() {
        Button btn = new Button();
        btn.setId(NORMAL_BUTTON_ID);
        btn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                testedField.setValue("Clicked");
                testedLabel.setValue("Clicked");
            }
        });
        return btn;
    }

    private void addListener(Button button, final String clickEventText) {
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                testedField.setValue(clickEventText);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Test button click, for button with ValoTheme.BUTTON_QUIET style";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16346;
    }

}
