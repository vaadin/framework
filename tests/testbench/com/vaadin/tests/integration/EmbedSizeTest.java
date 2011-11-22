package com.vaadin.tests.integration;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Root;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.ResizeEvent;

public class EmbedSizeTest extends TestBase {

    private Log log = new Log(10);

    @Override
    protected void setup() {
        Root mainWindow = getMainWindow();
        mainWindow.setSizeUndefined();
        mainWindow.getContent().setSizeUndefined();
        mainWindow.setImmediate(true);

        CheckBox lazyCheckBox = new CheckBox("Lazy resize",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        boolean resizeLazy = Boolean.TRUE == event.getButton()
                                .getValue();
                        getMainWindow().setResizeLazy(resizeLazy);
                        log.log("Resize lazy: " + resizeLazy);
                    }
                });
        lazyCheckBox.setValue(Boolean.FALSE);
        lazyCheckBox.setImmediate(true);
        addComponent(lazyCheckBox);

        addComponent(log);
        mainWindow.addListener(new Window.ResizeListener() {
            public void windowResized(ResizeEvent e) {
                Window window = e.getWindow();
                log.log("Resize event: " + window.getWidth() + " x "
                        + window.getHeight());
            }
        });
    }

    @Override
    protected String getDescription() {
        return "Resizing the browser window should send consistent resize events to the server even when the application is embedded";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7923);
    }

}
