package com.vaadin.tests.server.component.window;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowTest {

    public Window window;

    @Before
    public void setup() {
        window = new Window();
    }

    @Test
    public void testAssistiveDescription() {
        Label l1 = new Label("label 1");
        Button b2 = new Button("button 2");
        window.setAssistiveDescription(l1, b2);

        assertEquals(2, window.getAssistiveDescription().length);
        assertEquals(l1, window.getAssistiveDescription()[0]);
        assertEquals(b2, window.getAssistiveDescription()[1]);

        // Modifying return value must not change actual value
        window.getAssistiveDescription()[0] = null;

        assertEquals(2, window.getAssistiveDescription().length);
        assertEquals(l1, window.getAssistiveDescription()[0]);
        assertEquals(b2, window.getAssistiveDescription()[1]);

    }

    @Test
    public void testSetPosition() {
        window.setPosition(100, 200);
        assertEquals(100, window.getPositionX());
        assertEquals(200, window.getPositionY());
    }
}
