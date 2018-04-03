package com.vaadin.v7.data.util;

import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Table;

public class ContainerSizeAssertTest {

    @Test(expected = AssertionError.class)
    public void testNegativeSizeAssert() {
        Table table = createAttachedTable();

        table.setContainerDataSource(createNegativeSizeContainer());
    }

    @Test
    public void testZeroSizeNoAssert() {
        Table table = createAttachedTable();

        table.setContainerDataSource(new IndexedContainer());
    }

    private Container createNegativeSizeContainer() {
        return new IndexedContainer() {
            @Override
            public int size() {
                return -1;
            }
        };
    }

    private Table createAttachedTable() {
        return new Table() {
            private boolean initialized = true;

            @Override
            public boolean isAttached() {
                // This returns false until the super constructor has finished
                return initialized;
            }
        };
    }
}
