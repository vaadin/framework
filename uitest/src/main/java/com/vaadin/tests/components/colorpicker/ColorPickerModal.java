package com.vaadin.tests.components.colorpicker;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ColorPickerModal extends AbstractTestUI {

    private org.eclipse.jetty.util.log.Logger log;

    @Override
    protected void setup(VaadinRequest req) {
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
    protected String getTestDescription() {
        return "Test that setting color picker to modal, when it's on top of modal "
                + "Window doesn't produce IllegalStateException";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9511;
    }

}
