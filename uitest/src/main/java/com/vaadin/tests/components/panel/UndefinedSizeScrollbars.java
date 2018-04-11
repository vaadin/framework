package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

public class UndefinedSizeScrollbars extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSizeFull();
        setContent(layout);

        GridLayout grid = new GridLayout();
        grid.setSpacing(true);

        TextField text1 = new TextField();
        text1.setCaption("Text1");
        text1.setRequired(true);

        TextField text2 = new TextField();
        text2.setCaption("Text2");
        text2.setRequired(true);

        ComboBox<String> combo = new ComboBox<>();
        combo.setCaption("Combo1");

        CheckBox check = new CheckBox();
        check.setCaption("Check");

        grid.setColumns(2);
        grid.setRows(2);

        grid.addComponent(text1);
        grid.addComponent(text2);
        grid.addComponent(combo);
        grid.addComponent(check);

        grid.setSizeUndefined();

        Panel panel = new Panel();
        panel.setContent(grid);

        panel.setSizeUndefined();

        layout.addComponent(panel);
    }

}
