package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2242 extends Application implements ValueChangeListener {

    private Object tableValue = null;
    private Table t;
    private String valueDataSource = "-";
    private ObjectProperty prop;

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        Button b = new Button("Change container datasource",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        for (int i = 0; i < 5; i++) {
                            t.setContainerDataSource(createContainer());
                            // prop.setValue("ipsum");
                        }
                    }

                });

        layout.addComponent(b);

        t = new Table("A table");
        prop = new ObjectProperty(valueDataSource);
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

    private static class TestObject {
        public TestObject(int a, String b, Long c) {
            super();
            this.a = a;
            this.b = b;
            this.c = c;
        }

        private int a;
        private String b;
        private Long c;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public Long getC() {
            return c;
        }

        public void setC(Long c) {
            this.c = c;
        }
    }

    public void valueChange(ValueChangeEvent event) {
        System.out.println("Value change from " + tableValue + " to "
                + t.getValue());
        tableValue = t.getValue();
    }

}
