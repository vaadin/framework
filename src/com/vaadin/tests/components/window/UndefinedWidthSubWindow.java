package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class UndefinedWidthSubWindow extends TestBase {

    @Override
    protected String getDescription() {
        return "Two windows should be shown. The width of the one in the upper left corner should be adjusted according to the contents. The centered windows width should be set according to the caption and the second textfield should be clipped.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        Window dialog = new Window("Dialog - width defined by contents",
                new HorizontalLayout());
        dialog.getContent().setSizeUndefined();
        dialog.addComponent(new TextField("Field 1"));
        dialog.addComponent(new TextField("Field 2"));
        dialog.addComponent(new Button("Ok"));

        getMainWindow().addWindow(dialog);

        Window dialog2 = new Window("Dialog - width defined by caption");
        dialog2.addComponent(new TextField("Field 1"));

        TextField tf2 = new TextField("Field 2");
        tf2.setWidth("500px");
        dialog2.addComponent(tf2);
        dialog2.addComponent(new Button("Ok"));

        dialog2.center();
        getMainWindow().addWindow(dialog2);
    }

}
