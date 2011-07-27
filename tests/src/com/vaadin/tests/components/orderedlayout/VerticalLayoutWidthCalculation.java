package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class VerticalLayoutWidthCalculation extends AbstractTestCase {
    @Override
    public void init() {
        final Window mainWindow = new Window("Vaadintest Application");
        mainWindow.addWindow(createSubWindow());
        setMainWindow(mainWindow);

    }

    private Window createSubWindow() {
        HorizontalLayout hl = new HorizontalLayout();

        VerticalLayout vlTF1 = new VerticalLayout();
        vlTF1.setSizeUndefined();
        final TextField tf1 = new TextField("Text1");
        tf1.setSizeUndefined();
        vlTF1.addComponent(tf1);
        hl.addComponent(vlTF1);

        VerticalLayout vlTF2 = new VerticalLayout();
        vlTF2.setSizeUndefined();
        final TextField tf2 = new TextField("Text2");
        tf2.setVisible(false);
        tf2.setSizeUndefined();
        vlTF2.addComponent(tf2);
        hl.addComponent(vlTF2);

        Window wnd = new Window("Test");
        wnd.getContent().setSizeUndefined();
        wnd.addComponent(hl);
        Button btn = new Button("Show/hide");
        btn.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                tf2.setVisible(!tf2.isVisible());
            }
        });
        wnd.addComponent(btn);

        return wnd;
    }

    @Override
    protected String getDescription() {
        return "The second TextField is initially invisible. Make it visible and then hide it again. You should end up with the same result as initially.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7260;
    }

}
