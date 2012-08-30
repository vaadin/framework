package com.vaadin.tests.components;

import java.util.Arrays;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;

public class FocusFromShortcutAction extends TestBase {

    @Override
    protected void setup() {
        final Select select = new Select("Select", Arrays.asList("Option 1",
                "Option 2"));
        final TextField text = new TextField("Text");

        addComponent(select);
        addComponent(text);
        Button focusText = new Button("Focus text", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                text.focus();
            }
        });
        focusText.setClickShortcut(KeyCode.T, ModifierKey.ALT);

        addComponent(focusText);
        Button focusSelect = new Button("Focus select",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        select.focus();
                    }
                });
        focusSelect.setClickShortcut(KeyCode.S, ModifierKey.ALT);
        addComponent(focusSelect);
    }

    @Override
    protected String getDescription() {
        return "The option drop down of the Focus select should not be opened when the \"Focus select\" button is triggered by clicking it with the mouse or with the associated shortcut key Alt + S";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7539);
    }

}
