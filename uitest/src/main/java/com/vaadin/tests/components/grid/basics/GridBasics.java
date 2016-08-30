package com.vaadin.tests.components.grid.basics;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridBasics extends AbstractTestUIWithLog {

    private static class DetailedDetailsGenerator
            implements DetailsGenerator<DataObject> {

        @Override
        public Component apply(DataObject dataObj) {
            CssLayout cssLayout = new CssLayout();
            cssLayout.setHeight("200px");
            cssLayout.setWidth("100%");

            cssLayout.addComponent(
                    new Label("Row Number: " + dataObj.getRowNumber()));
            cssLayout.addComponent(new Label("Date: " + dataObj.getDate()));
            cssLayout.addComponent(
                    new Label("Big Random: " + dataObj.getBigRandom()));
            cssLayout.addComponent(
                    new Label("Small Random: " + dataObj.getSmallRandom()));

            cssLayout
                    .addComponent(new Button("Press me",
                            e -> Notification.show("You clicked on the "
                                    + "button in the details for " + "row "
                                    + dataObj.getRowNumber())));
            return cssLayout;
        }
    }

    private static class PersistingDetailsGenerator
            implements DetailsGenerator<DataObject> {

        private Map<DataObject, Panel> detailsMap = new HashMap<>();

        @Override
        public Component apply(DataObject dataObj) {
            if (!detailsMap.containsKey(dataObj)) {
                Panel panel = new Panel();
                panel.setContent(new Label("One"));
                detailsMap.put(dataObj, panel);
            }
            return detailsMap.get(dataObj);
        }

        public void changeDetailsComponent(MenuItem item) {
            for (DataObject id : detailsMap.keySet()) {
                Panel panel = detailsMap.get(id);
                Label label = (Label) panel.getContent();
                if (label.getValue().equals("One")) {
                    panel.setContent(new Label("Two"));
                } else {
                    panel.setContent(new Label("One"));
                }
            }
        }
    }

    private Grid<DataObject> grid;
    private Map<String, DetailsGenerator<DataObject>> generators = new LinkedHashMap<>();
    private List<DataObject> data;
    private int watchingCount = 0;
    private PersistingDetailsGenerator persistingDetails;

    public GridBasics() {
        generators.put("NULL", null);
        generators.put("Detailed", new DetailedDetailsGenerator());
        generators
                .put("\"Watching\"",
                        dataObj -> new Label("You are watching item id "
                                + dataObj.getRowNumber() + " ("
                                + (watchingCount++) + ")"));
        persistingDetails = new PersistingDetailsGenerator();
        generators.put("Persisting", persistingDetails);
    }

    @Override
    protected void setup(VaadinRequest request) {
        data = DataObject.generateObjects();

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeFull();

        // Create grid
        grid = new Grid<>();
        grid.setItems(data);

        grid.addColumn("Row Number", Integer.class, DataObject::getRowNumber);
        grid.addColumn("Date", Date.class, DataObject::getDate);
        grid.addColumn("HTML String", String.class, DataObject::getHtmlString);
        grid.addColumn("Big Random", Integer.class, DataObject::getBigRandom);
        grid.addColumn("Small Random", Integer.class,
                DataObject::getSmallRandom);

        layout.addComponent(createMenu());
        layout.addComponent(grid);
        addComponent(layout);
    }

    private Component createMenu() {
        MenuBar menu = new MenuBar();
        MenuItem componentMenu = menu.addItem("Component", null);
        createStateMenu(componentMenu.addItem("State", null));
        createSizeMenu(componentMenu.addItem("Size", null));
        createDetailsMenu(componentMenu.addItem("Details", null));
        return menu;
    }

    private void createSizeMenu(MenuItem sizeMenu) {
        MenuItem heightByRows = sizeMenu.addItem("Height by Rows", null);
        DecimalFormat df = new DecimalFormat("0.00");
        Stream.of(0.33, 0.67, 1.00, 1.33, 1.67, 2.00, 2.33, 2.67, 3.00, 3.33,
                3.67, 4.00, 4.33, 4.67)
                .forEach(d -> addGridMethodMenu(heightByRows,
                        df.format(d) + " rows", d, grid::setHeightByRows));
        sizeMenu.addItem("HeightMode Row", item -> {
            grid.setHeightMode(
                    item.isChecked() ? HeightMode.ROW : HeightMode.CSS);
        }).setCheckable(true);

        MenuItem heightMenu = sizeMenu.addItem("Height", null);
        Stream.of(50, 100, 200, 400).map(i -> i + "px").forEach(
                i -> addGridMethodMenu(heightMenu, i, i, grid::setHeight));
    }

    private void createStateMenu(MenuItem stateMenu) {
        MenuItem frozenColMenu = stateMenu.addItem("Frozen column count", null);
        for (int i = -1; i < 3; ++i) {
            addGridMethodMenu(frozenColMenu, "" + i, i,
                    grid::setFrozenColumnCount);
        }
    }

    private <T> void addGridMethodMenu(MenuItem parent, String name, T value,
            Consumer<T> method) {
        parent.addItem(name, menuItem -> method.accept(value));
    }

    /* DetailsGenerator related things */

    private void createDetailsMenu(MenuItem detailsMenu) {
        MenuItem generatorsMenu = detailsMenu.addItem("Generators", null);

        generators.forEach((name, gen) -> generatorsMenu.addItem(name,
                item -> grid.setDetailsGenerator(gen)));

        generatorsMenu.addItem("- Change Component",
                persistingDetails::changeDetailsComponent);

        detailsMenu.addItem("Toggle First", item -> {
            DataObject first = data.get(0);
            openOrCloseDetails(first);
            openOrCloseDetails(first);
        });
        detailsMenu.addItem("Open First", item -> {
            DataObject object = data.get(0);
            openOrCloseDetails(object);
        });
        detailsMenu.addItem("Open 1", item -> {
            DataObject object = data.get(1);
            openOrCloseDetails(object);
        });
        detailsMenu.addItem("Open 995", item -> {
            DataObject object = data.get(995);
            openOrCloseDetails(object);
        });
    }

    private void openOrCloseDetails(DataObject dataObj) {
        grid.setDetailsVisible(dataObj, !grid.isDetailsVisible(dataObj));
    }

}
