package com.vaadin.tests.components.twincolselect;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.TwinColSelect;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TwinColSelectAndIdBasedDataProvider extends AbstractTestUIWithLog {

    public static class MyObject {
        private int id;
        private String name;

        public MyObject() {

        }

        public MyObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "MyObject{" + "id=" + id + ", name='" + name + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MyObject myObject = (MyObject) o;
            return id == myObject.id && Objects.equals(name, myObject.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        ListDataProvider<MyObject> dataProvider = new ListDataProvider<MyObject>(
                Arrays.asList(new MyObject(0, "Zero"),
                        new MyObject(1, "One"))) {

            @Override
            public Object getId(MyObject item) {
                return item.getId();
            }
        };

        TwinColSelect<MyObject> twinColSelect = new TwinColSelect<>();
        twinColSelect.setDataProvider(dataProvider);

        twinColSelect.setValue(Collections.singleton(new MyObject(1, null)));

        twinColSelect.addValueChangeListener(
                event1 -> log.log("value: " + event1.getValue()));

        addComponent(twinColSelect);
        addComponent(new Button("Deselect id=1", e -> {
            twinColSelect.deselect(new MyObject(1, "foo"));
        }));
    }

}
