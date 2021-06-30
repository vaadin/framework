package com.vaadin.v7.tests.components.grid;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.validator.IntegerRangeValidator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.renderers.DateRenderer;

@SuppressWarnings("deprecation")
public class GridThemeUI extends AbstractTestUIWithLog {

    protected static String[] columns = { "firstName", "lastName", "gender",
            "birthDate", "age", "alive", "address.streetAddress",
            "address.postalCode", "address.city", "address.country" };

    protected BeanItemContainer<ComplexPerson> container = ComplexPerson
            .createContainer(100);
    {
        container.addNestedContainerBean("address");
    }
    protected ComboBox formType;

    private Component active = null;

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(new Locale("en", "US"));

        final NativeSelect pageSelect = new NativeSelect("Page");
        pageSelect.setImmediate(true);
        pageSelect.setId("page");
        addComponent(pageSelect);

        pageSelect.addItem(new Editor());
        pageSelect.addItem(new HeaderFooter());

        pageSelect.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (active != null) {
                    removeComponent(active);
                }
                active = (Component) pageSelect.getValue();
                addComponent(active);
            }
        });
        pageSelect.setNullSelectionAllowed(false);
        pageSelect.setValue(pageSelect.getItemIds().iterator().next());

    }

    public class Editor extends Grid {
        @Override
        public String toString() {
            return "Editor";
        };

        public Editor() {
            setContainerDataSource(container);
            setColumnOrder((Object[]) columns);
            removeColumn("salary");
            setEditorEnabled(true);
            getColumn("lastName").setEditable(false);
            setSizeFull();
            getColumn("age").getEditorField().addValidator(
                    new IntegerRangeValidator("Must be between 0 and 100", 0,
                            100));
            getColumn("birthDate").setRenderer(new DateRenderer(
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)));
        }
    }

    public class HeaderFooter extends Grid {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        };

        public HeaderFooter() {
            setContainerDataSource(container);
            setColumnOrder((Object[]) columns);
            HeaderRow row = addHeaderRowAt(0);
            row.join("firstName", "lastName").setHtml("<b>Name</b>");
            Button b = new Button("The address, yo");
            b.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    HeaderRow row = addHeaderRowAt(0);
                    List<Object> pids = new ArrayList<>();
                    for (Column c : getColumns()) {
                        pids.add(c.getPropertyId());
                    }
                    row.join(pids.toArray()).setText("The big header");
                }
            });
            b.setSizeFull();
            row.join("address.streetAddress", "address.postalCode",
                    "address.city", "address.country").setComponent(b);
            // NOTE: can't set column width that is too narrow to accommodate
            // cell paddings, e.g. 25 would not be enough for Valo
            getColumn("age").setWidth(42);
            removeColumn("salary");
            setEditorEnabled(true);
            setSizeFull();
            getColumn("age").getEditorField().addValidator(
                    new IntegerRangeValidator("Must be between 0 and 100", 0,
                            100));
            getColumn("birthDate").setRenderer(new DateRenderer(
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)));

            addFooterRowAt(0);
        }
    }

}
