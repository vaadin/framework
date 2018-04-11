package com.vaadin.tests.components.window;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ModalWindowFocus extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest req) {

        Button button = new Button("Open windows");
        button.setId("firstButton");
        addComponent(button);
        button.addClickListener(event -> {
            Window w = new Window("This is first window");
            w.setModal(true);
            addWindow(w);

            Window w2 = new Window("This is second window");
            w2.setModal(true);
            addWindow(w2);

            HorizontalLayout lay = new HorizontalLayout();
            Button buttonInWindow = new Button("Open window");
            buttonInWindow.setId("windowButton");
            lay.addComponent(buttonInWindow);
            w2.setContent(lay);

            buttonInWindow.addClickListener(clickEvent -> {
                Window w3 = new Window("This is third window");
                w3.setModal(true);
                w3.setId("window3");
                addWindow(w3);
            });
        });
        Button button2 = new Button(
                "Open unclosable and unresizable modal window");
        addComponent(button2);
        button2.setId("modalWindowButton");
        button2.addClickListener(event -> {
            Window modalWindow = new Window("Modal window");
            modalWindow.setModal(true);
            modalWindow.setClosable(false);
            modalWindow.setResizable(false);
            VerticalLayout vl = new VerticalLayout();
            TextField tf = new TextField("Textfield");
            tf.setId("focusfield");
            tf.addFocusListener(e -> tf.setValue("this has been focused"));
            TextField tf2 = new TextField("Another Textfield");
            tf2.focus();
            vl.addComponents(tf, tf2);
            modalWindow.setContent(vl);
            addWindow(modalWindow);
        });

    }

    @Override
    protected String getTestDescription() {
        return "Topmost modal window should be focused on opening "
                + "and on closing an overlying window";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17021;
    }

}
