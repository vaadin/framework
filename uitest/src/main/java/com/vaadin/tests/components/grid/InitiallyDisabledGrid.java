package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class InitiallyDisabledGrid extends UI {

    public static class NotAPersonJustStringAndInt {
        private String name;

        public NotAPersonJustStringAndInt() {
        }

        public NotAPersonJustStringAndInt(String string, int i) {
            name = string;
            age = i;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        private int age;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setSizeFull();
        layout.setWidth("600px");
        layout.setHeight("600px");
        final Grid g = createGrid();
        Button button = new Button("Sample button");

        layout.addComponent(button);
        VerticalLayout l = new VerticalLayout();
        l.setSizeFull();
        l.addComponent(g);

        layout.addComponent(l);
        layout.setExpandRatio(l, 1.0f);
    }

    private Grid createGrid() {
        // Have some data
        Collection<NotAPersonJustStringAndInt> people = new ArrayList<NotAPersonJustStringAndInt>();
        for (int i = 0; i < 100; i++) {
            people.add(new NotAPersonJustStringAndInt("A " + i, i));
        }
        // Have a container of some type to contain the data
        BeanItemContainer<NotAPersonJustStringAndInt> container = new BeanItemContainer<NotAPersonJustStringAndInt>(
                NotAPersonJustStringAndInt.class, people);

        // Create a grid bound to the container
        Grid grid = new Grid(container);
        grid.setSizeFull();
        grid.setColumnOrder("name", "age");

        grid.setEnabled(false);

        return grid;

    }

}