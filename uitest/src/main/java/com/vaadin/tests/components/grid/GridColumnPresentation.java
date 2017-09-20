package com.vaadin.tests.components.grid;

import com.vaadin.data.HasValue;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.TextRenderer;

/**
 * An example for using a different value and presentation types in a Grid
 * column.
 */
public class GridColumnPresentation extends AbstractTestUI {

    /**
     * Dummy HasValue for Address.
     */
    private static class AddressField extends Composite
            implements HasValue<Address> {

        Address address;
        private Label label;

        public AddressField() {
            super();

            label = new Label();
            setCompositionRoot(label);
        }

        @Override
        public void setValue(Address value) {
            Address oldAddress = address;
            address = value;
            label.setValue(String.valueOf(address));
            fireEvent(new ValueChangeEvent<>(this, oldAddress, false));
        }

        @Override
        public Address getValue() {
            return address;
        }

        @Override
        public Registration addValueChangeListener(
                ValueChangeListener<Address> listener) {
            return addListener(ValueChangeEvent.class, listener,
                    ValueChangeListener.VALUE_CHANGE_METHOD);
        }

        @Override
        public boolean isReadOnly() {
            return super.isReadOnly();
        }

        @Override
        public void setReadOnly(boolean readOnly) {
            super.setReadOnly(readOnly);
        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return super.isRequiredIndicatorVisible();
        }

        @Override
        public void setRequiredIndicatorVisible(boolean visible) {
            super.setRequiredIndicatorVisible(visible);
        }

        @Override
        public void setComponentError(ErrorMessage componentError) {
            label.setComponentError(componentError);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> personGrid = new Grid<>();
        personGrid.setItems(Person.createTestPerson1(),
                Person.createTestPerson2());
        personGrid.addColumn(Person::getAddress)
                .setRenderer(
                        address -> address.getCity() + " "
                                + address.getCountry().name(),
                        new TextRenderer())
                .setCaption("Address")
                .setEditorComponent(new AddressField(), Person::setAddress);
        personGrid.getEditor().setEnabled(true);
        addComponent(personGrid);
    }

}
