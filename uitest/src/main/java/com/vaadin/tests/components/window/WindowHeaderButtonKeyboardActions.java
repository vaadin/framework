package com.vaadin.tests.components.window;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class WindowHeaderButtonKeyboardActions extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("Open window");
        button.setId("firstButton");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Window window = new Window("WINDOW");
                window.setContent(new Label("Inside window"));
                window.setHeight("100px");
                window.setId("testWindow");
                window.addCloseListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(Window.CloseEvent e) {

                    }
                });
                addWindow(window);
            }
        });
        addComponent(button);

    }

}
