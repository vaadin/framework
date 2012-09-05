package com.vaadin.tests.components.abstractfield;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public abstract class AbstractComponentDataBindingTest extends TestBase
        implements ValueChangeListener {
    private static final Object CAPTION = "CAPTION";
    private Log log = new Log(5);
    private ComboBox localeSelect;

    @Override
    protected void setup() {
        addComponent(log);
        localeSelect = createLocaleSelect();
        addComponent(localeSelect);

        // Causes fields to be created
        localeSelect.setValue(Locale.US);
    }

    private ComboBox createLocaleSelect() {
        ComboBox cb = new ComboBox("Locale");
        cb.addContainerProperty(CAPTION, String.class, "");
        cb.setItemCaptionPropertyId(CAPTION);
        cb.setNullSelectionAllowed(false);
        for (Locale l : Locale.getAvailableLocales()) {
            Item i = cb.addItem(l);
            i.getItemProperty(CAPTION).setValue(
                    l.getDisplayName(Locale.ENGLISH));
        }
        ((Container.Sortable) cb.getContainerDataSource()).sort(
                new Object[] { CAPTION }, new boolean[] { true });
        cb.setImmediate(true);
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateLocale((Locale) localeSelect.getValue());
            }
        });
        return cb;
    }

    protected void updateLocale(Locale locale) {
        VaadinSession.getCurrent().setLocale(locale);
        for (Component c : fields) {
            removeComponent(c);
        }
        fields.clear();
        createFields();
    }

    protected abstract void createFields();

    private Set<Component> fields = new HashSet<Component>();

    @Override
    protected void addComponent(Component c) {
        super.addComponent(c);
        if (c instanceof AbstractField) {
            configureField((AbstractField<?>) c);
            if (c != localeSelect) {
                fields.add(c);
            }
        }
    }

    protected void configureField(AbstractField<?> field) {
        field.setImmediate(true);
        field.addListener(this);
    }

    @Override
    protected String getDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        AbstractField field = (AbstractField) event.getProperty();
        // if (field == localeSelect) {
        // return;
        // }

        Object newValue = field.getValue();
        if (newValue != null) {
            newValue = newValue + " (" + newValue.getClass().getName() + ")";
        }

        String message = "Value of " + field.getCaption() + " changed to "
                + newValue + ".";
        if (field.getPropertyDataSource() != null) {
            Object dataSourceValue = field.getPropertyDataSource().getValue();
            message += "Data model value is " + dataSourceValue;
            message += " (" + field.getPropertyDataSource().getType().getName()
                    + ")";
        }
        log.log(message);

    }

}
