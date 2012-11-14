package com.vaadin.tests.tickets;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket677 extends LegacyApplication {

    private static final Label info = new Label(
            "<li> keep debug window open to see variable changes"
                    + "<li> disable root panel w/ toggle button"
                    + "<li> toggle one of the subpanels"
                    + "<li> we attempt to focus the subpanels first textfield"
                    + "<li> focusing should fail (try tabbing as well) [worked previousy]"
                    + "<li> no variable changes should be sent from disabled fields [changed sent previously]"
                    + "<li> try further toggling and tabbing around",
            ContentMode.RAW);

    Panel root = new Panel("Enabled");
    Panel one = new Panel("Enabled");
    Panel two = new Panel("Enabled");
    Form form;
    Table table;

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
        setMainWindow(main);

        main.addComponent(info);

        HorizontalLayout l = new HorizontalLayout();
        main.addComponent(l);

        l.addComponent(new Button("Toggle root panel",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        toggle(root);
                    }
                }));
        l.addComponent(new Button("Toggle panel one",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        toggle(one);
                    }
                }));
        l.addComponent(new Button("Toggle panel two",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        toggle(two);
                    }
                }));
        l.addComponent(new Button("Toggle form", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toggle(form);
            }
        }));
        l.addComponent(new Button("Toggle table", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toggle(table);
            }
        }));

        GridLayout content = new GridLayout(2, 3);
        root.setContent(content);
        main.addComponent(root);

        TextField tf = new TextField("Enabled");
        tf.setImmediate(true);
        content.addComponent(tf);
        tf = new TextField("Disabled");
        tf.setImmediate(true);
        tf.setEnabled(false);
        content.addComponent(tf);

        VerticalLayout oneLayout = new VerticalLayout();
        oneLayout.setMargin(true);
        one.setContent(oneLayout);

        content.addComponent(one);
        tf = new TextField("Enabled");
        tf.setImmediate(true);
        oneLayout.addComponent(tf);
        tf = new TextField("Disabled");
        tf.setImmediate(true);
        tf.setEnabled(false);
        oneLayout.addComponent(tf);

        VerticalLayout twoLayout = new VerticalLayout();
        twoLayout.setMargin(true);
        two.setContent(twoLayout);

        content.addComponent(two);
        tf = new TextField("Enabled");
        tf.setImmediate(true);
        twoLayout.addComponent(tf);
        tf = new TextField("Disabled");
        tf.setImmediate(true);
        tf.setEnabled(false);
        twoLayout.addComponent(tf);

        form = new Form();
        form.setCaption("Enabled");
        form.setFormFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field<?> createField(Item item, Object propertyId,
                    Component uiContext) {
                Field<?> f = super.createField(item, propertyId, uiContext);
                f.setEnabled(!"disabled".equals(propertyId));
                return f;
            }

        });
        form.setItemDataSource(new BeanItem<MyBean>(new MyBean()));
        content.addComponent(form);

        table = new Table("Enabled");
        table.setPageLength(7);
        table.addContainerProperty("Text", String.class, null);
        for (int i = 0; i < 150; i++) {
            Item item = table.addItem("Item" + i);
            Property<String> p = item.getItemProperty("Text");
            p.setValue(i % 5 == 0 ? "enabled" : "disabled");
        }

        table.setTableFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field<?> createField(Container container, Object itemId,
                    Object propertyId, Component uiContext) {
                Field<?> f = super.createField(container, itemId, propertyId,
                        uiContext);
                Item item = container.getItem(itemId);
                Property<?> p = item.getItemProperty(propertyId);
                if ("disabled".equals(p.getValue())) {
                    f.setEnabled(false);
                }
                return f;
            }

        });
        table.setEditable(true);
        content.addComponent(table);

    }

    private void toggle(Component c) {
        boolean enable = "Disabled".equals(c.getCaption());
        c.setEnabled(enable);
        c.setCaption((enable ? "Enabled" : "Disabled"));
        if (c instanceof ComponentContainer) {
            TextField tf = (TextField) ((ComponentContainer) c)
                    .getComponentIterator().next();
            tf.focus();
        }
    }

    class MyBean {
        boolean on = false;
        int number = 1;
        String rw = "read/write";
        String r = "read";
        String disabled = "disabled";

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getRw() {
            return rw;
        }

        public void setRw(String rw) {
            this.rw = rw;
        }

        public String getDisabled() {
            return disabled;
        }

        public void setDisabled(String disabled) {
            this.disabled = disabled;
        }

        public String getR() {
            return r;
        }

    }
}
