package com.vaadin.data.provider.hierarchical;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.DataCommunicatorTest;
import com.vaadin.data.provider.HierarchicalDataCommunicator;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HierarchicalCommunicatorTest {

    private static final String ROOT = "ROOT";
    private static final String FOLDER = "FOLDER";
    private static final String LEAF = "LEAF";
    private TreeDataProvider<String> dataProvider;
    private TestHierarchicalDataCommunicator<String> communicator;
    private TreeData<String> treeData;

    @Before
    public void setUp() {
        VaadinSession session = new MockVaadinSession(
                Mockito.mock(VaadinService.class));
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        treeData = new TreeData<>();
        treeData.addItems(null, ROOT);
        treeData.addItems(ROOT, FOLDER);
        treeData.addItems(FOLDER, LEAF);
        dataProvider = new TreeDataProvider<>(treeData);
        communicator = new TestHierarchicalDataCommunicator<>();
        communicator.extend(ui);
        communicator.setDataProvider(dataProvider, null);
        communicator.attach();
    }

    @Test
    public void testFolderRemoveRefreshAll() {
        testItemRemove(FOLDER, true);
    }

    @Test
    public void testLeafRemoveRefreshAll() {
        testItemRemove(LEAF, true);
    }

    @Test
    public void testFolderRemove() {
        testItemRemove(FOLDER, false);
    }

    @Test
    public void testLeafRemove() {
        testItemRemove(LEAF, false);
    }

    private void testItemRemove(String item, boolean refreshAll) {
        communicator.pushData(1, Arrays.asList(ROOT, FOLDER, LEAF));
        communicator.expand(ROOT);
        communicator.expand(FOLDER);
        // Put the item into client queue
        communicator.refresh(item);
        treeData.removeItem(item);
        if (refreshAll) {
            dataProvider.refreshAll();
        } else {
            dataProvider.refreshItem(item);
        }
        communicator.beforeClientResponse(false);
    }

    @Test
    public void testReplaceAll() {
        communicator.pushData(1, Arrays.asList(ROOT, FOLDER, LEAF));
        // Some modifications
        communicator.expand(ROOT);
        communicator.expand(FOLDER);
        communicator.refresh(LEAF);
        // Replace dataprovider
        communicator.setDataProvider(new TreeDataProvider<>(new TreeData<>()),
                null);
        dataProvider.refreshAll();
        communicator.beforeClientResponse(false);
        assertFalse("Stalled object in KeyMapper",
                communicator.getKeyMapper().has(ROOT));
        assertEquals(-1, communicator.getParentIndex(FOLDER).longValue());
    }

    private static class TestHierarchicalDataCommunicator<T>
            extends HierarchicalDataCommunicator<T> {
        @Override
        public void extend(AbstractClientConnector target) {
            super.extend(target);
        }

        @Override
        public void pushData(int firstIndex, List<T> data) {
            super.pushData(firstIndex, data);
        }
    }
}
