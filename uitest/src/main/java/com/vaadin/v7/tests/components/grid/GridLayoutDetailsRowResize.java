package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.DetailsGenerator;
import com.vaadin.v7.ui.Grid.RowReference;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Reindeer;

/**
 * Tests that details row resizes along with the contents properly.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("deprecation")
public class GridLayoutDetailsRowResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.setSizeFull();
        grid.addColumn("name", String.class);
        grid.addColumn("born", Integer.class);

        grid.addRow("Nicolaus Copernicus", 1543);
        grid.addRow("Galileo Galilei", 1564);
        grid.addRow("Johannes Kepler", 1571);

        addComponent(grid);

        grid.setDetailsGenerator(new DetailsGenerator() {
            @Override
            public Component getDetails(final RowReference rowReference) {
                final VerticalLayout detailsLayout = new VerticalLayout();
                detailsLayout.setId("details");
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
                lbl4.setVisible(false);
                detailsLayout.addComponent(lbl4);

                final Button button = new Button("Toggle visibility",
                        new Button.ClickListener() {

                            @Override
                            public void buttonClick(ClickEvent event) {
                                lbl4.setVisible(!lbl4.isVisible());
                            }
                        });
                button.setId("btn");
                detailsLayout.addComponent(button);

                return detailsLayout;
            }
        });

        grid.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(final ItemClickEvent event) {
                final Object itemId = event.getItemId();
                grid.setDetailsVisible(itemId, !grid.isDetailsVisible(itemId));
            }
        });

        addComponent(new Button("Toggle theme", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (ValoTheme.THEME_NAME.equals(getUI().getTheme())) {
                    getUI().setTheme(Reindeer.THEME_NAME);
                } else {
                    getUI().setTheme(ValoTheme.THEME_NAME);
                }
            }
        }));

        addComponent(new Button("Open details", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                for (Object itemId : grid.getContainerDataSource()
                        .getItemIds()) {
                    grid.setDetailsVisible(itemId, true);
                }
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Detail row should be correctly resized when its contents change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7341;
    }
}
