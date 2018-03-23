package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBoxGroup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class CheckCheckBoxGroupWithId extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        DataProvider<MyObject, SerializablePredicate<MyObject>> dataProvider = new ListDataProvider<MyObject>(
                Arrays.asList(new MyObject("Yellow", "real"),
                        new MyObject("Red", "real"))) {
            @Override
            public Object getId(MyObject item) {
                return item.getName();
            }
        };

        CheckBoxGroup<MyObject> checkBoxGroup = new CheckBoxGroup<>();
        checkBoxGroup.setItemCaptionGenerator(MyObject::getName);
        checkBoxGroup.setDataProvider(dataProvider);
        checkBoxGroup.setValue(
                new HashSet<>(Arrays.asList(new MyObject("Yellow", null))));

        addComponent(checkBoxGroup);
        addButton("Deselect",
                event -> checkBoxGroup.deselect(new MyObject("Yellow", "XX")));
    }

    public static class MyObject {
        private final String name;
        private final String other;

        public MyObject(String name, String other) {
            this.name = name;
            this.other = other;
        }

        public String getName() {
            return name;
        }

        public String getOther() {
            return other;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            MyObject myObject = (MyObject) o;
            return Objects.equals(name, myObject.name)
                    && Objects.equals(other, myObject.other);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, other);
        }
    }
}
