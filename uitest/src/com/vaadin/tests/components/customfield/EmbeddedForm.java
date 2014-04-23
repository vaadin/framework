package com.vaadin.tests.components.customfield;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Layout;

/**
 * Form that displays its fields in the layout of another form.
 * 
 * The fields are still logically part of this form even though they are in the
 * layout of the parent form. The embedded form itself is automatically hidden.
 * 
 * TODO Known issue: any field factory creating an {@link EmbeddedForm}
 * (directly or indirectly) should re-use the field once it has been created to
 * avoid the creation of duplicate fields when e.g. setting the visible item
 * properties.
 */
public class EmbeddedForm extends Form {
    private Form parentForm;
    private Map<Object, Field> fields = new HashMap<Object, Field>();

    /**
     * Create a form that places its fields in another {@link Form}.
     * 
     * @param parentForm
     *            form to which to embed the fields, not null
     */
    public EmbeddedForm(Form parentForm) {
        this.parentForm = parentForm;
        setVisible(false);
    }

    @Override
    protected void attachField(Object propertyId, Field field) {
        if (propertyId == null || field == null) {
            return;
        }

        Layout layout = parentForm.getLayout();

        Field oldField = fields.get(propertyId);
        if (oldField != null) {
            layout.removeComponent(oldField);
        }

        fields.put(propertyId, field);

        if (layout instanceof CustomLayout) {
            ((CustomLayout) layout).addComponent(field, propertyId.toString());
        } else {
            layout.addComponent(field);
        }
    }

    @Override
    public boolean removeItemProperty(Object id) {
        // remove the field from the parent layout if already added there
        parentForm.getLayout().removeComponent(fields.get(id));
        fields.remove(id);

        return super.removeItemProperty(id);
    }
}
