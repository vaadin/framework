package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Table;

public class HeaderSyncOnScroll extends AbstractReindeerTestUI {

    private Table table;

    @Override
    protected String getTestDescription() {
        return "Headers should be in sync when scrolling";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17947;
    }

    @Override
    protected void setup(VaadinRequest request) {
        table = new Table();
        table.setWidth("400px");
        table.setHeight("300px");
        table.setPageLength(40);

        table.setFooterVisible(true);
        for (int i = 1; i < 11; i++) {
            String propertyId = "Property " + i;
            table.addContainerProperty(propertyId, String.class, null);
            if ((i - 1) % 2 == 0) {
                table.setColumnFooter(propertyId, "FOOTER " + i);
            }
        }

        for (int i = 0; i < 80; i++) {
            table.addItem(new String[] { "item " + i + "1", "item " + i + "2",
                    "item " + i + "3", "item " + i + "4", "item " + i + "5",
                    "item " + i + "6", "item " + i + "7", "item " + i + "8",
                    "item " + i + "9", "item " + i + "10" }, i);
        }

        addComponent(table);

        HorizontalLayout buttonsLo = new HorizontalLayout();
        buttonsLo.setSpacing(false);
        addComponent(buttonsLo);

        Button setTableWidth100 = new Button("table.setWidth(100%)",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setWidth("100%");
                    }
                });

        Button setParent500px = new Button("layout.setWidth(500px)",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.getParent().setWidth("500px");
                    }
                });

        Button setParent100 = new Button("layout.setWidth(100%)",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.getParent().setWidth("100%");
                    }
                });

        buttonsLo.addComponents(setTableWidth100, setParent500px, setParent100);

    }
}
