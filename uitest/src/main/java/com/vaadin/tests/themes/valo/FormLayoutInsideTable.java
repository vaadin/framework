package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class FormLayoutInsideTable extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();

        table.addGeneratedColumn("data", new Table.ColumnGenerator() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                FormLayout layout = new FormLayout();
                layout.addComponent(new Label("Line 1 " + itemId));
                layout.addComponent(new Label("Line 2 " + itemId));

                return layout;
            }
        });

        table.setSizeFull();
        table.addItem("abc0");
        addComponent(table);
    }}
