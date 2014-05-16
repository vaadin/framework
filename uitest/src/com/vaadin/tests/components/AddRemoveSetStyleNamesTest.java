package com.vaadin.tests.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PopupDateField;

public class AddRemoveSetStyleNamesTest extends TestBase {

    private String style1 = "style1";
    private String style2 = "style2";
    private String thestyle = "thestyle";

    private PopupDateField popupDateField;
    private Button button1;
    private Button button2;
    private Button button3;

    private Button.ClickListener listener;

    @Override
    protected void setup() {
        popupDateField = new PopupDateField("PopupDateField");
        popupDateField.setRequired(true);
        popupDateField.setRequiredError("abcd");
        addComponent(popupDateField);

        listener = new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String style = (String) event.getButton().getData();
                setComponentsStyle(style, !popupDateField.getStyleName()
                        .contains(style), event.getButton());
            }
        };

        button1 = new Button("Add style1", listener);
        button1.setData(style1);
        addComponent(button1);

        button2 = new Button("Add style2", listener);
        button2.setData(style2);
        addComponent(button2);

        button3 = new Button("Set thestyle", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (popupDateField.getStyleName().contains(thestyle)) {
                    popupDateField.removeStyleName(thestyle);
                    button3.setCaption("Set thestyle");
                } else {
                    popupDateField.setStyleName(thestyle);
                    button1.setCaption("Add style1");
                    button2.setCaption("Add style2");
                    button3.setCaption("Remove thestyle");
                }
            }
        });
        addComponent(button3);
    }

    private void setComponentsStyle(String style, boolean add, Button button) {
        if (add) {
            popupDateField.addStyleName(style);
            button.setCaption("Remove " + style);
        } else {
            popupDateField.removeStyleName(style);
            button.setCaption("Add " + style);
        }
    }

    @Override
    protected String getDescription() {
        return "If a widget has set multiple css class names, AbtractComponentConnector.getStyleNames() removes all but first one of them. This is not acceptable, because we should be able to create connector for any existing GWT component and thus we do not know it it depends on multiple css class names.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8664;
    }

}
