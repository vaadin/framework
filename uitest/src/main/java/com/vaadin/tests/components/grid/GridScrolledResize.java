package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GridScrolledResize extends AbstractTestUI {

    private Label spaceGrabber;

    @Override
    public void setup(VaadinRequest vaadinRequest) {

        Grid<Person> grid = new Grid<>(Person.class);
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.setItems(createPersons());

        HorizontalLayout splitter = new HorizontalLayout();
        splitter.setWidthFull();
        splitter.addComponent(grid);
        splitter.setExpandRatio(grid, 1f);

        addComponent(splitter);

        addComponent(new Button("Toggle component", e -> {

            if (spaceGrabber == null) {
                spaceGrabber = new Label("I'm a space grabber...");
                spaceGrabber.setWidth(500, Unit.PIXELS);
            }

            if (spaceGrabber.isAttached()) {
                splitter.removeComponent(spaceGrabber);
            } else {
                splitter.addComponent(spaceGrabber);
            }

        }));

    }

    private List<Person> createPersons() {
        ArrayList<Person> people = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            people.add(new Person("First", "Last"));
        }

        return people;

    }

    public static class Person {
        private String fName;
        private String lName;
        private String col3;
        private String col4;
        private String col5;
        private String col6;
        private String col7;
        private String col8;
        private String col9;
        private String col10;
        private String col11;
        private String col12;

        public String getCol8() {
            return col8;
        }

        public void setCol8(String col8) {
            this.col8 = col8;
        }

        public String getCol9() {
            return col9;
        }

        public void setCol9(String col9) {
            this.col9 = col9;
        }

        public String getCol10() {
            return col10;
        }

        public void setCol10(String col10) {
            this.col10 = col10;
        }

        public String getCol11() {
            return col11;
        }

        public void setCol11(String col11) {
            this.col11 = col11;
        }

        public String getCol12() {
            return col12;
        }

        public void setCol12(String col12) {
            this.col12 = col12;
        }

        public Person(String fName, String lName) {
            this.fName = fName;
            this.lName = lName;

            int i = 3;
            col3 = fName + " " + lName + i++;
            col4 = col3 + i++;
            col5 = col3 + i++;
            col6 = col3 + i++;
            col7 = col3 + i++;
            col8 = col3 + i++;
            col9 = col3 + i++;
            col10 = col3 + i++;
            col11 = col3 + i++;
            col12 = col3 + i++;
        }

        public String getfName() {
            return fName;
        }

        public void setfName(String fName) {
            this.fName = fName;
        }

        public String getlName() {
            return lName;
        }

        public void setlName(String lName) {
            this.lName = lName;
        }

        public String getCol3() {
            return col3;
        }

        public void setCol3(String col3) {
            this.col3 = col3;
        }

        public String getCol4() {
            return col4;
        }

        public void setCol4(String col4) {
            this.col4 = col4;
        }

        public String getCol5() {
            return col5;
        }

        public void setCol5(String col5) {
            this.col5 = col5;
        }

        public String getCol6() {
            return col6;
        }

        public void setCol6(String col6) {
            this.col6 = col6;
        }

        public String getCol7() {
            return col7;
        }

        public void setCol7(String col7) {
            this.col7 = col7;
        }
    }
}
