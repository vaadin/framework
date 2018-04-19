package com.vaadin.ui;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.tests.util.MockUI;

public class ComboBoxTest {

    private List<String> eventValues = new ArrayList<>();

    @Test
    public void testResetValue() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("one", "two");

        // Reset value whenever it changes (in a real case, this listener would
        // do something with the selected value before discarding it)
        comboBox.addValueChangeListener(event -> {
            eventValues.add(event.getValue());
            comboBox.setValue(null);
        });

        // "Attach" the component and initialize diffstate
        new MockUI().setContent(comboBox);
        // Generate initial data
        comboBox.getDataCommunicator().beforeClientResponse(true);
        ComponentTest.syncToClient(comboBox);

        // Emulate selection of "one"
        String oneKey = comboBox.getDataCommunicator().getKeyMapper()
                .key("one");
        ServerRpcManager.getRpcProxy(comboBox, SelectionServerRpc.class)
                .select(oneKey);

        assertArrayEquals("Unexpected values from selection events",
                new Object[] { "one", null }, eventValues.toArray());

        ComponentTest.assertEncodedStateProperties(comboBox,
                "Selection change done by the listener should be sent to the client",
                "selectedItemKey");
    }
}
