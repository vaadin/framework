package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

public class SubWindowPositionUpdate extends TestBase {

    static int delay = 400;

    @Override
    protected void setup() {
        Window subWindow = new Window("Draggable sub window") {
            @Override
            public void setPositionX(int positionX) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.setPositionX(positionX);
            }
        };
        getMainWindow().addWindow(subWindow);
        ProgressIndicator pi = new ProgressIndicator();
        pi.setIndeterminate(true);
        pi.setPollingInterval(delay);
        addComponent(pi);
    }

    @Override
    protected String getDescription() {
        return "The window position should not jump inconsistently while "
                + "dragging, even though external UIDL updates are sent and "
                + "received by the progress indicator. A small delay is used "
                + "on the server side to surface the issue (" + delay + "ms).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4427;
    }

}
