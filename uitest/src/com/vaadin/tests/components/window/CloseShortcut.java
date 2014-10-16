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
package com.vaadin.tests.components.window;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Tests close shortcuts for Window.
 * 
 * @author Vaadin Ltd
 */
public class CloseShortcut extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Window w = new Window();
        w.setWidth("300px");
        w.setHeight("300px");
        w.center();
        addWindow(w);
        w.focus();

        // add textfield to the window to give TestBench something to send the
        // keys to
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        TextField textField = new TextField();
        textField.setSizeFull();
        content.addComponent(textField);
        w.setContent(content);

        final CheckBox cbDefault = new CheckBox("Use default (ESC) shortcut");
        cbDefault.setId("default");
        addComponent(cbDefault);
        final CheckBox cbOther = new CheckBox("Use R shortcut");
        cbOther.setId("other");
        addComponent(cbOther);
        final CheckBox cbCtrl = new CheckBox("Use CTRL+A shortcut");
        cbCtrl.setId("control");
        addComponent(cbCtrl);
        final CheckBox cbShift = new CheckBox("Use SHIFT+H shortcut");
        cbShift.setId("shift");
        addComponent(cbShift);

        cbOther.setValue(true);
        cbCtrl.setValue(true);
        cbShift.setValue(true);

        Property.ValueChangeListener listener = new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (Boolean.TRUE.equals(cbDefault.getValue())) {
                    w.resetCloseShortcuts();
                } else {
                    w.removeCloseShortcuts();
                }
                if (Boolean.TRUE.equals(cbOther.getValue())) {
                    w.addCloseShortcut(KeyCode.R);
                }
                if (Boolean.TRUE.equals(cbCtrl.getValue())) {
                    w.addCloseShortcut(KeyCode.A, ModifierKey.CTRL);
                }
                if (Boolean.TRUE.equals(cbShift.getValue())) {
                    w.addCloseShortcut(KeyCode.H, ModifierKey.SHIFT);
                }
            }
        };
        cbDefault.addValueChangeListener(listener);
        cbOther.addValueChangeListener(listener);
        cbCtrl.addValueChangeListener(listener);
        cbShift.addValueChangeListener(listener);

        cbDefault.setValue(true); // trigger value change

        Button button = new Button("Reopen window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                w.close();
                addWindow(w);
                w.focus();
            }
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "It should be possible to have multiple shortcuts at the same time, and to remove the default shortcut ESC.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14843;
    }
}
