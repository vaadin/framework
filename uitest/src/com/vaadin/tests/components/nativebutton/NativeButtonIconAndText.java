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
package com.vaadin.tests.components.nativebutton;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeButton;

public class NativeButtonIconAndText extends AbstractTestUI implements
        ClickListener {

    static final String UPDATED_ALTERNATE_TEXT = "Now has alternate text";
    static final String INITIAL_ALTERNATE_TEXT = "Initial alternate text";
    static final String BUTTON_TEXT = "buttonText";
    static final String BUTTON_TEXT_ICON = "buttonTextIcon";
    static final String BUTTON_TEXT_ICON_ALT = "buttonTextIconAlt";
    static final String NATIVE_BUTTON_TEXT = "nativeButtonText";
    static final String NATIVE_BUTTON_TEXT_ICON = "nativeButtonTextIcon";
    static final String NATIVE_BUTTON_TEXT_ICON_ALT = "nativeButtonTextIconAlt";

    @Override
    protected void setup(VaadinRequest request) {
        Button buttonText = new Button("Only text");

        Button buttonTextIcon = new Button("Text icon");
        buttonTextIcon.setIcon(new ThemeResource("../runo/icons/64/ok.png"));

        Button buttonTextIconAlt = new Button("Text icon alt");
        buttonTextIconAlt.setIcon(new ThemeResource(
                "../runo/icons/64/cancel.png"));
        buttonTextIconAlt.setIconAlternateText(INITIAL_ALTERNATE_TEXT);

        buttonText.addClickListener(this);
        buttonTextIcon.addClickListener(this);
        buttonTextIconAlt.addClickListener(this);

        buttonText.setId(BUTTON_TEXT);
        buttonTextIcon.setId(BUTTON_TEXT_ICON);
        buttonTextIconAlt.setId(BUTTON_TEXT_ICON_ALT);

        addComponent(buttonText);
        addComponent(buttonTextIcon);
        addComponent(buttonTextIconAlt);

        NativeButton nativeButtonText = new NativeButton("Only text");

        NativeButton nativeButtonTextIcon = new NativeButton("Text icon");
        nativeButtonTextIcon.setIcon(new ThemeResource(
                "../runo/icons/64/ok.png"));

        NativeButton nativeButtonTextIconAlt = new NativeButton("Text icon alt");
        nativeButtonTextIconAlt.setIcon(new ThemeResource(
                "../runo/icons/64/cancel.png"));
        nativeButtonTextIconAlt.setIconAlternateText(INITIAL_ALTERNATE_TEXT);

        nativeButtonText.addClickListener(this);
        nativeButtonTextIcon.addClickListener(this);
        nativeButtonTextIconAlt.addClickListener(this);

        nativeButtonText.setId(NATIVE_BUTTON_TEXT);
        nativeButtonTextIcon.setId(NATIVE_BUTTON_TEXT_ICON);
        nativeButtonTextIconAlt.setId(NATIVE_BUTTON_TEXT_ICON_ALT);

        addComponent(nativeButtonText);
        addComponent(nativeButtonTextIcon);
        addComponent(nativeButtonTextIconAlt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Click the buttons to toggle icon alternate text";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12780;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.
     * ClickEvent)
     */
    @Override
    public void buttonClick(ClickEvent event) {
        Button b = event.getButton();
        String was = b.getIconAlternateText();
        if (was == null || was.isEmpty()) {
            b.setIconAlternateText(UPDATED_ALTERNATE_TEXT);
        } else {
            b.setIconAlternateText(null);
        }

    }

}
