package com.vaadin.ui;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.select.AbstractSelectState;

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
        Assert.assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    private static class TestNativeSelect extends NativeSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }

    private void assertFocusRpcRegistered(NativeSelect s) {
        Assert.assertNotNull("RPC is not correctly registered", s.getRpcManager(
                "com.vaadin.shared.communication.FieldRpc$FocusAndBlurServerRpc"));
    }

}
