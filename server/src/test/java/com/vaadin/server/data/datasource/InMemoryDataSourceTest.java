package com.vaadin.server.data.datasource;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.vaadin.server.data.DataSource;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.data.InMemoryDataSource;
import com.vaadin.server.data.Query;

public class InMemoryDataSourceTest {

    private InMemoryDataSource<StrBean> dataSource;
    private List<StrBean> data;

    @Before
    public void setUp() {
        data = StrBean.generateRandomBeans(100);
        dataSource = DataSource.create(data);
    }

    @Test
    public void testListContainsAllData() {
        dataSource.apply(new Query()).forEach(
                str -> assertTrue(
                        "Data source contained values not in original data",
                        data.remove(str)));
        assertTrue("Not all values from original data were in data source",
                data.isEmpty());
    }

}
