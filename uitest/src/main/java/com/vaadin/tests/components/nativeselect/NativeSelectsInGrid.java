package com.vaadin.tests.components.nativeselect;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class NativeSelectsInGrid extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout(new Label(
                "Keep opening the editor and selecting an enum value. Eventually the list will not show "
                        + "all options"),
                createGrid());
        NativeSelect<Enum> ns = new NativeSelect<>();
        ns.setEmptySelectionAllowed(false);
        ns.setItems(Enum.values());

        NativeSelect<Enum> ns2 = new NativeSelect<>();
        ns2.setItems(Enum.values());
        addComponent(ns);
        addComponent(ns2);
        addComponent(new Button("Change visibility of NS", e -> {
            ns2.setVisible(!ns2.isVisible());
        }));
        addComponent(layout);

    }

    public Component createGrid() {
        Grid<Person> grid = new Grid<>();
        grid.setItems(new Person(Enum.baz), new Person(Enum.foo),
                new Person(Enum.bizzle));
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        NativeSelect<Enum> ns = new NativeSelect<>();
        ns.setEmptySelectionAllowed(false);
        ns.setItems(Enum.values());
        grid.addColumn(Person::getEnumValue).setCaption("Enum value")
                .setEditorComponent(ns, Person::setEnumValue);
        grid.getEditor().setEnabled(true);
        return grid;
    }

    enum Enum {
        foo, bar, baz, bizzle, quux
    }

    public static class Person {
        Enum enumValue;

        public Person(Enum progress) {
            this.enumValue = progress;
        }

        public Enum getEnumValue() {
            return enumValue;
        }

        public void setEnumValue(Enum enumValue) {
            this.enumValue = enumValue;
        }
    }
}
