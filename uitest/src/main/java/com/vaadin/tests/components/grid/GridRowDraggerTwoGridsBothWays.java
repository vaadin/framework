package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.components.grid.GridRowDragger;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridRowDraggerTwoGridsBothWays extends AbstractGridDnD {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        Grid<Person> left = createGridAndFillWithData(25);
        Grid<Person> right = createGridAndFillWithData(25);

        GridRowDragger<Person> leftToRight = new GridRowDragger<>(left, right);
        GridRowDragger<Person> rightToLeft = new GridRowDragger<>(right, left);

        leftToRight.getGridDragSource()
                .addDragStartListener(event -> rightToLeft.getGridDropTarget()
                        .setDropEffect(DropEffect.NONE));
        leftToRight.getGridDragSource().addDragEndListener(
                event -> rightToLeft.getGridDropTarget().setDropEffect(null));

        rightToLeft.getGridDragSource()
                .addDragStartListener(event -> leftToRight.getGridDropTarget()
                        .setDropEffect(DropEffect.NONE));
        rightToLeft.getGridDragSource().addDragEndListener(
                event -> leftToRight.getGridDropTarget().setDropEffect(null));

        Layout layout = new HorizontalLayout();

        layout.addComponent(left);
        layout.addComponent(right);
        layout.setWidth("100%");
        addComponent(layout);
    }

}
