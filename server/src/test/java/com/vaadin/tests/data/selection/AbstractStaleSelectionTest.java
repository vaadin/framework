package com.vaadin.tests.data.selection;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import com.vaadin.data.provider.ReplaceListDataProvider;
import com.vaadin.data.provider.StrBean;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.UI;

@RunWith(Parameterized.class)
public abstract class AbstractStaleSelectionTest<S extends AbstractListing<StrBean>> {

    protected ReplaceListDataProvider dataProvider;
    protected final List<StrBean> data = StrBean.generateRandomBeans(2);

    @Parameter(0)
    public String name;

    @Parameter(1)
    public S select;

    @Before
    public void setUp() {
        dataProvider = new ReplaceListDataProvider(data);

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
        uI.setContent(select);
        uI.attach();
        select.getDataCommunicator().setDataProvider(dataProvider, null);
    }

    protected final void assertIsStale(StrBean bean) {
        Assert.assertTrue("Bean with id " + bean.getId() + " should be stale.",
                dataProvider.isStale(bean));
    }

    protected final void assertNotStale(StrBean bean) {
        Assert.assertFalse(
                "Bean with id " + bean.getId() + " should not be stale.",
                dataProvider.isStale(bean));
    }
}
