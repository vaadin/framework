package com.vaadin.tests.tickets;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * Test class for ticket 1983
 */
public class Ticket1983 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow("Test for ticket 1983");
        main.setContent(new TestLayout());
        setMainWindow(main);
    }

    private static class TestLayout extends HorizontalSplitPanel {
        boolean isLong = true;
        final Table table = new MyTable();
        final String propId = "col";
        final String propId2 = "col2";

        public TestLayout() {

            setSplitPosition(200, Sizeable.UNITS_PIXELS);
            setLocked(true);

            final HorizontalSplitPanel leftSide = initLeftSide();
            setFirstComponent(leftSide);

            final Layout rightSide = new VerticalLayout();
            rightSide.setHeight("100%");
            setSecondComponent(rightSide);
        }

        private HorizontalSplitPanel initLeftSide() {
            final HorizontalSplitPanel leftSide = new HorizontalSplitPanel();
            leftSide.setHeight("100%");

            final IndexedContainer dataSource = new IndexedContainer();
            dataSource.addContainerProperty(propId, String.class, null);
            dataSource.addContainerProperty(propId2, String.class, null);
            final Object itemId = dataSource.addItem();
            dataSource
                    .getItem(itemId)
                    .getItemProperty(propId)
                    .setValue(
                            "Very long value that makes a scrollbar appear for sure");
            dataSource
                    .getItem(itemId)
                    .getItemProperty(propId2)
                    .setValue(
                            "Very long value that makes a scrollbar appear for sure");

            for (int i = 0; i < 150; i++) {
                Object id = dataSource.addItem();
                dataSource
                        .getItem(id)
                        .getItemProperty(propId)
                        .setValue(
                                (i == 100 ? "Very long value that makes a scrollbar appear for sure"
                                        : "Short"));
                dataSource.getItem(id).getItemProperty(propId2)
                        .setValue("Short");
            }

            table.setSizeFull();
            table.setContainerDataSource(dataSource);
            table.setVisibleColumns(new Object[] { propId });

            leftSide.setSecondComponent(table);

            Button button = new Button("Change col value to short");
            button.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    // Change the column value to a short one --> Should remove
                    // the scrollbar
                    if (isLong) {
                        dataSource.getItem(itemId).getItemProperty(propId)
                                .setValue("Short value");
                        dataSource.getItem(itemId).getItemProperty(propId2)
                                .setValue("Short value");
                        isLong = false;
                    } else {
                        dataSource
                                .getItem(itemId)
                                .getItemProperty(propId)
                                .setValue(
                                        "Very long value that makes a scrollbar appear for sure");
                        dataSource
                                .getItem(itemId)
                                .getItemProperty(propId2)
                                .setValue(
                                        "Very long value that makes a scrollbar appear for sure");
                        isLong = true;
                    }
                    // Works the same way with or without repaint request
                    table.markAsDirty();
                }
            });

            VerticalLayout ol = new VerticalLayout();
            ol.addComponent(button);
            leftSide.setFirstComponent(ol);

            CheckBox checkBox = new CheckBox("Two col");
            checkBox.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    if ((Boolean) event.getProperty().getValue()) {
                        table.setVisibleColumns(new Object[] { propId, propId2 });
                    } else {
                        table.setVisibleColumns(new Object[] { propId });
                    }

                }

            });
            ol.addComponent(checkBox);

            return leftSide;
        }
    }

    static class MyTable extends Table {
        MyTable() {
            alwaysRecalculateColumnWidths = true;
        }
    }
}
