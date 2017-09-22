package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

/**
 * Test to validate clean detaching in Grid with ComponentRenderer.
 */
public class GridComponentRendererTest {

    private static final Person PERSON = Person.createTestPerson1();
    private Grid<Person> grid;
    private List<Person> backend;
    private DataProvider<Person, ?> dataProvider;
    private Label testComponent;
    private Label oldComponent;

    @Before
    public void setUp() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        backend = new ArrayList<>();
        backend.add(PERSON);
        dataProvider = DataProvider.ofCollection(backend);
        grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.addComponentColumn(p -> {
            oldComponent = testComponent;
            testComponent = new Label();
            return testComponent;
        });
        new MockUI() {
            @Override
            public Future<Void> access(Runnable runnable) {
                runnable.run();
                return null;
            };
        }.setContent(grid);
    }

    @Test
    public void testComponentChangeOnRefresh() {
        generateDataForClient(true);
        dataProvider.refreshItem(PERSON);
        generateDataForClient(false);
        Assert.assertNotNull("Old component should exist.", oldComponent);
    }

    @Test
    public void testComponentChangeOnSelection() {
        generateDataForClient(true);
        grid.select(PERSON);
        generateDataForClient(false);
        Assert.assertNotNull("Old component should exist.", oldComponent);
    }

    @Test
    public void testComponentChangeOnDataProviderChange() {
        generateDataForClient(true);
        grid.setItems(PERSON);
        Assert.assertEquals(
                "Test component was not detached on DataProvider change.", null,
                testComponent.getParent());
    }

    private void generateDataForClient(boolean initial) {
        grid.getDataCommunicator().beforeClientResponse(initial);
        if (testComponent != null) {
            Assert.assertEquals("New component was not attached.", grid,
                    testComponent.getParent());
        }
        if (oldComponent != null) {
            Assert.assertEquals("Old component was not detached.", null,
                    oldComponent.getParent());
        }
    }
}
