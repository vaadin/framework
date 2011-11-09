package com.vaadin.tests.fieldbinder;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Millionaire;
import com.vaadin.ui.Form;

public class FormOneToMany extends TestBase {

    @Override
    protected void setup() {
        final Form form = new Form();
        addComponent(form);
        form.setItemDataSource(createMillionaireItem());

        // TODO support adding, editing and removing secondary addresses
    }

    protected BeanItem<Millionaire> createMillionaireItem() {
        Millionaire person = new Millionaire("First", "Last", "foo@vaadin.com",
                "02-111 2222", "Ruukinkatu 2-4", 20540, "Turku");

        // TODO this should be made much easier!!!
        BeanItem<Millionaire> item = new BeanItem<Millionaire>(person);
        item.addItemProperty("streetAddress", new NestedMethodProperty<String>(
                person, "address.streetAddress"));
        item.addItemProperty("postalCode", new NestedMethodProperty<Integer>(
                person, "address.postalCode"));
        item.addItemProperty("city", new NestedMethodProperty<String>(person,
                "address.city"));
        item.removeItemProperty("address");

        // TODO for now, hide secondary residences
        item.removeItemProperty("secondaryResidences");

        return item;
    }

    @Override
    protected String getDescription() {
        return "Form with an editable list of sub-objects.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
