package com.vaadin.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

/**
 * Check that the value change listener for a combo box is triggered exactly
 * once when setting the value, at the correct time.
 * 
 * See <a href="http://dev.vaadin.com/ticket/4394">Ticket 4394</a>.
 */
public class TestComboBoxValueChange extends
        AbstractTestFieldValueChange<Object> {
    @Override
    protected void setUp() throws Exception {
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
