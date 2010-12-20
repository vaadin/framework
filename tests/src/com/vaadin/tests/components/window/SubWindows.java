package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class SubWindows extends TestBase {

    private Window autoWideWindow;

    @Override
    protected String getDescription() {
        return "Three windows should be shown. "
                + "The width of the one in the upper left corner should be adjusted according to the contents. "
                + "The centered windows width should be set according to the caption and the second textfield should be clipped. "
                + "The third window should be initially the width and height of its content and when resizing the window the content width should be updated so it is always 100% of the window.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private Component createRemoveButton() {
        Button b = new Button("Remove");
        b.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                Button b = event.getButton();
                ComponentContainer cc = (ComponentContainer) b.getParent();
                cc.removeComponent(b);
            }
        });

        return b;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setup() {
        autoWideWindow = new Window("Dialog - width by contents",
                new HorizontalLayout());
        autoWideWindow.getContent().setSizeUndefined();
        autoWideWindow.addComponent(new TextField("Field 1"));
        autoWideWindow.addComponent(new TextField("Field 2"));
        autoWideWindow.addComponent(new Button("Add", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                autoWideWindow.addComponent(createRemoveButton());

            }

        }));

        getMainWindow().addWindow(autoWideWindow);

        {
            Window dialog = new Window("Dialog - width defined by caption");
            dialog.addComponent(new TextField("Field 1"));

            TextField tf2 = new TextField("Field 2");
            tf2.setWidth("500px");
            dialog.addComponent(tf2);
            dialog.addComponent(new Button("Ok"));

            dialog.center();
            getMainWindow().addWindow(dialog);
        }

        {
            Window dialog = new Window("Dialog - width defined by content");
            dialog.getContent().setHeight(null);
            dialog.getContent().setWidth("100%");

            TextField tf = new TextField();
            tf.setValue("The textfield should fill the window (except margins)."
                    + "\n - Try to resize the window\n");
            tf.setRows(5);
            tf.setWidth("100%");
            dialog.addComponent(tf);

            dialog.setPositionX(20);
            dialog.setPositionY(100);
            getMainWindow().addWindow(dialog);
        }

        {
            Window dialog = new Window("Dialog - size defined by content");
            dialog.getContent().setHeight("100%");
            dialog.getContent().setWidth("100%");

            TextField tf = new TextField();
            tf.setValue("The textfield should fill the window (except margins)."
                    + "\n - Try to resize the window\n");
            tf.setWidth("100%");
            tf.setHeight("100%");
            tf.setRows(5);
            dialog.addComponent(tf);

            dialog.setPositionX(20);
            dialog.setPositionY(300);
            getMainWindow().addWindow(dialog);
        }
    }
}
