package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.TestDataGenerator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDragger;
import com.vaadin.ui.components.grid.GridDropTarget;

public abstract class AbstractGridDnD extends AbstractTestUIWithLog {

    protected final Layout controls = new HorizontalLayout();

    protected void initializeTestFor(GridDragger<Person> gridDragger) {
        initializeTestFor(gridDragger.getGridDragSource().getGrid(),
                gridDragger.getGridDropTarget().getGrid(),
                gridDragger.getGridDragSource(),
                gridDragger.getGridDropTarget());
    }

    protected void initializeTestFor(Grid<Person> source, Grid<Person> target,
            GridDragSource<Person> dragSource,
            GridDropTarget<Person> dropTarget) {
        // Layout the two grids
        Layout layout = new HorizontalLayout();

        layout.addComponent(source);
        layout.addComponent(target); // noop if source == target
        layout.setWidth("100%");

        // Selection modes
        List<Grid.SelectionMode> selectionModes = Arrays
                .asList(Grid.SelectionMode.SINGLE, Grid.SelectionMode.MULTI);
        RadioButtonGroup<Grid.SelectionMode> selectionModeSelect = new RadioButtonGroup<>(
                "Selection mode", selectionModes);
        selectionModeSelect.setSelectedItem(Grid.SelectionMode.SINGLE);
        selectionModeSelect.addValueChangeListener(
                event -> source.setSelectionMode(event.getValue()));

        // Drop locations
        List<DropMode> dropLocations = Arrays.asList(DropMode.values());
        RadioButtonGroup<DropMode> dropLocationSelect = new RadioButtonGroup<>(
                "Allowed drop location", dropLocations);
        dropLocationSelect.setSelectedItem(DropMode.BETWEEN);
        dropLocationSelect.addValueChangeListener(
                event -> dropTarget.setDropMode(event.getValue()));

        CheckBox transitionCheckBox = new CheckBox("Transition layout", false);
        transitionCheckBox.addValueChangeListener(event -> {
            if (event.getValue()) {
                layout.addStyleName("transitioned");
            } else {
                layout.removeStyleName("transitioned");
            }
        });

        RadioButtonGroup<Integer> frozenColumnSelect = new RadioButtonGroup<>(
                "Frozen columns", Arrays.asList(new Integer[] { -1, 0, 1 }));
        frozenColumnSelect.setValue(source.getFrozenColumnCount());
        frozenColumnSelect.addValueChangeListener(event -> {
            source.setFrozenColumnCount(event.getValue());
            target.setFrozenColumnCount(event.getValue());
        });

        controls.addComponents(selectionModeSelect, dropLocationSelect,
                transitionCheckBox, frozenColumnSelect);

        addComponents(controls, layout);

        getPage().getStyles()
                .add(".transitioned { transform: translate(-30px, 30px);}");
    }

    protected Grid<Person> createGridAndFillWithData(int numberOfItems) {
        Grid<Person> grid = new Grid<>();
        grid.setWidth("100%");

        grid.setItems(generateItems(numberOfItems));
        grid.addColumn(
                person -> person.getFirstName() + " " + person.getLastName())
                .setCaption("Name");
        grid.addColumn(person -> person.getAddress().getStreetAddress())
                .setCaption("Street Address");
        grid.addColumn(person -> person.getAddress().getCity())
                .setCaption("City");

        return grid;
    }

    private List<Person> generateItems(int num) {
        return Stream.generate(() -> generateRandomPerson(new Random()))
                .limit(num).collect(Collectors.toList());
    }

    private Person generateRandomPerson(Random r) {
        return new Person(TestDataGenerator.getFirstName(r),
                TestDataGenerator.getLastName(r), "foo@bar.com",
                TestDataGenerator.getPhoneNumber(r),
                TestDataGenerator.getStreetAddress(r),
                TestDataGenerator.getPostalCode(r),
                TestDataGenerator.getCity(r));
    }
}
