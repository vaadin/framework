package com.vaadin.tests;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class FocusOutsideWindow extends AbstractTestUI {
    private boolean focusTextF = true;

    @Override
    protected void setup(VaadinRequest request) {

        Button button = new Button("Open window");
        Button focusBut = new Button("Focus TextField/DefaultFocus", e -> {
            focusTextF = !focusTextF;
        });
        button.setId("buttonOp");
        focusBut.setId("focusBut");
        final TextField textField = new TextField("Focus shoud go here");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Window window = new Window("WINDOW");
                window.setContent(new Label("Inside window"));
                window.setHeight("100px");
                window.addCloseListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        if (focusTextF) {
                            textField.focus();
                        }
                    }
                });
                addWindow(window);
            }
        });
        addComponent(button);
        addComponent(focusBut);
        addComponent(textField);
    }
}
