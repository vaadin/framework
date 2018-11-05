package com.vaadin.tests.components.grid;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.themes.Reindeer;

/**
 * Tests that details row resizes along with the contents properly.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("deprecation")
public class GridLayoutDetailsRowResize extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = createGrid();
        grid.setSizeFull();

        addComponent(new Button("Toggle theme", event -> {
            if (ValoTheme.THEME_NAME.equals(getUI().getTheme())) {
                getUI().setTheme(Reindeer.THEME_NAME);
            } else {
                getUI().setTheme(ValoTheme.THEME_NAME);
            }
        }));

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
            lbl4.setVisible(false);
            detailsLayout.addComponent(lbl4);

            final Button button = new Button("Toggle visibility",
                    event -> lbl4.setVisible(!lbl4.isVisible()));
            button.setId("btn");
            detailsLayout.addComponent(button);

            return detailsLayout;
        });

        grid.addItemClickListener(click -> {
            final Person person = click.getItem();
            grid.setDetailsVisible(person, !grid.isDetailsVisible(person));
        });

        addComponent(new Button("Open details", event -> {
            for (Object itemId : ((ListDataProvider<?>) grid.getDataProvider())
                    .getItems()) {
                if (itemId instanceof Person) {
                    grid.setDetailsVisible((Person) itemId, true);
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
