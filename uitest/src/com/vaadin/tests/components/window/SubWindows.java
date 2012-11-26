package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
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

            @Override
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
        final HorizontalLayout hl = new HorizontalLayout();
        autoWideWindow = new Window("Dialog - width by contents", hl);
        hl.setSizeUndefined();
        hl.addComponent(new TextField("Field 1"));
        hl.addComponent(new TextField("Field 2"));
        hl.addComponent(new Button("Add", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(createRemoveButton());

            }

        }));

        getMainWindow().addWindow(autoWideWindow);

        {
            VerticalLayout vl = new VerticalLayout();
            vl.setMargin(true);
            Window dialog = new Window("Dialog - undefined width", vl);
            vl.addComponent(new TextField("Field 1"));

            TextField tf2 = new TextField("Field 2");
            tf2.setWidth("500px");
            vl.addComponent(tf2);
            vl.addComponent(new Button("Ok"));

            dialog.center();
            getMainWindow().addWindow(dialog);
        }

        {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            Window dialog = new Window("Dialog - width defined by content",
                    layout);
            layout.setHeight(null);
            layout.setWidth("100%");

            TextArea ta = new TextArea();
            ta.setValue("The textfield should fill the window (except margins)."
                    + "\n - Try to resize the window\n");
            ta.setRows(5);
            ta.setWidth("100%");
            layout.addComponent(ta);

            dialog.setPositionX(20);
            dialog.setPositionY(100);
            getMainWindow().addWindow(dialog);
        }

        {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            Window dialog = new Window("Dialog - size defined by content",
                    layout);
            layout.setHeight("100%");
            layout.setWidth("100%");

            TextArea ta = new TextArea();
            ta.setValue("The textfield should fill the window (except margins)."
                    + "\n - Try to resize the window\n");
            ta.setWidth("100%");
            ta.setHeight("100%");
            ta.setRows(5);
            layout.addComponent(ta);

            dialog.setPositionX(20);
            dialog.setPositionY(300);
            getMainWindow().addWindow(dialog);
        }
    }
}
