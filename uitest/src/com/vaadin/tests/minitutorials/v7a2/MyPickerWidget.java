package com.vaadin.tests.minitutorials.v7a2;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

public class MyPickerWidget extends ComplexPanel {

    public static final String CLASSNAME = "mypicker";

    private final TextBox textBox = new TextBox();
    private final PushButton button = new PushButton("...");

    public MyPickerWidget() {
        setElement(Document.get().createDivElement());
        setStylePrimaryName(CLASSNAME);

        textBox.setStylePrimaryName(CLASSNAME + "-field");
        button.setStylePrimaryName(CLASSNAME + "-button");

        add(textBox, getElement());
        add(button, getElement());

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Calendar picker not yet supported!");
            }
        });
    }

    public void setButtonText(String buttonText, boolean adjustSpace) {
        if (buttonText == null || buttonText.length() == 0) {
            buttonText = "...";
        }
        button.setText(buttonText);

        if (adjustSpace) {
            adjustButtonSpace(button.getOffsetWidth());
        }
    }

    public void adjustButtonSpace(int width) {
        getElement().getStyle().setPaddingRight(width, Unit.PX);
        button.getElement().getStyle().setMarginRight(-width, Unit.PX);
    }
}
