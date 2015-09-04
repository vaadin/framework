package com.vaadin.tests.components.splitpanel;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@Theme("reindeer")
public class GridLayoutWithCheckbox extends UI {

    @Override
    protected void init(VaadinRequest request) {
        GridLayout grid = new GridLayout(2, 3);
        grid.setWidth(500, Unit.PIXELS);

        Label l = new Label("Textfield 1:");
        grid.addComponent(l, 0, 0);
        TextField textfield = new TextField();
        textfield.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {

            }
        });
        textfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
        grid.addComponent(textfield, 1, 0);

        l = new Label("CheckBox:");
        grid.addComponent(l, 0, 1);
        CheckBox checkBox = new CheckBox();
        grid.addComponent(checkBox, 1, 2);
        checkBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {

            }
        });
        Window window = new Window();
        window.setWidth(300.0f, Unit.PIXELS);
        window.setContent(grid);
        window.setResizable(false);
        window.setWidth(550, Unit.PIXELS);

        // grid.setColumnExpandRatio(1, 1);
        addWindow(window);
    }
}