package com.vaadin.v7.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.Test;

import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.select.AbstractSelectState;

public class NativeSelectTest {

    @Test
    public void rpcRegisteredConstructorNoArg() {
        assertFocusRpcRegistered(new NativeSelect());
    }

    @Test
    public void rpcRegisteredConstructorString() {
        assertFocusRpcRegistered(new NativeSelect("foo"));
    }

    @Test
    public void rpcRegisteredConstructorStringCollection() {
        assertFocusRpcRegistered(
                new NativeSelect("foo", Collections.singleton("Hello")));
    }

    @Test
    public void rpcRegisteredConstructorStringContainer() {
        assertFocusRpcRegistered(
                new NativeSelect("foo", new IndexedContainer()));
    }

    @Test
    public void getState_listSelectHasCustomState() {
        TestNativeSelect select = new TestNativeSelect();
        AbstractSelectState state = select.getState();
        assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    private static class TestNativeSelect extends NativeSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }

    private void assertFocusRpcRegistered(NativeSelect s) {
        assertNotNull("RPC is not correctly registered", s.getRpcManager(
                "com.vaadin.shared.communication.FieldRpc$FocusAndBlurServerRpc"));
    }

}
