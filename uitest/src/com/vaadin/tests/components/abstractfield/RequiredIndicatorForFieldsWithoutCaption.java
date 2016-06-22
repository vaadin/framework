package com.vaadin.tests.components.abstractfield;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class RequiredIndicatorForFieldsWithoutCaption extends AbstractTestUI {
    private Set<Field> fields = new HashSet<Field>();

    @Override
    protected void setup(VaadinRequest request) {

        CheckBox required = new CheckBox("Fields required", true);
        required.setImmediate(true);
        required.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean required = (Boolean) event.getProperty().getValue();
                for (Field f : fields) {
                    f.setRequired(required);
                }
            }
        });
        addComponent(required);
        addComponent(createWrappedTextField(new GridLayout(2, 1)));
        addComponent(createWrappedTextField(new VerticalLayout()));
        addComponent(createWrappedTextField(new HorizontalLayout()));
        AbsoluteLayout al = new AbsoluteLayout();
        al.setWidth("400px");
        al.setHeight("100px");
        addComponent(createWrappedTextField(al));
        addComponent(createWrappedTextField(new CssLayout()));
    }

    /**
     * @param gridLayout
     * @return
     */
    private Component createWrappedTextField(ComponentContainer container) {
        TextField tf = new TextField();
        tf.setRequired(true);
        tf.setWidth(200, Unit.PIXELS);
        fields.add(tf);
        container.addComponent(new Label(container.getClass().getSimpleName()));
        container.addComponent(tf);
        if (container instanceof AbsoluteLayout) {
            ((AbsoluteLayout) container).getPosition(tf).setLeft(100.0f,
                    Unit.PIXELS);
            ((AbsoluteLayout) container).getPosition(tf).setTop(50.0f,
                    Unit.PIXELS);
        }

        return container;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test for ensuring that the required indicator is visible for fields even when they would not otherwise have a caption";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12077;
    }
}
