package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.data.selection.SelectionServerRpc;

public class RadioButtonGroupTest {
    private RadioButtonGroup<String> radioButtonGroup;

    @Before
    public void setUp() {
        radioButtonGroup = new RadioButtonGroup<>();
        // Intentional deviation from upcoming selection order
        radioButtonGroup.setDataProvider(
                DataProvider.ofItems("Third", "Second", "First"));
    }

    @Test
    public void apiSelectionChange_notUserOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);

        radioButtonGroup.addSelectionListener(event -> {
            listenerCount.incrementAndGet();
            assertFalse(event.isUserOriginated());
        });

        radioButtonGroup.setValue("First");
        radioButtonGroup.setValue("Second");

        radioButtonGroup.setValue(null);
        radioButtonGroup.setValue(null);

        assertEquals(3, listenerCount.get());
    }

    @Test
    public void rpcSelectionChange_userOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);

        radioButtonGroup.addSelectionListener(event -> {
            listenerCount.incrementAndGet();
            assertTrue(event.isUserOriginated());
        });

        SelectionServerRpc rpc = ServerRpcManager.getRpcProxy(radioButtonGroup,
                SelectionServerRpc.class);

        rpc.select(getItemKey("First"));
        rpc.select(getItemKey("Second"));
        rpc.deselect(getItemKey("Second"));

        assertEquals(3, listenerCount.get());
    }

    private String getItemKey(String dataObject) {
        return radioButtonGroup.getDataCommunicator().getKeyMapper()
                .key(dataObject);
    }

}
