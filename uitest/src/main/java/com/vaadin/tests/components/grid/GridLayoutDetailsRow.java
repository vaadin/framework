package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * Tests that details row displays GridLayout contents properly.
 *
 * @author Vaadin Ltd
 */
public class GridLayoutDetailsRow extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = createGrid();
        grid.setSizeFull();

        addComponent(grid);

        grid.setDetailsGenerator(item -> {
            final GridLayout detailsLayout = new GridLayout();
            detailsLayout.setSizeFull();
            detailsLayout.setHeightUndefined();

            final Label lbl1 = new Label("test1");
            lbl1.setId("lbl1");
            lbl1.setWidth("200px");
            detailsLayout.addComponent(lbl1);

            final Label lbl2 = new Label("test2");
            lbl2.setId("lbl2");
            detailsLayout.addComponent(lbl2);

            final Label lbl3 = new Label("test3");
            lbl3.setId("lbl3");
            detailsLayout.addComponent(lbl3);

            final Label lbl4 = new Label("test4");
            lbl4.setId("lbl4");
            detailsLayout.addComponent(lbl4);

            return detailsLayout;
        });

        grid.addItemClickListener(click -> {
            final Person person = click.getItem();
            grid.setDetailsVisible(person, !grid.isDetailsVisible(person));
        });
    }

    @Override
    protected String getTestDescription() {
        return "GridLayout as part of Grid detail row should be correctly computed/displayed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18619;
    }
}
