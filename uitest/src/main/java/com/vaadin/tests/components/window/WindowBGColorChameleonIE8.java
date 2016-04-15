package com.vaadin.tests.components.window;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
@Theme("chameleon")
public class WindowBGColorChameleonIE8 extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Window window = new Window();
        window.setCaption("Window");
        window.setModal(true);
        window.setClosable(true);
        window.setDraggable(true);
        window.setWidth("400px");
        window.setHeight("300px");
        window.center();
        final UI ui = UI.getCurrent();
        ui.addWindow(window);
    }
}