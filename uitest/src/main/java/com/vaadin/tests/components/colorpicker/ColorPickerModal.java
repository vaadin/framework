package com.vaadin.tests.components.colorpicker;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ColorPickerModal extends AbstractTestUIWithLog
        implements ErrorHandler {

    @Override
    protected void setup(VaadinRequest req) {
        getSession().setErrorHandler(this);
        Window modalWindow = new Window("Modal window");
        modalWindow.setModal(true);
        VerticalLayout vl = new VerticalLayout();
        ColorPicker cp = new ColorPicker("Color Picker test", Color.GREEN);
        cp.setId("colorP");
        cp.setModal(true);
        vl.addComponent(cp);
        modalWindow.setContent(vl);
        addWindow(modalWindow);
    }

    @Override
    protected Integer getTicketNumber() {
        return 9511;
    }

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        log("Exception caught on execution with "
                + event.getClass().getSimpleName() + " : "
                + event.getThrowable().getClass().getName());

        DefaultErrorHandler.doDefault(event);
    }
}
