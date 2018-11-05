package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class TableColumnAddAndResize extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String people[][] = { { "Galileo", "Liked to go around the Sun" },
                { "Monnier", "Liked star charts" },
                { "VÃ€isÃ€lÃ€", "Liked optics" }, { "Oterma", "Liked comets" },
                { "Valtaoja", "Likes cosmology and still "
                        + "lives unlike the others above" }, };

        VerticalLayout content = new VerticalLayout();

        final Table table = new Table("Awesome Table");
        table.setSizeFull();
        table.addContainerProperty("Id1", String.class, "TestString");
        table.addContainerProperty("Id2", String.class, "TestString2");

        for (String[] p : people) {
            table.addItem(p);
        }
        table.setColumnWidth("Id1", 100);

        table.setColumnWidth("Id2", 100);

        table.setVisibleColumns("Id1");
        content.addComponent(table);
        Button button = new Button("Add and Resize");
        button.addClickListener(event -> {
            table.setVisibleColumns("Id1", "Id2");
            table.setColumnWidth("Id2", 200);
        });
        content.addComponent(button);
        addComponent(content);

    }
}
