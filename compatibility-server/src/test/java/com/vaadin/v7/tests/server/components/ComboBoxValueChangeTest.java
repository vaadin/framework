package com.vaadin.v7.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import com.vaadin.v7.tests.server.component.abstractfield.AbstractFieldValueChangeTestBase;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;

/**
 * Check that the value change listener for a combo box is triggered exactly
 * once when setting the value, at the correct time.
 *
 * See <a href="http://dev.vaadin.com/ticket/4394">Ticket 4394</a>.
 */
public class ComboBoxValueChangeTest
        extends AbstractFieldValueChangeTestBase<Object> {

    @Before
    public void setUp() {
        ComboBox combo = new ComboBox();
        combo.addItem("myvalue");
        super.setUp(combo);
    }

    @Override
    protected void setValue(AbstractField<Object> field) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("selected", new String[] { "myvalue" });
        ((ComboBox) field).changeVariables(field, variables);
    }

}
