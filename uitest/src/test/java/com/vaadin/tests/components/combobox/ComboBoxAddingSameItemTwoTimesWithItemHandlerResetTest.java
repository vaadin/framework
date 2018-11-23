package com.vaadin.tests.components.combobox;

public class ComboBoxAddingSameItemTwoTimesWithItemHandlerResetTest
        extends ComboBoxSelectingNewItemValueChangeTest {

    @Override
    public void itemHandling(
            ComboBoxSelectingNewItemValueChangeTest.SelectionType selectionType,
            String[] inputs) {
        assertThatSelectedValueIs("");

        // add new item for the first time
        typeInputAndSelect(inputs[0], selectionType);
        assertThatSelectedValueIs(inputs[0]);
        assertValueChange(1);

        reset();

        // add the same item for the 2nd time
        typeInputAndSelect(inputs[0], selectionType);
        assertThatSelectedValueIs(inputs[0]);
        assertValueChange(1);
    }
}
