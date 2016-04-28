package com.vaadin.tests.components.tree;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Tree;

public class TreeFiltering extends TestBase {

    @Override
    public void setup() {

        final Tree ccTree = new Tree();
        addComponent(ccTree);
        final HierarchicalContainer cont = new HierarchicalContainer();
        cont.addContainerProperty("caption", String.class, "");

        Item item;
        for (int i = 0; i < 5; i++) {
            item = cont.addItem(i);
            item.getItemProperty("caption").setValue("Number " + i);
            cont.setParent(i, i - 1);
        }

        for (int i = 0; i < 5; i++) {
            Object id = cont.addItem();
            item = cont.getItem(id);
            item.getItemProperty("caption").setValue("0-" + i);
            cont.setParent(id, 0);
        }

        ccTree.setContainerDataSource(cont);
        ccTree.setItemCaptionPropertyId("caption");

        for (final Object o : ccTree.getItemIds()) {
            ccTree.expandItem(o);
        }

        final CheckBox filterType = new CheckBox(
                "Include parent when filtering", true);
        filterType.setImmediate(true);
        filterType.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                cont.setIncludeParentsWhenFiltering(((CheckBox) event
                        .getProperty()).getValue());
                ccTree.markAsDirty();
            }
        });
        addComponent(filterType);

        final Button b = new Button("Add filter 'foo'", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "foo", true, false);

            }
        });
        addComponent(b);
        final Button b2 = new Button("Add filter 'Num'", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "Num", true, false);

            }
        });

        addComponent(b2);
        final Button num = new Button("Add filter '0'", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "0", true, false);

            }
        });

        addComponent(num);
        final Button num2 = new Button("Add filter '0-'", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                cont.addContainerFilter("caption", "0-", true, false);

            }
        });

        addComponent(num2);
        final Button num3 = new Button("Add filter 'Number 4'",
                new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        cont.addContainerFilter("caption", "Number 4", true,
                                false);

                    }
                });

        addComponent(num3);
        final Button p1 = new Button("Set Number 3 parent to Number 0",
                new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        cont.setParent(3, 0);

                    }
                });
        addComponent(p1);
        final Button r = new Button("Remove filters", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                cont.removeAllContainerFilters();

            }
        });
        addComponent(r);
    }

    @Override
    protected String getDescription() {
        return "Filtering in a tree should work as expected. Roots and their children which match the filter should be shown. Other nodes should be hidden";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4192;
    }

}
