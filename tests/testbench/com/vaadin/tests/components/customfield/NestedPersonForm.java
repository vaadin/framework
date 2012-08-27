package com.vaadin.tests.components.customfield;

import java.util.Arrays;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout.MarginHandler;

/**
 * Example of nested forms
 */
public class NestedPersonForm extends Form {
    private BeanItem<Person> beanItem;
    private final boolean embeddedAddress;

    /**
     * Creates a person form which contains nested form for the persons address
     */
    public NestedPersonForm(Person person, boolean embeddedAddress) {
        this.embeddedAddress = embeddedAddress;

        beanItem = new BeanItem<Person>(person);
        setCaption("Update person details");
        setBuffered(true);
        setFormFieldFactory(new PersonFieldFactory());
        // set the data source and the visible fields
        // Note that if the nested form is the first or last field in the parent
        // form, styles from the parent (padding, ...) may leak to its contents.
        setItemDataSource(beanItem, Arrays.asList("firstName", "lastName",
                "address", "email", "phoneNumber"));
        getFooter().addComponent(getButtonsLayout());
        ((MarginHandler) getFooter()).setMargin(new MarginInfo(false, false,
                true, true));
    }

    /**
     * Get apply and discard button in the layout
     */
    private Component getButtonsLayout() {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        Button discardChanges = new Button("Discard changes",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        NestedPersonForm.this.discard();
                    }
                });
        buttons.addComponent(discardChanges);
        buttons.setComponentAlignment(discardChanges, Alignment.MIDDLE_LEFT);

        Button apply = new Button("Apply", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    NestedPersonForm.this.commit();
                } catch (Exception e) {
                    // Ignored, we'll let the Form handle the errors
                }
            }
        });
        buttons.addComponent(apply);
        return buttons;
    }

    /**
     * Field factory for person form
     */
    private class PersonFieldFactory extends DefaultFieldFactory {
        // reuse the address field - required by EmbeddedForm
        private AddressField addressField;

        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            Field f = super.createField(item, propertyId, uiContext);
            if ("address".equals(propertyId)) {
                // create a custom field for the Address object
                if (addressField == null) {
                    Form form = (embeddedAddress && uiContext instanceof Form) ? (Form) uiContext
                            : null;
                    addressField = new AddressField(form);
                }
                f = addressField;
            }
            return f;
        }
    }
}
