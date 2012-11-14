package com.vaadin.tests.components.window;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

public class LazyWindowResize extends AbstractTestCase {

    private LegacyWindow mainWindow;
    private Window subWindow;
    private CheckBox lazyMode;
    private Log log = new Log(5);
    private CheckBox resizeListenerCheckBox;

    protected ResizeListener resizeListener = new ResizeListener() {

        @Override
        public void windowResized(ResizeEvent e) {
            log.log("Sub window resized");
        }
    };

    protected BrowserWindowResizeListener browserWindowResizeListener = new BrowserWindowResizeListener() {
        @Override
        public void browserWindowResized(BrowserWindowResizeEvent event) {
            log.log("Main window resized");
        }
    };

    private CheckBox immediateCheckBox;

    @Override
    protected String getDescription() {
        return "Check or uncheck the checkbox to use lazy or eager resize events. Lazy mode uses a small delay before recalculating layout sizes and can be used to speed up resizes in slow UIs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6715;
    }

    @Override
    public void init() {
        mainWindow = new LegacyWindow("Resize test");
        setMainWindow(mainWindow);
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        subWindow = new Window("Sub window", layout);
        subWindow.setHeight("50%");
        subWindow.setWidth("50%");
        subWindow.center();
        layout.addComponent(new Label(LoremIpsum.get(1000)));
        getMainWindow().addWindow(subWindow);

        lazyMode = new CheckBox("Lazy resize");
        lazyMode.setImmediate(true);
        lazyMode.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                setLazy(lazyMode.getValue());
            }
        });

        resizeListenerCheckBox = new CheckBox("Resize listener");
        resizeListenerCheckBox.setImmediate(true);
        resizeListenerCheckBox.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (resizeListenerCheckBox.getValue()) {
                    subWindow.addListener(resizeListener);
                    mainWindow.addListener(browserWindowResizeListener);
                } else {
                    subWindow.removeListener(resizeListener);
                    mainWindow.removeListener(browserWindowResizeListener);
                }

            }

        });
        immediateCheckBox = new CheckBox("Windows immediate");
        immediateCheckBox.setImmediate(true);
        immediateCheckBox.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                mainWindow.setImmediate(immediateCheckBox.getValue());
                subWindow.setImmediate(immediateCheckBox.getValue());
            }

        });
        mainWindow.addComponent(lazyMode);
        mainWindow.addComponent(resizeListenerCheckBox);
        mainWindow.addComponent(immediateCheckBox);
        mainWindow.addComponent(log);
        mainWindow.addComponent(new Label("<br/><br/>", ContentMode.HTML));
        mainWindow.addComponent(new Label(LoremIpsum.get(10000)));

        setLazy(false);
    }

    private void setLazy(boolean b) {
        mainWindow.setResizeLazy(b);
        subWindow.setResizeLazy(b);
    }

}
