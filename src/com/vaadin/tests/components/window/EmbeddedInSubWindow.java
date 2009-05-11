package com.vaadin.tests.components.window;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

public class EmbeddedInSubWindow extends TestBase {

    @Override
    protected String getDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        Window zoom = new Window("Image Preview");
        zoom.setSizeUndefined();
        zoom.getLayout().setSizeUndefined();

        String res = "icons/640ok.png";
        Embedded imagePreview = new Embedded("", new ThemeResource(res));
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
