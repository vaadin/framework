package com.vaadin.tests.components.window;

import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class WindowTwinColSelect extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TwinColSelect<String> testScroll = new TwinColSelect<>();
        testScroll.setItems(Arrays.asList("Option1", "Option2"));
        testScroll.setRows(10);
        testScroll.setSizeFull();
        testScroll.setHeightUndefined();

        Window window = new Window();
        window.setHeight("200px");
        window.setWidth("400px");
        window.setModal(true);
        window.setContent(testScroll);
        addWindow(window);
        window.center();
    }

    @Override
    protected String getTestDescription() {
        return "Scroll bar shouldn't interfere with how "
                + "full-size TwinColSelect contents are displayed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10652;
    }
}
