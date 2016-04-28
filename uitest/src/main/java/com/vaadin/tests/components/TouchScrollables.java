package com.vaadin.tests.components;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TouchScrollables extends TestBase {
    java.util.Random r = new java.util.Random(1);

    private TabSheet testSelector = new TabSheet();

    @Override
    public void setup() {
        getLayout().addComponent(testSelector);
        testSelector.setHeight("500px");

        addTest(getPanelTest());
        addTest(getSimpleTableTest());
        addTest(getDDSortableTableTest());
        addTest(getTabSheetTest());
        addTest(getSplitPanelTest());
        addTest(getAccordionTest());
        addTest(getSubWindowTest());

        TestUtils
                .injectCSS(
                        getLayout().getUI(),
                        "body * {-webkit-user-select: none;} .v-table-row-drag-middle .v-table-cell-content {"
                                + "        background-color: inherit ; border-bottom: 1px solid cyan;"
                                + "}"
                                + ".v-table-row-drag-middle .v-table-cell-wrapper {"
                                + "        margin-bottom: -1px;" + "}" + ""

                );
    }

    private Component getPanelTest() {
        Layout cssLayout = new CssLayout();
        cssLayout.setCaption("Panel");

        final VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        final Panel p = new Panel(pl);
        p.setHeight("400px");
        Label l50 = null;
        for (int i = 0; i < 100; i++) {
            Label c = new Label("Label" + i);
            pl.addComponent(c);
            if (i == 50) {
                l50 = c;
            }
        }

        final Label l = l50;
        Button button = new Button("Scroll to label 50",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getLayout().getUI().scrollIntoView(l);
                    }
                });
        cssLayout.addComponent(button);
        button = new Button("Scroll to 100px", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                p.setScrollTop(100);
            }
        });
        cssLayout.addComponent(button);
        cssLayout.addComponent(p);
        return cssLayout;
    }

    private Component getTabSheetTest() {
        TabSheet ts = new TabSheet();
        ts.setCaption("Tabsheet");
        ts.setHeight("100%");
        ts.addTab(getBigComponent(), "Tab 1");
        ts.addTab(getBigComponent(), "Tab 2");
        return ts;
    }

    private Component getSplitPanelTest() {
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setCaption("Splitpanel");
        sp.addComponent(getBigComponent());
        sp.addComponent(getBigComponent());
        return sp;
    }

    private Component getSimpleTableTest() {
        CssLayout cssLayout = new CssLayout();
        final Table table = new Table();

        Button button = new Button("Toggle lazyloading");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (table.getCacheRate() == 100) {
                    table.setCacheRate(2);
                    table.setPageLength(15);
                } else {
                    table.setCacheRate(100);
                    table.setHeight("400px");
                }
            }
        });
        cssLayout.addComponent(button);

        button = new Button("Toggle selectable");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setSelectable(!table.isSelectable());
            }
        });
        cssLayout.addComponent(button);

        table.addContainerProperty("foo", String.class, "bar");
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
        for (int i = 0; i < 1000; i++) {
            table.addItem();
        }
        cssLayout.addComponent(table);
        cssLayout.setCaption("Table");
        return cssLayout;
    }

    private Component getAccordionTest() {
        Accordion a = new Accordion();
        a.setCaption("Accordion");
        a.setHeight("100%");
        a.addTab(getBigComponent(), "Tab 1");
        a.addTab(getBigComponent(), "Tab 2");
        a.addTab(getBigComponent(), "Tab 3");
        return a;
    }

    private Component getSubWindowTest() {
        Button b = new Button("Open subwindow", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                VerticalLayout layout = new VerticalLayout();
                layout.setMargin(true);
                Window w = new Window("Subwindow", layout);
                w.center();
                w.setHeight("200px");
                layout.addComponent(getBigComponent());
                getMainWindow().addWindow(w);
            }
        });
        return b;
    }

    private Component getDDSortableTableTest() {
        final Table table;
        table = new Table();
        table.setCaption("DD sortable table with context menus");
        // table.setWidth("100%");
        table.setPageLength(10);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.addActionHandler(new Handler() {

            Action[] actions = new Action[] { new Action("FOO"),
                    new Action("BAR"), new Action("CAR") };

            @Override
            public Action[] getActions(Object target, Object sender) {
                return actions;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                Notification.show(action.getCaption());

            }
        });

        populateTable(table);

        /*
         * Make table rows draggable
         */
        table.setDragMode(Table.TableDragMode.ROW);

        table.setDropHandler(new DropHandler() {
            // accept only drags from this table
            AcceptCriterion crit = new SourceIs(table);

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return crit;
            }

            @Override
            public void drop(DragAndDropEvent dropEvent) {
                AbstractSelectTargetDetails dropTargetData = (AbstractSelectTargetDetails) dropEvent
                        .getTargetDetails();
                DataBoundTransferable transferable = (DataBoundTransferable) dropEvent
                        .getTransferable();
                Object itemIdOver = dropTargetData.getItemIdOver();
                Object itemId = transferable.getItemId();
                if (itemId == null || itemIdOver == null
                        || itemId.equals(itemIdOver)) {
                    return; // no move happened
                }

                // IndexedContainer goodies... (hint: don't use it in real apps)
                IndexedContainer containerDataSource = (IndexedContainer) table
                        .getContainerDataSource();
                int newIndex = containerDataSource.indexOfId(itemIdOver) - 1;
                if (dropTargetData.getDropLocation() != VerticalDropLocation.TOP) {
                    newIndex++;
                }
                if (newIndex < 0) {
                    newIndex = 0;
                }
                Object idAfter = containerDataSource.getIdByIndex(newIndex);
                Collection<?> selections = (Collection<?>) table.getValue();
                if (selections != null && selections.contains(itemId)) {
                    // dragged a selected item, if multiple rows selected, drag
                    // them too (functionality similar to apple mail)
                    for (Object object : selections) {
                        moveAfter(containerDataSource, object, idAfter);
                    }

                } else {
                    // move just the dragged row, not considering selection at
                    // all
                    moveAfter(containerDataSource, itemId, idAfter);
                }

            }

            private void moveAfter(IndexedContainer containerDataSource,
                    Object itemId, Object idAfter) {
                try {
                    IndexedContainer clone = null;
                    clone = (IndexedContainer) containerDataSource.clone();
                    containerDataSource.removeItem(itemId);
                    Item newItem = containerDataSource.addItemAfter(idAfter,
                            itemId);
                    Item item = clone.getItem(itemId);
                    for (Object propId : item.getItemPropertyIds()) {
                        newItem.getItemProperty(propId).setValue(
                                item.getItemProperty(propId).getValue());
                    }

                    // TODO Auto-generated method stub
                } catch (CloneNotSupportedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        return table;
    }

    private void populateTable(Table table) {
        table.addContainerProperty("Name", String.class, "");
        table.addContainerProperty("Weight", Integer.class, 0);

        PersonContainer testData = PersonContainer.createWithTestData();

        for (int i = 0; i < 40; i++) {
            Item addItem = table.addItem("Item" + i);
            Person p = testData.getIdByIndex(i);
            addItem.getItemProperty("Name").setValue(
                    p.getFirstName() + " " + p.getLastName());
            addItem.getItemProperty("Weight").setValue(50 + r.nextInt(60));
        }

    }

    private void addTest(final Component t) {
        testSelector.addComponent(t);
    }

    private Component getBigComponent() {
        Layout l = new VerticalLayout();
        for (int i = 0; i < 100; i++) {
            Label c = new Label("Label" + i);
            l.addComponent(c);
        }
        return l;
    }

    @Override
    protected String getDescription() {
        return "Various components and setups suitable for testing scrolling on touch devices.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
