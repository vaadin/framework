package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class UndefinedWidthSubWindow extends TestBase {

    private Window autoWideWindow;

    @Override
    protected String getDescription() {
        return "Two windows should be shown. The width of the one in the upper left corner should be adjusted according to the contents. The centered windows width should be set according to the caption and the second textfield should be clipped.";
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

    @Override
    protected void setup() {
        autoWideWindow = new Window("Dialog - width defined by contents",
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
