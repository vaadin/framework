package com.vaadin.tests.components.customfield;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.util.Address;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Form;

/**
 * Nested form for the Address object of the Person object
 */
public class AddressField extends CustomField<Address> {
    private Form addressForm;
    private final Form parentForm;

    public AddressField() {
        this(null);
    }

    public AddressField(Form parentForm) {
        this.parentForm = parentForm;
    }

    @Override
    protected Component initContent() {
        if (parentForm != null) {
            addressForm = new EmbeddedForm(parentForm);
        } else {
            addressForm = new Form();
        }
        addressForm.setCaption("Address");
        addressForm.setBuffered(true);

        // make sure field changes are sent early
        addressForm.setImmediate(true);
        addressForm.setFooter(null);
        return addressForm;
    }

    @Override
    protected Form getContent() {
        return (Form) super.getContent();
    }

    @Override
    public void setInternalValue(Address address) throws ReadOnlyException {
        // create the address if not given
        if (null == address) {
            address = new Address();
        }

        super.setInternalValue(address);

        // set item data source and visible properties in a single operation to
        // avoid creating fields multiple times
        List<String> visibleProperties = Arrays.asList("streetAddress",
                "postalCode", "city");
        getContent().setItemDataSource(new BeanItem<Address>(address),
                visibleProperties);
    }

    /**
     * commit changes of the address form
     */
    @Override
    public void commit() throws Buffered.SourceException, InvalidValueException {
        addressForm.commit();
        super.commit();
    }

    /**
     * discard changes of the address form
     */
    @Override
    public void discard() throws Buffered.SourceException {
        // Do not discard the top-level value
        // super.discard();
        addressForm.discard();
    }

    @Override
    public boolean isReadOnly() {
        // In this application, the address is modified implicitly by
        // addressForm.commit(), not by setting the Address object for a Person.
        return false;
    }

    @Override
    public Class<Address> getType() {
        return Address.class;
    }
}
