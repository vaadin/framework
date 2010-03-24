package com.vaadin.tests.server.components;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

/**
 * Check that the value change listener for a text field is triggered exactly
 * once when setting the value, at the correct time.
 * 
 * See <a href="http://dev.vaadin.com/ticket/4394">Ticket 4394</a>.
 */
public class TestTextFieldValueChange extends AbstractTestFieldValueChange {

    @Override
    protected void setUp() throws Exception {
        super.setUp(new TextField());
    }

    /**
     * Case where the text field only uses its internal buffer, no external
     * property data source.
     */
    public void testNoDataSource() {
        getField().setPropertyDataSource(null);

        expectValueChangeFromSetValueNotCommit();
    }

    @Override
    protected void setValue(AbstractField field) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("text", "newValue");
        field.changeVariables(field, variables);
    }

}
