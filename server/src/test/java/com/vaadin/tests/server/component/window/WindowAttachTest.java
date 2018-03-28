package com.vaadin.tests.server.component.window;

import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowAttachTest {

    private static class MyUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttachUsingSetContent() {
        UI ui = new MyUI();
        ui.setContent(new Window("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToLayout() {
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Window("foo"));
    }
}
