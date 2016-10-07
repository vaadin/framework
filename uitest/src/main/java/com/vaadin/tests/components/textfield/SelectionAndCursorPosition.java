package com.vaadin.tests.components.textfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.v7.ui.TextArea;

public class SelectionAndCursorPosition extends AbstractReindeerTestUI {

    static final String DEFAULT_TEXT = "So we have some text to select";
    static final String TEXTFIELD_ID = "tf";
    static final String TEXTAREA_ID = "ta";
    static final String SELECT_ALL_ID = "selectAll";
    static final String RANGE_START_ID = "rS";
    static final String RANGE_LENGTH_ID = "rL";
    static final String CURSOR_POS_ID = "cp";
    static final String RANGE_SET_BUTTON_ID = "setSelection";
    static final String CURSOR_POS_SET_ID = "cps";

    TextField textField = createTextField();
    TextArea textArea = createTextArea();
    AbstractTextField activeComponent = textField;

    @Override
    protected void setup(VaadinRequest request) {
        FormLayout fl = new FormLayout();
        Panel panel = new Panel(fl);
        panel.setCaption("Hackers panel");
        CheckBox ml = new CheckBox("Multiline");
        // FIXME re-add this when TextArea has been replaced with vaadin8
        // version
        // ml.addListener(new Property.ValueChangeListener() {
        // @Override
        // public void valueChange(ValueChangeEvent event) {
        // if (textField.getUI() == null
        // || textField.getUI().getSession() == null) {
        // replaceComponent(textArea, textField);
        // activeComponent = textField;
        // } else {
        // replaceComponent(textField, textArea);
        // activeComponent = textArea;
        // }
        // }
        // });
        fl.addComponent(ml);

        Button selectAll = new Button("Select all ( selectAll() )");
        selectAll.setId(SELECT_ALL_ID);
        selectAll.addClickListener(event -> activeComponent.selectAll());
        fl.addComponent(selectAll);

        HorizontalLayout selectRange = new HorizontalLayout();
        selectRange.setCaption(
                "Select range of text ( setSelectionRange(int start, int lengt) )");
        final TextField start = new TextField("From:");
        start.setId(RANGE_START_ID);
        final TextField length = new TextField("Selection length:");
        length.setId(RANGE_LENGTH_ID);
        Button select = new Button("select");
        select.setId(RANGE_SET_BUTTON_ID);
        select.addClickListener(event -> {
            int startPos = Integer.parseInt(start.getValue());
            int lenght = Integer.parseInt(length.getValue());

            activeComponent.setSelection(startPos, lenght);
        });

        selectRange.addComponent(start);
        selectRange.addComponent(length);
        selectRange.addComponent(select);
        fl.addComponent(selectRange);

        HorizontalLayout setCursorPosition = new HorizontalLayout();
        final TextField pos = new TextField("Position:");
        pos.setId(CURSOR_POS_ID);
        Button setCursorButton = new Button("set");
        setCursorButton.setId(CURSOR_POS_SET_ID);
        setCursorButton.addClickListener(event -> {
            int startPos = Integer.parseInt(pos.getValue());
            activeComponent.setCursorPosition(startPos);
        });

        setCursorPosition.addComponent(pos);
        setCursorPosition.addComponent(setCursorButton);
        setCursorPosition.setCaption(
                "Set cursor position ( setCursorPosition(int pos) )");
        fl.addComponent(setCursorPosition);

        getLayout().addComponent(textField);
        getLayout().addComponent(panel);

    }

    private static TextField createTextField() {
        TextField textField = new TextField();
        textField.setId(TEXTFIELD_ID);
        textField.setCaption("Text field");
        textField.setValue(DEFAULT_TEXT);
        textField.setWidth("400px");

        return textField;
    }

    private static TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.setId(TEXTAREA_ID);
        textArea.setCaption("Text area");
        textArea.setValue(DEFAULT_TEXT);
        textArea.setWidth("400px");
        textArea.setHeight("50px");

        return textArea;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that setSelectionRange and setCursorPosition works for a TextField";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2058;
    }

}
