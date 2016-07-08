package com.vaadin.tokka.tests.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.SingleSelection;
import com.vaadin.tokka.ui.components.grid.Grid;
import com.vaadin.tokka.ui.components.nativeselect.NativeSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
public class ListingTestUI extends AbstractTestUI {

    static Random r = new Random();

    static class Bean {
        private String value;
        private Integer intVal;

        public Bean(String value, Integer intVal) {
            this.value = value;
            this.intVal = intVal;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getIntVal() {
            return intVal;
        }

        public void setIntVal(Integer intVal) {
            this.intVal = intVal;
        }

        @Override
        public String toString() {
            return "Bean { value: " + value + ", intVal: " + intVal + " }";
        }

        public static List<Bean> generateRandomBeans() {
            String[] values = new String[] { "Foo", "Bar", "Baz" };

            List<Bean> beans = new ArrayList<Bean>();
            for (int i = 0; i < 100; ++i) {
                beans.add(new Bean(values[r.nextInt(values.length)], r
                        .nextInt(100)));
            }
            return beans;

        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        final List<String> options = createOptions();
        NativeSelect<String> select = new NativeSelect<>(
                DataSource.create(options));
        layout.addComponent(select);

        layout.addComponent(new Button("Notify", e -> select
                .getSelectionModel().getSelected()
                .forEach(s -> Notification.show(s))));
        layout.addComponent(new Button("Random select", e -> {
            String value = options.get(r.nextInt(options.size()));
            select.getSelectionModel().select(value);
        }));

        Grid<Bean> grid = new Grid<Bean>();
        addComponent(layout);
        layout.addComponent(grid);
        grid.addColumn("String Value", Bean::getValue);
        grid.addColumn("Integer Value", Bean::getIntVal);
        grid.addColumn("toString", Bean::toString);
        grid.setDataSource(DataSource.create(Bean.generateRandomBeans()));

        addComponent(new Button("Toggle Grid Selection",
                new Button.ClickListener() {

                    private boolean hasSelection = true;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (hasSelection) {
                            grid.setSelectionModel(null);
                        } else {
                            grid.setSelectionModel(new SingleSelection<>());
                        }
                        hasSelection = !hasSelection;
                    }
                }));

    }

    private List<String> createOptions() {
        List<String> options = new ArrayList<>();
        Stream.of(1, 2, 3, 4, 5).map(i -> "Option " + i).forEach(options::add);
        return options;
    }

}