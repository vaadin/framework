package com.vaadin.tests.components.richtextarea;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class RichTextAreaPreventsTextFieldAccess extends TestBase {

    @Override
    protected void setup() {
        Label label = new Label(
                "Steps to reproduce problem with IE8. "
                        + "<br> Step 1: Click on the 'Open RichTextArea-Dialog' button "
                        + "<br> Step 2: Write something in the RichTextArea. "
                        + "Do not press outside the textfield for the "
                        + "richTextArea. <br> Step 3: Press the 'removeWindowButton' "
                        + "<br> Now you cannot write in the TextField on this page "
                        + "<br> Resetting the focus to textfield explicitly, works around the issue");
        label.setContentMode(ContentMode.HTML);
        addComponent(label);

        final TextField testField = new TextField("");
        testField.setId("field");
        addComponent(testField);

        final RichTextArea rText = new RichTextArea();
        rText.setWidth("300px");
        rText.setHeight("300px");

        final Window subWindow = new Window("SubWindow");
        subWindow.setWidth("500px");
        subWindow.setHeight("500px");
        subWindow.setModal(true);

        final VerticalLayout wLayout = new VerticalLayout();
        subWindow.setContent(wLayout);

        wLayout.addComponent(rText);
        wLayout.addComponent(new TextField());

        Button addWindowButton = new Button("Open RichTextArea-Dialog");
        addWindowButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().addWindow(subWindow);

            }
        });
        addComponent(addWindowButton);

        Button removeWindowButton = new Button("removeWindowButton");
        removeWindowButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().removeWindow(subWindow);

            }
        });
        wLayout.addComponent(removeWindowButton);

        Button focusButton = new Button("Set focus on TextField");
        focusButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                testField.focus();

            }
        });
        addComponent(focusButton);

        Button removeRTA = new Button("Remove RTA");
        removeRTA.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                wLayout.removeComponent(rText);

            }
        });
        wLayout.addComponent(removeRTA);

        CheckBox cb = new CheckBox("close");
        cb.setImmediate(true);
        cb.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                getMainWindow().removeWindow(subWindow);
            }
        });
        wLayout.addComponent(cb);

    }

    @Override
    protected String getDescription() {
        return "RichtextArea prevents TextField access in IE8";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10776;
    }

}
