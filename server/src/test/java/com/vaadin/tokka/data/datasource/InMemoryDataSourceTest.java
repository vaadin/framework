package com.vaadin.tokka.data.datasource;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.InMemoryDataSource;

public class InMemoryDataSourceTest {

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

    private InMemoryDataSource<StrBean> dataSource;
    private List<StrBean> data;

    @Before
    public void setUp() {
        data = createData();
        dataSource = DataSource.create(data);
    }

    @Test
    public void testListContainsAllData() {
        dataSource.apply(null).forEach(str -> assertTrue(data.contains(str)));
    }

    private List<StrBean> createData() {
        List<StrBean> list = new ArrayList<>();
        Stream.of("Foo", "Bar", "Baz").map(StrBean::new).forEach(list::add);
        return list;
    }

}
