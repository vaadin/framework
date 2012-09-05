package com.vaadin.tests.server.component.button;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Tests the public click() method.
 */
public class ButtonClick {
    private boolean clicked = false;

    @Test
    public void testClick() {
        getButton().click();
        assertEquals(clicked, true);
    }

    @Test
    public void testClickDisabled() {
        Button b = getButton();
        b.setEnabled(false);
        b.click();
        assertEquals(clicked, false);
    }

    @Test
    public void testClickReadOnly() {
        Button b = getButton();
        b.setReadOnly(true);
        b.click();
        assertEquals(clicked, false);
    }

    private Button getButton() {
        Button b = new Button();
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent ev) {
                clicked = true;
            }
        });
        return b;
    }
}
