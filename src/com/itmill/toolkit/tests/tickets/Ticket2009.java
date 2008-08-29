package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.event.ItemClickEvent;
import com.itmill.toolkit.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2009 extends com.itmill.toolkit.Application {

    TextField f = new TextField();

    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.setLayout(ol);
        ol.setSizeFull();

        Panel p = new Panel("Tree test");
        p.setSizeFull();

        Tree t = new Tree();

        t.addItem("Foo");
        t.addItem("Bar");

        final OrderedLayout events = new OrderedLayout();

        t.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                events.addComponent(new Label(new Label("Click:"
                        + (event.isDoubleClick() ? "double" : "single")
                        + " button:" + event.getButton() + " propertyId:"
                        + event.getPropertyId() + " itemID:"
                        + event.getItemId() + " item:" + event.getItem())));

            }
        });

        main.addComponent(p);
        p.addComponent(t);
        p.addComponent(events);

        Panel p2 = new Panel("Table test (try dbl click also)");
        p2.setSizeFull();

        final OrderedLayout events2 = new OrderedLayout();
        Table table = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(5, 100);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        table.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                events2.addComponent(new Label("Click:"
                        + (event.isDoubleClick() ? "double" : "single")
                        + " button:" + event.getButton() + " propertyId:"
                        + event.getPropertyId() + " itemID:"
                        + event.getItemId() + " item:" + event.getItem()));
                if (event.isDoubleClick()) {
                    new PropertyEditor(event);
                }

            }
        });
        p2.addComponent(table);
        p2.addComponent(events2);

        main.addComponent(p2);

    }

    class PropertyEditor extends Window {

        private static final int W = 300;
        private static final int H = 150;

        private Container c;
        private Object itemid;
        private Object propertyid;

        TextField editor = new TextField();
        Button done = new Button("Done");

        PropertyEditor(ItemClickEvent event) {
            c = (Container) event.getSource();

            propertyid = event.getPropertyId();
            itemid = event.getItemId();

            setCaption("Editing " + itemid + " : " + propertyid);

            editor.setPropertyDataSource(c.getContainerProperty(itemid,
                    propertyid));
            addComponent(editor);
            addComponent(done);

            setWidth(W);
            setHeight(H);

            setPositionX(event.getClientX() - W / 2);
            setPositionY(event.getClientY() - H / 2);

            getMainWindow().addWindow(this);

            done.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    getMainWindow().removeWindow(PropertyEditor.this);
                }
            });

        }

    }

}