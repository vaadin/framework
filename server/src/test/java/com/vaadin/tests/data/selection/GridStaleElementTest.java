package com.vaadin.tests.data.selection;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.ReplaceListDataProvider;
import com.vaadin.data.provider.StrBean;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.GridSelectionModel;

public class GridStaleElementTest {

    private Grid<StrBean> grid = new Grid<>();
    private ReplaceListDataProvider dataProvider;
    private List<StrBean> data = StrBean.generateRandomBeans(2);

    @Before
    public void setUp() {
        // Make Grid attached to UI to make DataCommunicator do it's magic.
        final VaadinSession application = new AlwaysLockedVaadinSession(null);
        final UI uI = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }

            @Override
            public VaadinSession getSession() {
                return application;
            }

            @Override
            public Future<Void> access(Runnable runnable) {
                runnable.run();
                return null;
            }
        };
        uI.setContent(grid);
        uI.attach();
        dataProvider = new ReplaceListDataProvider(data);
        grid.setDataProvider(dataProvider);
    }

    @Test
    public void testGridMultiSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);

        GridSelectionModel<StrBean> model = grid
                .setSelectionMode(SelectionMode.MULTI);
        model.select(toReplace);

        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(),
                -1);
        dataProvider.refreshItem(replacement);

        assertStale(toReplace);
        model.getSelectedItems()
                .forEach(item -> Assert.assertFalse(
                        "Selection should not contain stale values",
                        dataProvider.isStale(item)));

        Object oldId = dataProvider.getId(toReplace);
        Assert.assertTrue("Selection did not contain an item with matching Id.",
                model.getSelectedItems().stream().map(dataProvider::getId)
                        .anyMatch(oldId::equals));
        Assert.assertTrue("Stale element is not considered selected.",
                model.isSelected(toReplace));
    }

    @Test
    public void testGridSingleSelectionUpdateOnRefreshItem() {
        StrBean toReplace = data.get(0);
        assertNotStale(toReplace);

        GridSelectionModel<StrBean> model = grid
                .setSelectionMode(SelectionMode.SINGLE);
        model.select(toReplace);

        StrBean replacement = new StrBean("Replacement bean", toReplace.getId(),
                -1);
        dataProvider.refreshItem(replacement);

        assertStale(toReplace);
        model.getSelectedItems()
                .forEach(i -> Assert.assertFalse(
                        "Selection should not contain stale values",
                        dataProvider.isStale(i)));

        Assert.assertTrue("Selection did not contain an item with matching Id.",
                model.getSelectedItems().stream().map(dataProvider::getId)
                        .filter(i -> dataProvider.getId(toReplace).equals(i))
                        .findFirst().isPresent());
        Assert.assertTrue("Stale element is not considered selected.",
                model.isSelected(toReplace));
    }

    private void assertNotStale(StrBean bean) {
        Assert.assertFalse(
                "Bean with id " + bean.getId() + " should not be stale.",
                dataProvider.isStale(bean));
    }

    private void assertStale(StrBean bean) {
        Assert.assertTrue("Bean with id " + bean.getId() + " should be stale.",
                dataProvider.isStale(bean));
    }
}
