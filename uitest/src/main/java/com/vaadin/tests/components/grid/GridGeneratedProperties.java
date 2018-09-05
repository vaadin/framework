package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Container.Filterable;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.Sort;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.data.util.filter.Compare;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;
import com.vaadin.v7.ui.Grid;

/**
 * Smoke tests for v7 generated properties in Grid
 */
public class GridGeneratedProperties extends AbstractReindeerTestUI {

    private GeneratedPropertyContainer container;
    static double MILES_CONVERSION = 0.6214d;
    private Filter filter = new Compare.Greater("miles", 1d);

    @Override
    protected void setup(VaadinRequest request) {
        container = new GeneratedPropertyContainer(createContainer());
        Grid grid = new Grid(container);
        addComponent(grid);

        container.addGeneratedProperty("miles",
                new PropertyValueGenerator<Double>() {

                    @Override
                    public Double getValue(Item item, Object itemId,
                            Object propertyId) {
                        return (Double) item.getItemProperty("km").getValue()
                                * MILES_CONVERSION;
                    }

                    @Override
                    public Class<Double> getType() {
                        return Double.class;
                    }

                    @Override
                    public Filter modifyFilter(Filter filter)
                            throws UnsupportedFilterException {
                        if (filter instanceof Compare.Greater) {
                            Double value = (Double) ((Compare.Greater) filter)
                                    .getValue();
                            value = value / MILES_CONVERSION;
                            return new Compare.Greater("km", value);
                        }
                        return super.modifyFilter(filter);
                    }
                });

        final Button filterButton = new Button("Add filter");
        filterButton.addClickListener(new ClickListener() {

            boolean active = false;

            @Override
            public void buttonClick(ClickEvent event) {
                if (active) {
                    ((Filterable) container).removeContainerFilter(filter);
                    filterButton.setCaption("Add filter");
                    active = false;
                    return;
                }
                ((Filterable) container).addContainerFilter(filter);
                filterButton.setCaption("Remove filter");
                active = true;
            }
        });

        container.addGeneratedProperty("foo",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return item.getItemProperty("foo").getValue() + " "
                                + item.getItemProperty("bar").getValue();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });
        container.removeContainerProperty("bar");
        container.addGeneratedProperty("baz",
                new PropertyValueGenerator<Integer>() {

                    @Override
                    public Integer getValue(Item item, Object itemId,
                            Object propertyId) {
                        return (Integer) item.getItemProperty("bar").getValue();
                    }

                    @Override
                    public Class<Integer> getType() {
                        return Integer.class;
                    }

                    @Override
                    public SortOrder[] getSortProperties(SortOrder order) {
                        return Sort.by("bar", order.getDirection()).build()
                                .toArray(new SortOrder[1]);
                    }
                });

        addComponent(filterButton);
        grid.sort(Sort.by("km").then("bar", SortDirection.DESCENDING));
    }

    private Indexed createContainer() {
        Indexed container = new IndexedContainer();
        container.addContainerProperty("foo", String.class, "foo");
        container.addContainerProperty("bar", Integer.class, 0);
        // km contains double values from 0.0 to 2.0
        container.addContainerProperty("km", Double.class, 0);

        for (int i = 0; i <= 100; ++i) {
            Object itemId = container.addItem();
            Item item = container.getItem(itemId);
            item.getItemProperty("foo").setValue("foo");
            item.getItemProperty("bar").setValue(i);
            item.getItemProperty("km").setValue(i / 5.0d);
        }

        return container;
    }

    @Override
    protected String getTestDescription() {
        return "A Grid with GeneratedPropertyContainer";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13334;
    }

}
