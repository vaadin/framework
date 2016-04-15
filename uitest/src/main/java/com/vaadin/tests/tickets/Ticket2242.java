package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class Ticket2242 extends LegacyApplication implements
        ValueChangeListener {

    private Object tableValue = null;
    private Table t;
    private String valueDataSource = "-";
    private ObjectProperty<String> prop;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        Button b = new Button("Change container datasource",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        for (int i = 0; i < 5; i++) {
                            t.setContainerDataSource(createContainer());
                            // prop.setValue("ipsum");
                        }
                    }

                });

        layout.addComponent(b);

        t = new Table("A table");
        prop = new ObjectProperty<String>(valueDataSource);
        t.setPropertyDataSource(prop);
        t.setSelectable(true);
        t.setImmediate(true);
        t.setPageLength(5);
        t.setContainerDataSource(createContainer());
        tableValue = t.getValue();
        t.addListener(this);

        layout.addComponent(t);
    }

    private IndexedContainer createContainer() {
        IndexedContainer ic = new IndexedContainer();
        ic.addContainerProperty("a", String.class, null);

        for (String s : new String[] { "Lorem", "ipsum", "dolor", "sit",
                "amet", "consectetuer" }) {
            Item item = ic.addItem(s);
            item.getItemProperty("a").setValue(s);

        }

        return ic;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        System.out.println("Value change from " + tableValue + " to "
                + t.getValue());
        tableValue = t.getValue();
    }

}
