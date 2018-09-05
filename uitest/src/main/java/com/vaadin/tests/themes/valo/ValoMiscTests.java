package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TreeTable;

public class ValoMiscTests extends UI {

    VerticalLayout layout = new VerticalLayout();

    public ValoMiscTests() {
        layout.setMargin(true);

        Panel p = new Panel();
        // layout.addComponent(p);

        HorizontalLayout row = new HorizontalLayout();
        p.setHeight("300px");
        // row.setSpacing(true);
        // row.setMargin(true);
        row.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
        row.setWidth("100%");
        // row.setHeight("248px");
        row.setSizeFull();

        Button button = new Button("Boo");
        button.setWidth("100%");
        Button button2 = new Button("Abc");
        button2.setWidth("100%");
        row.addComponents(button, new InlineDateField(), new CheckBox("Far"));

        p.setContent(row);

        p = new Panel();
        // layout.addComponent(p);
        p.setHeight("400px");

        VerticalLayout col = new VerticalLayout();
        col.setSizeFull();
        col.setSpacing(true);
        // row.setMargin(true);
        col.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);

        col.addComponents(new Button("Boo"), new InlineDateField(),
                new CheckBox("Far"), new Button("Abc"));

        p.setContent(col);

        TreeTable table = new TreeTable();
        table.setWidth("100%");
        table.setContainerDataSource(ValoThemeUI.generateContainer(200, true));
        Tables.configure(table, true, false, false, true, true, true, false,
                true, false, false, false, false, false, false);
        layout.addComponent(table);

    }

    @Override
    protected void init(VaadinRequest request) {
        setContent(layout);
    }

}
