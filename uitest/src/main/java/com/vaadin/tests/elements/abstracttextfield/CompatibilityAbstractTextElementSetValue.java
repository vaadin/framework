package com.vaadin.tests.elements.abstracttextfield;

import java.util.Date;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class CompatibilityAbstractTextElementSetValue extends AbstractTestUI {

    AbstractTextField[] comps = { new TextField(), new PasswordField(),
            new TextArea() };
    // one extra label for DateField, which we create in a separate method
    Label[] eventCountLabels = new Label[comps.length + 1];
    int[] eventCounters = new int[comps.length + 1];
    public static final String INITIAL_VALUE = "initial value";
    public static final Date INITIAL_DATE = new Date(2016, 5, 7);

    @Override
    protected void setup(VaadinRequest request) {

        for (int i = 0; i < comps.length; i++) {
            comps[i].setValue(INITIAL_VALUE);
            eventCountLabels[i] = new Label();
            eventCountLabels[i].setCaption("event count");
            // create an valueChangeListener, to count valueChangeListener
            // events
            comps[i].addValueChangeListener(new ValueChangeCounter(i));
            addComponent(comps[i]);
            addComponent(eventCountLabels[i]);

        }
        // add one extra label for DateField, which we create in a separate
        // method
        eventCountLabels[comps.length] = new Label();
        DateField df = createDateField();
        df.addValueChangeListener(new ValueChangeCounter(comps.length));
        addComponent(df);
        eventCountLabels[comps.length].setCaption("event  count");
        addComponent(eventCountLabels[comps.length]);
    }

    @Override
    protected String getTestDescription() {
        return "Test type method of AbstractTextField components";
    }

    private DateField createDateField() {
        DateField df = new DateField();
        df.setValue(INITIAL_DATE);
        return df;
    }

    @Override
    protected Integer getTicketNumber() {
        return 13365;
    }

    // helper class, which increases valuechange event counter
    private class ValueChangeCounter implements ValueChangeListener {
        private int index;

        public ValueChangeCounter(int index) {
            this.index = index;
        }

        @Override
        public void valueChange(ValueChangeEvent event) {
            eventCounters[index]++;
            String value = "" + eventCounters[index];
            eventCountLabels[index].setValue(value);
        }

    }
}
