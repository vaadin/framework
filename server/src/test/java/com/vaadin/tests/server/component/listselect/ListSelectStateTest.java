package com.vaadin.tests.server.component.listselect;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.select.AbstractSelectState;
import com.vaadin.ui.ListSelect;

/**
 * Tests for ListSelect State.
 *
 */
public class ListSelectStateTest {

    @Test
    public void getState_listSelectHasCustomState() {
        TestListSelect select = new TestListSelect();
        AbstractSelectState state = select.getState();
        Assert.assertEquals("Unexpected state class", AbstractSelectState.class,
                state.getClass());
    }

    private static class TestListSelect extends ListSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }

}
