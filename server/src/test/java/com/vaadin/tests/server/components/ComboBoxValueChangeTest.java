package com.vaadin.tests.server.components;

import org.junit.Before;

import com.vaadin.server.ServerRpcManager;
import com.vaadin.server.ServerRpcMethodInvocation;
import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

/**
 * Check that the value change listener for a combo box is triggered exactly
 * once when setting the value, at the correct time.
 * 
 * See <a href="http://dev.vaadin.com/ticket/4394">Ticket 4394</a>.
 */
public class ComboBoxValueChangeTest extends
        AbstractFieldValueChangeTestBase<Object> {

    @Before
    public void setUp() {
        ComboBox combo = new ComboBox() {
            @Override
            public String getConnectorId() {
                return "id";
            }
        };
        combo.addItem("myvalue");
        super.setUp(combo);
    }

    @Override
    protected void setValue(AbstractField<Object> field) {
        ComboBox combo = (ComboBox) field;
        ServerRpcMethodInvocation invocation = new ServerRpcMethodInvocation(
                combo.getConnectorId(), ComboBoxServerRpc.class,
                "setSelectedItem", 1);
        invocation.setParameters(new Object[] { "myvalue" });
        try {
            ServerRpcManager.applyInvocation(combo, invocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
