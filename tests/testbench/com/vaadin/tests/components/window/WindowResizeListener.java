package com.vaadin.tests.components.window;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Root;
import com.vaadin.ui.Root.BrowserWindowResizeEvent;
import com.vaadin.ui.Window;

public class WindowResizeListener extends TestBase {

    @Override
    protected String getDescription() {
        return "Size changes from windows (both sub "
                + "and browsers level) should get back to server."
                + " If size changes, a separate server side event should occur.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    Window subwin = new ResizeListenerWindow();

    @Override
    protected void setup() {

        final Label l = new Label();
        getLayout().addComponent(l);

        getMainWindow().addListener(new Root.BrowserWindowResizeListener() {
            public void browserWindowResized(BrowserWindowResizeEvent event) {
                l.setValue("Current browser window size: "
                        + getMainWindow().getBrowserWindowWidth() + " x "
                        + getMainWindow().getBrowserWindowHeight());
            }
        });

        CheckBox subwindow = new CheckBox("show subwindow");
        subwindow.setImmediate(true);
        subwindow.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                if ((Boolean) event.getProperty().getValue()) {
                    getMainWindow().addWindow(subwin);
                } else {
                    getMainWindow().removeWindow(subwin);
                }
            }
        });
        getLayout().addComponent(subwindow);

        CheckBox immediate = new CheckBox("immediate");
        immediate.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                boolean booleanValue = (Boolean) event.getProperty().getValue();
                getMainWindow().setImmediate(booleanValue);
                subwin.setImmediate(booleanValue);
            }
        });
        immediate.setImmediate(true);
        immediate.setValue(true);
        getMainWindow().setImmediate(true);
        subwin.setImmediate(true);
        getLayout().addComponent(immediate);

        getLayout().addComponent(new Button("Sync"));

    }
}

class ResizeListenerWindow extends Window {
    Label sizeLabel = new Label();

    public ResizeListenerWindow() {
        super("Subwindow");
        setWidth("400px");

        Layout hl = (Layout) getContent();
        hl.addComponent(new Label("Current size: "));
        hl.addComponent(sizeLabel);

        addListener(new ResizeListener() {
            public void windowResized(ResizeEvent e) {
                updateLabel();
            }
        });

        updateLabel();
    }

    public void updateLabel() {
        sizeLabel.setValue(getWidth() + getWidthUnits().getSymbol() + " x "
                + getHeight() + getHeightUnits().getSymbol());
    }
}
