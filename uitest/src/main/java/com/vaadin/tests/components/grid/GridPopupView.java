package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridPopupView extends UI {

    private static final long serialVersionUID = 1L;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        VerticalLayout vl = new VerticalLayout();

        vl.addComponent(new Label("Grid Test with Vaadin Version: "
                + com.vaadin.shared.Version.getFullVersion()));

        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Car 1"));
        cars.add(new Car("Car 2"));
        cars.add(new Car("Car 3"));

        Grid<Car> carGrid = new Grid<>("My Cars: ", cars);

        PopupView popupView = new PopupView(
                "Show grid (click me multiple times)", carGrid);
        popupView.setHideOnMouseOut(false);

        Column<Car, String> col = carGrid
                .addColumn(new ValueProvider<Car, String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String apply(Car car) {
                        return car.getDescription();
                    }
                });

        col.setCaption("Cars");

        vl.addComponent(popupView);
        setContent(vl);
    }

}

// internal class for car demo
class Car {

    private final String description;

    public Car(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
