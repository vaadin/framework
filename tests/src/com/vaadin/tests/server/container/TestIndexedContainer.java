package com.vaadin.tests.server.container;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import com.vaadin.data.util.IndexedContainer;

public class TestIndexedContainer extends AbstractInMemoryContainerTest {

    public void testBasicOperations() {
        testBasicContainerOperations(new IndexedContainer());
    }

    public void testFiltering() {
        testContainerFiltering(new IndexedContainer());
    }

    public void testSorting() {
        testContainerSorting(new IndexedContainer());
    }

    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(new IndexedContainer());
    }

    public void testContainerOrdered() {
        testContainerOrdered(new IndexedContainer());
    }

    public void testContainerIndexed() {
        testContainerIndexed(new IndexedContainer(), sampleData[2], 2, true,
                "newItemId", true);
    }

    protected abstract class ItemSetChangeListenerTester extends
            BaseItemSetChangeListenerTester<IndexedContainer> {
        @Override
        protected IndexedContainer prepareContainer() {
            IndexedContainer container = new IndexedContainer();
            initializeContainer(container);
            return container;
        }
    }

    public void testItemSetChangeListeners() {
        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItem();
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAt(0);
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAt(container.size());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAfter(null);
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAfter(container.firstItemId());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAfter(container.lastItemId());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItem("com.example.Test");
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItem("com.example.Test");
                container.addItem("com.example.Test2");
            }
        }.listenerTest(2, false);

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                String cn = "com.example.Test";
                container.addItem(cn);
                // second add is a NOP
                container.addItem(cn);
            }
        }.listenerTest(1, false);

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAfter(null, "com.example.Test");
                Assert.assertEquals("com.example.Test", container.firstItemId());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAfter(container.firstItemId(),
                        "com.example.Test");
                Assert.assertEquals("com.example.Test",
                        container.getIdByIndex(1));
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAfter(container.lastItemId(),
                        "com.example.Test");
                Assert.assertEquals("com.example.Test", container.lastItemId());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAt(0, "com.example.Test");
                Assert.assertEquals("com.example.Test", container.firstItemId());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAt(1, "com.example.Test");
                Assert.assertEquals("com.example.Test",
                        container.getIdByIndex(1));
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.addItemAt(container.size(), "com.example.Test");
                Assert.assertEquals("com.example.Test", container.lastItemId());
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.removeItem(sampleData[0]);
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.removeItem("com.example.Test");
            }
        }.listenerTest(0, true);

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                // this test does not check that there would be no second
                // notification because the collection is already empty
                container.removeAllItems();
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            private int propertyIndex = 0;

            @Override
            protected void performModification(IndexedContainer container) {
                Collection<?> containerPropertyIds = container
                        .getContainerPropertyIds();
                container.addContainerFilter(new ArrayList<Object>(
                        containerPropertyIds).get(propertyIndex++), "a", true,
                        false);
            }
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.removeContainerFilters(SIMPLE_NAME);
            }

            @Override
            protected IndexedContainer prepareContainer() {
                IndexedContainer container = super.prepareContainer();
                container.addContainerFilter(SIMPLE_NAME, "a", true, false);
                return container;
            };
        }.listenerTest();

        new ItemSetChangeListenerTester() {
            @Override
            protected void performModification(IndexedContainer container) {
                container.removeAllContainerFilters();
            }

            @Override
            protected IndexedContainer prepareContainer() {
                IndexedContainer container = super.prepareContainer();
                container.addContainerFilter(SIMPLE_NAME, "a", true, false);
                return container;
            };
        }.listenerTest();
    }

}
