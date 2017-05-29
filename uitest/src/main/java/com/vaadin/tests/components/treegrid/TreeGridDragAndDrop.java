package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.HierarchicalTestBean;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridDragAndDrop extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        TreeGrid<HierarchicalTestBean> grid;
        grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(HierarchicalTestBean::toString).setCaption("String")
                .setId("string");
        grid.addColumn(HierarchicalTestBean::getDepth).setCaption("Depth")
                .setId("depth").setDescriptionGenerator(
                t -> "Hierarchy depth: " + t.getDepth());
        grid.addColumn(HierarchicalTestBean::getIndex)
                .setCaption("Index on this depth").setId("index");
        grid.setHierarchyColumn("string");
        grid.setDataProvider(new LazyHierarchicalDataProvider(3, 2));

        grid.setId("testComponent");

        addComponent(grid);
    }

//    private TreeGrid<Person> createGridAndFillWithData(int numberOfItems) {
//        Grid<Person> grid = new Grid<>();
//        grid.setWidth("100%");
//
//        grid.setItems(generateItems(numberOfItems));
//        grid.addColumn(
//                person -> person.getFirstName() + " " + person.getLastName())
//                .setCaption("Name");
//        grid.addColumn(person -> person.getAddress().getStreetAddress())
//                .setCaption("Street Address");
//        grid.addColumn(person -> person.getAddress().getCity())
//                .setCaption("City");
//
//        return grid;
//    }
}
