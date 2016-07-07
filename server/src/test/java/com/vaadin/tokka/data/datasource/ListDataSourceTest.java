package com.vaadin.tokka.data.datasource;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.tokka.server.communication.data.DataChangeHandler;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.ListDataSource;

public class ListDataSourceTest {

    private static class StrBean {

        private String value;

        public StrBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    private ListDataSource<StrBean> dataSource;
    private List<StrBean> data;
    private DataChangeHandler<StrBean> handler;

    @Before
    public void setUp() {
        data = createData();
        dataSource = DataSource.create(data);
        handler = EasyMock.mock(DataChangeHandler.class);
    }

    @Test
    public void testListContainsAllData() {
        EasyMock.replay(handler);
        dataSource.request().forEach(str -> assertTrue(data.contains(str)));
    }

    @Test
    public void testListFiresDataAddEvent() {
        StrBean strBean = new StrBean("Spam");
        handler.onDataAppend(strBean);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(handler);

        dataSource.addDataChangeHandler(handler);
        dataSource.save(strBean);
    }

    @Test
    public void testListFiresDataRemoveEvent() {
        StrBean strBean = data.get(0);
        handler.onDataRemove(strBean);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(handler);

        dataSource.addDataChangeHandler(handler);
        dataSource.remove(strBean);
    }

    @Test
    public void testListFiresDataUpdateEvent() {
        StrBean strBean = data.get(0);
        handler.onDataUpdate(strBean);
        EasyMock.expectLastCall().times(1);
        EasyMock.replay(handler);

        dataSource.addDataChangeHandler(handler);
        strBean.setValue("Fuu");
        dataSource.save(strBean);
    }

    @After
    public void tearDown() {
        EasyMock.verify(handler);
    }

    private List<StrBean> createData() {
        List<StrBean> list = new ArrayList<>();
        Stream.of("Foo", "Bar", "Baz").map(StrBean::new).forEach(list::add);
        return list;
    }

}
