package com.vaadin.tests.tickets;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.tests.TestForTablesInitialColumnWidthLogicRendering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2009 extends com.vaadin.server.LegacyApplication {

    TextField f = new TextField();

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        HorizontalLayout ol = new HorizontalLayout();
        main.setContent(ol);
        ol.setSizeFull();

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel p = new Panel("Tree test", pl);
        p.setSizeFull();

        Tree t = new Tree();

        t.addItem("Foo");
        t.addItem("Bar");

        final VerticalLayout events = new VerticalLayout();

        t.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                events.addComponent(new Label(new Label("Click:"
                        + (event.isDoubleClick() ? "double" : "single")
                        + " button:" + event.getButtonName() + " propertyId:"
                        + event.getPropertyId() + " itemID:"
                        + event.getItemId() + " item:" + event.getItem())));

            }
        });

        main.addComponent(p);
        pl.addComponent(t);
        pl.addComponent(events);

        VerticalLayout p2l = new VerticalLayout();
        p2l.setMargin(true);
        Panel p2 = new Panel("Table test (try dbl click also)", p2l);
        p2.setSizeFull();

        final VerticalLayout events2 = new VerticalLayout();
        Table table = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(5, 100);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        table.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                events2.addComponent(new Label("Click:"
                        + (event.isDoubleClick() ? "double" : "single")
                        + " button:" + event.getButtonName() + " propertyId:"
                        + event.getPropertyId() + " itemID:"
                        + event.getItemId() + " item:" + event.getItem()));
                if (event.isDoubleClick()) {
                    new PropertyEditor(event);
                }

            }
        });
        p2l.addComponent(table);
        p2l.addComponent(events2);

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
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            setContent(layout);
            c = (Container) event.getSource();

            propertyid = event.getPropertyId();
            itemid = event.getItemId();

            setCaption("Editing " + itemid + " : " + propertyid);

            editor.setPropertyDataSource(c.getContainerProperty(itemid,
                    propertyid));
            layout.addComponent(editor);
            layout.addComponent(done);

            setWidth(W + "px");
            setHeight(H + "px");

            setPositionX(event.getClientX() - W / 2);
            setPositionY(event.getClientY() - H / 2);

            getMainWindow().addWindow(this);

            done.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    getMainWindow().removeWindow(PropertyEditor.this);
                }
            });

        }

    }

}
