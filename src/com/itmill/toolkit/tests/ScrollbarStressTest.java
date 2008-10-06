package com.itmill.toolkit.tests;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class ScrollbarStressTest extends Application {

    final Window main = new Window("Scrollbar Stress Test");

    final Panel panel = new Panel("Panel");
    final Window subwindow = new Window("Subwindow");

    final OptionGroup width = new OptionGroup("Width");
    final OptionGroup height = new OptionGroup("Height");

    @Override
    public void init() {
        setTheme("tests-tickets");
        setMainWindow(main);
        createControlWindow();
        subwindow.setWidth("400px");
        subwindow.setHeight("400px");
    }

    private void createControlWindow() {
        final OptionGroup context = new OptionGroup("Context");
        context.addItem("Main window");
        context.addItem("Subwindow");
        context.addItem("Panel");
        context.setValue("Main window");

        width.addItem("100%");
        width.addItem("50%");
        width.addItem("150%");
        width.addItem("100px");
        width.addItem("500px");
        width.setValue("100%");

        height.addItem("100%");
        height.addItem("50%");
        height.addItem("150%");
        height.addItem("100px");
        height.addItem("500px");
        height.setValue("100%");

        final Button set = new Button("Set", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (context.getValue() == "Main window") {
                    drawInMainWindow();
                } else if (context.getValue() == "Subwindow") {
                    drawInSubwindow();
                } else if (context.getValue() == "Panel") {
                    drawInPanel();
                }
            }
        });

        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.addComponent(context);
        ol.addComponent(width);
        ol.addComponent(height);
        ol.addComponent(set);
        ol.setSpacing(true);
        ol.setMargin(true);

        Window controller = new Window("Controller");
        controller.setLayout(ol);
        main.addWindow(controller);
    }

    private void drawInPanel() {
        OrderedLayout ol = new OrderedLayout();
        ol.setSizeFull();
        main.setLayout(ol);
        ol.addComponent(panel);

        ol = new OrderedLayout();
        ol.setSizeFull();
        panel.setSizeFull();
        panel.setLayout(ol);

        Label l = new Label("Label");
        l.setWidth((String) width.getValue());
        l.setHeight((String) height.getValue());
        l.setStyleName("no-padding");

        ol.addComponent(l);
        main.removeWindow(subwindow);
    }

    private void drawInSubwindow() {
        main.removeAllComponents();
        OrderedLayout ol = new OrderedLayout();
        ol.setSizeFull();
        Label l = new Label("Label");
        l.setWidth((String) width.getValue());
        l.setHeight((String) height.getValue());
        l.setStyleName("no-padding");

        ol.addComponent(l);
        subwindow.setLayout(ol);
        main.addWindow(subwindow);
    }

    private void drawInMainWindow() {
        OrderedLayout ol = new OrderedLayout();
        ol.setSizeFull();
        main.setLayout(ol);

        Label l = new Label("Label");
        l.setWidth((String) width.getValue());
        l.setHeight((String) height.getValue());
        l.setStyleName("no-padding");

        ol.addComponent(l);
        main.removeWindow(subwindow);
    }
}
