package com.vaadin.tests.components.grid;

import java.util.List;
import java.util.Random;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.TextRenderer;

@SuppressWarnings("serial")
@Theme("valo")
public class GridSwitchRenderers extends AbstractTestUIWithLog {
    private static final int MANUALLY_FORMATTED_COLUMNS = 1;
    private static final int COLUMNS = 3;
    private static final int ROWS = 1000;
    private static final String EXPANSION_COLUMN_ID = "Column 0";

    private IndexedContainer ds;

    @Override
    protected void setup(VaadinRequest request) {
        ds = new IndexedContainer() {
            @Override
            public List<Object> getItemIds(int startIndex, int numberOfIds) {
                log("Requested items " + startIndex + " - "
                        + (startIndex + numberOfIds));
                return super.getItemIds(startIndex, numberOfIds);
            }
        };

        {
            ds.addContainerProperty(EXPANSION_COLUMN_ID, String.class, "");

            int col = MANUALLY_FORMATTED_COLUMNS;
            for (; col < COLUMNS; col++) {
                ds.addContainerProperty(getColumnProperty(col), String.class,
                        "");
            }
        }

        Random rand = new Random();
        rand.setSeed(13334);
        for (int row = 0; row < ROWS; row++) {
            Item item = ds.addItem(Integer.valueOf(row));
            fillRow("" + row, item);
            item.getItemProperty(getColumnProperty(1)).setReadOnly(true);
        }

        final Grid grid = new Grid(ds);
        grid.setWidth("100%");

        grid.getColumn(EXPANSION_COLUMN_ID).setWidth(50);
        for (int col = MANUALLY_FORMATTED_COLUMNS; col < COLUMNS; col++) {
            grid.getColumn(getColumnProperty(col)).setWidth(300);
            grid.getColumn(getColumnProperty(col)).setRenderer(
                    new TextRenderer());
        }

        grid.setSelectionMode(SelectionMode.NONE);
        addComponent(grid);

        final CheckBox changeRenderer = new CheckBox(
                "SetHtmlRenderer for Column 2", false);
        changeRenderer.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Column column = grid.getColumn(getColumnProperty(1));
                if (changeRenderer.getValue()) {
                    column.setRenderer(new HtmlRenderer());
                } else {
                    column.setRenderer(new TextRenderer());
                }
                grid.markAsDirty();
            }
        });
        addComponent(changeRenderer);
    }

    @SuppressWarnings("unchecked")
    private void fillRow(String content, Item item) {
        int col = MANUALLY_FORMATTED_COLUMNS;

        for (; col < COLUMNS; col++) {
            item.getItemProperty(getColumnProperty(col)).setValue(
                    "<b>(" + content + ", " + col + ")</b>");
        }
    }

    private static String getColumnProperty(int c) {
        return "Column " + c;
    }

}