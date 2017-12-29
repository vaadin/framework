package com.vaadin.tests.components.abstractsingleselect;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;

@Widgetset("com.vaadin.DefaultWidgetSet")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AbstractSingleSelection extends AbstractTestUI {

    /* Initial placeholder component */
    AbstractSingleSelect<String> component = new ComboBox<>();

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<Class<? extends AbstractSingleSelect>> componentSelect = new NativeSelect<>();
        componentSelect.setItems(RadioButtonGroup.class, NativeSelect.class,
                ComboBox.class);
        componentSelect.setItemCaptionGenerator(Class::getSimpleName);

        componentSelect.setEmptySelectionAllowed(false);
        componentSelect
                .addValueChangeListener(singleSelectClass -> createComponent(
                        singleSelectClass.getValue()));

        addComponent(componentSelect);
        addComponent(component); // This will be replaced in createComponent
        addComponent(
                new Button("Deselect",
                        event -> component.setSelectedItem(null)));
        addComponent(new Button("Select Bar",
                event -> component.setSelectedItem("Bar")));
        addComponent(new Button("Refresh",
                event -> component.getDataProvider().refreshAll()));

        // Select a value from native select to create the initial component
        componentSelect.getDataProvider().fetch(new Query<>()).findFirst()
                .ifPresent(componentSelect::setSelectedItem);
    }

    private void createComponent(
            Class<? extends AbstractSingleSelect> singleSelectClass) {
        try {
            AbstractSingleSelect<String> select = singleSelectClass
                    .newInstance();
            select.setItems("Foo", "Bar", "Baz", "Reset");
            select.setSelectedItem("Bar");

            select.addValueChangeListener(event -> {
                if ("Reset".equals(event.getValue())) {
                    select.setSelectedItem("Bar");
                }
            });

            select.setId("testComponent");

            replaceComponent(component, select);
            component = select;
        } catch (InstantiationException | IllegalAccessException e1) {
            throw new RuntimeException("Component creation failed", e1);
        }
    }
}
