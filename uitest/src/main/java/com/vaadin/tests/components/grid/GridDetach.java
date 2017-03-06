package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridDetach extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setCaption("asd");
        grid.addColumn(Person::getAge).setCaption("foobar");

        addComponent(grid);
        addComponent(new Button("Detach grid", e -> removeComponent(grid)));
    }

}
