package com.vaadin.tests.data;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridRefreshWithGetId extends AbstractTestUI {

    private static class TestObject {

        private final int id;
        private String name;

        public TestObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * The class intentionally has strange {@code hashCode()} and
         * {@code equals()} implementation to ensure if {@code Grid} relies on
         * bean id rather than on bean hashcode/equals identification.
         *
         * {@see Object.equals}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            TestObject myObject = (TestObject) o;

            if (id != myObject.id)
                return false;
            return name != null ? name.equals(myObject.name)
                    : myObject.name == null;
        }

        /**
         * The class intentionally has strange {@code hashCode()} and
         * {@code equals()} implementation to ensure if {@code Grid} relies on
         * bean id rather than on bean hashcode/equals identification.
         *
         * {@see Object.hashCode}
         */
        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        List<TestObject> data = new ArrayList<>();
        data.add(new TestObject(0, "blue"));
        data.add(new TestObject(1, "red"));
        data.add(new TestObject(2, "green"));
        data.add(new TestObject(3, "yellow"));
        data.add(new TestObject(4, "purple"));

        ListDataProvider<TestObject> dataProvider = new ListDataProvider<TestObject>(
                data) {

            @Override
            public Object getId(TestObject item) {
                return item.getId();
            }
        };

        Grid<TestObject> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        addComponent(grid);

        grid.addColumn(TestObject::getName);

        Button button = new Button("Change green to black");
        button.addClickListener(event1 -> {
            TestObject myObject = data.get(2);
            myObject.setName("black");
            dataProvider.refreshItem(myObject);
        });
        addComponent(button);
    }
}
