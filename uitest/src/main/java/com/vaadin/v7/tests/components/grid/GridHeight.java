package com.vaadin.v7.tests.components.grid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.DetailsGenerator;
import com.vaadin.v7.ui.Grid.RowReference;
import com.vaadin.v7.ui.OptionGroup;

/**
 * Tests that Grid gets correct height based on height mode, and resizes
 * properly with details row if height is undefined.
 *
 * @author Vaadin Ltd
 */
public class GridHeight extends AbstractReindeerTestUI {

    static final String FULL = "Full";
    static final String UNDEFINED = "Undefined";
    static final String PX100 = "100px";
    static final Integer ROW3 = 3;

    static final Object[] gridHeights = { FULL, UNDEFINED, ROW3 };
    static final String[] gridWidths = { FULL, UNDEFINED };
    static final String[] detailsRowHeights = { FULL, UNDEFINED, PX100 };

    private Grid grid;
    private Map<Object, VerticalLayout> detailsLayouts = new HashMap<>();
    private OptionGroup detailsHeightSelector;

    @Override
    protected void setup(VaadinRequest request) {

        grid = new Grid();
        grid.addColumn("name", String.class);
        grid.addColumn("born", Integer.class);

        grid.addRow("Nicolaus Copernicus", 1543);
        grid.addRow("Galileo Galilei", 1564);
        for (int i = 0; i < 1; ++i) {
            grid.addRow("Johannes Kepler", 1571);
        }

        grid.setDetailsGenerator(new DetailsGenerator() {
            @Override
            public Component getDetails(final RowReference rowReference) {
                if (!detailsLayouts.containsKey(rowReference.getItemId())) {
                    createDetailsLayout(rowReference.getItemId());
                }
                return detailsLayouts.get(rowReference.getItemId());
            }
        });

        grid.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(final ItemClickEvent event) {
                final Object itemId = event.getItemId();
                grid.setDetailsVisible(itemId, !grid.isDetailsVisible(itemId));
            }
        });

        addComponent(createOptionLayout());
        addComponent(grid);
    }

    private void createDetailsLayout(Object itemId) {
        VerticalLayout detailsLayout = new VerticalLayout();
        setDetailsHeight(detailsLayout, detailsHeightSelector.getValue());
        detailsLayout.setWidth("100%");

        Label lbl1 = new Label("details row");
        lbl1.setId("lbl1");
        lbl1.setSizeUndefined();
        detailsLayout.addComponent(lbl1);
        detailsLayout.setComponentAlignment(lbl1, Alignment.MIDDLE_CENTER);

        detailsLayouts.put(itemId, detailsLayout);
    }

    private Component createOptionLayout() {
        HorizontalLayout optionLayout = new HorizontalLayout();
        OptionGroup gridHeightSelector = new OptionGroup("Grid height",
                Arrays.<Object> asList(gridHeights));
        gridHeightSelector.setId("gridHeightSelector");
        gridHeightSelector.setItemCaption(ROW3, ROW3 + " rows");
        gridHeightSelector.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (UNDEFINED.equals(value)) {
                    grid.setHeightUndefined();
                    grid.setHeightMode(HeightMode.UNDEFINED);
                } else if (FULL.equals(value)) {
                    grid.setHeight("100%");
                    grid.setHeightMode(HeightMode.CSS);
                } else if (ROW3.equals(value)) {
                    grid.setHeightByRows(ROW3);
                    grid.setHeightMode(HeightMode.ROW);
                }
            }
        });
        gridHeightSelector.setValue(UNDEFINED);
        optionLayout.addComponent(gridHeightSelector);

        OptionGroup gridWidthSelector = new OptionGroup("Grid width",
                Arrays.asList(gridWidths));
        gridWidthSelector.setId("gridWidthSelector");
        gridWidthSelector.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (UNDEFINED.equals(value)) {
                    grid.setWidthUndefined();
                } else if (FULL.equals(value)) {
                    grid.setWidth("100%");
                }
            }
        });
        gridWidthSelector.setValue(UNDEFINED);
        optionLayout.addComponent(gridWidthSelector);

        detailsHeightSelector = new OptionGroup("Details row height",
                Arrays.asList(detailsRowHeights));
        detailsHeightSelector.setId("detailsHeightSelector");
        detailsHeightSelector.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                for (VerticalLayout detailsLayout : detailsLayouts.values()) {
                    setDetailsHeight(detailsLayout, value);
                }
            }
        });
        detailsHeightSelector.setValue(PX100);
        optionLayout.addComponent(detailsHeightSelector);
        return optionLayout;
    }

    private void setDetailsHeight(VerticalLayout detailsLayout, Object value) {
        if (UNDEFINED.equals(value)) {
            detailsLayout.setHeightUndefined();
        } else if (FULL.equals(value)) {
            detailsLayout.setHeight("100%");
        } else if (PX100.equals(value)) {
            detailsLayout.setHeight(PX100);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Grid with undefined height should display all rows and resize when details row is opened."
                + "<br>Grid with full height is always 400px high regardless or details row."
                + "<br>Grid with row height should always be the height of those rows regardless of details row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19690;
    }
}
