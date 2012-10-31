package com.vaadin.tests.integration;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;

public class EmbedSizeTest extends TestBase {

    private Log log = new Log(10);

    @Override
    protected void setup() {
        LegacyWindow mainWindow = getMainWindow();
        mainWindow.setSizeUndefined();
        mainWindow.getContent().setSizeUndefined();
        mainWindow.setImmediate(true);

        CheckBox lazyCheckBox = new CheckBox("Lazy resize");
        lazyCheckBox.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                CheckBox cb = (CheckBox) event.getProperty();
                Boolean resizeLazy = cb.getValue();
                getMainWindow().setResizeLazy(resizeLazy);
                log.log("Resize lazy: " + resizeLazy);
            }
        });
        lazyCheckBox.setValue(Boolean.FALSE);
        lazyCheckBox.setImmediate(true);
        addComponent(lazyCheckBox);

        addComponent(log);
        mainWindow.addListener(new Page.BrowserWindowResizeListener() {
            @Override
            public void browserWindowResized(BrowserWindowResizeEvent event) {
                log.log("Resize event: " + event.getWidth() + " x "
                        + event.getHeight());
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
