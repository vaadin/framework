package com.itmill.toolkit.tests.components.window;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Window;

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
