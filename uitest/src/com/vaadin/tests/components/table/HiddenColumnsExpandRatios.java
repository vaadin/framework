package com.vaadin.tests.components.table;

import java.util.Random;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

public class HiddenColumnsExpandRatios extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setColumnCollapsingAllowed(true);
        table.setWidth("800px");
        addComponent(table);
        table.addContainerProperty("foo", String.class, "");
        table.addContainerProperty("bar", String.class, "");
        table.addContainerProperty("baz", String.class, "");
        table.addContainerProperty("asdf", String.class, "");
        table.addContainerProperty("sdfg", String.class, "");
        table.addContainerProperty("dfgh", String.class, "");
        table.setColumnExpandRatio("bar", 1.0f);
        for (int i = 0; i < 10; i++) {
            Item item = table.addItem(i);
            item.getItemProperty("foo").setValue(genValue());
            item.getItemProperty("bar").setValue(genValue());
            item.getItemProperty("baz").setValue(genValue());
            item.getItemProperty("asdf").setValue(genValue());
            item.getItemProperty("sdfg").setValue(genValue());
            item.getItemProperty("dfgh").setValue(genValue());
        }

        addComponent(new Button("All", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setVisibleColumns(table.getContainerPropertyIds()
                        .toArray());
            }
        }));
        addComponent(new Button("Some", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setWidth("100px");
                table.setWidth("800px");
                table.setVisibleColumns(new Object[] { "foo", "bar", "baz" });
            }
        }));
    }

    private String genValue() {
        Random rnd = new Random();
        StringBuffer str = new StringBuffer("");
        String[] strings = new String[] { "foo", "bar", "baz" };
        for (int i = 0; i < 5; i++) {
            str.append(strings[Math.abs(rnd.nextInt() % strings.length)])
                    .append(" ");
        }
        return str.toString();
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
