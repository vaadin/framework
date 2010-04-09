package com.vaadin.tests.components.window;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

public class EmbeddedInSubWindow extends TestBase {

    @Override
    protected String getDescription() {
        return "The sub window contains a large icon and should be sized according to the icon. The icon contains a blue border of 10px at the outer edges. The layout in the sub window has margins enabled.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        Window zoom = new Window("Image Preview");
        zoom.setSizeUndefined();
        zoom.getContent().setSizeUndefined();

        String res = "icons/EmbeddedInSubWindow-image.png";
        Embedded imagePreview = new Embedded(null, new ThemeResource(res));
        imagePreview.setSizeUndefined();

        zoom.addComponent(imagePreview);
        zoom.setModal(true);
        zoom.setResizable(false);

        zoom.addListener(new Window.CloseListener() {
            public void windowClose(Window.CloseEvent closeEvent) {
                getMainWindow().removeWindow(closeEvent.getWindow());
            }
        });

        getMainWindow().addWindow(zoom);

    }

}
