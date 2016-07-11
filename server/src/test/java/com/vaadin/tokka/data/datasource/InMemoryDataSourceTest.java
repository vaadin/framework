package com.vaadin.tokka.data.datasource;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.InMemoryDataSource;

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
        dataSource.apply(null).forEach(str -> assertTrue(data.contains(str)));
    }

    @Test
    public void testSortByComparatorListsDiffer() {
        Comparator<StrBean> comp = Comparator.comparing(StrBean::getValue)
                .thenComparing(StrBean::getRandomNumber)
                .thenComparing(StrBean::getId);
        List<StrBean> list = dataSource.sortingBy(comp).apply(null)
                .collect(Collectors.toList());

        // First value in data is { Xyz, 10, 100 } which should be last in list
        Assert.assertNotEquals("First value should not match", data.get(0),
                list.get(0));

        data.sort(comp);
        for (int i = 0; i < data.size(); ++i) {
            Assert.assertEquals("Sorting result differed", data.get(i),
                    list.get(i));
        }
    }

    @Test
    public void testDefatulSortWithSpecifiedPostSort() {
        Comparator<StrBean> comp = Comparator.comparing(StrBean::getValue)
                .thenComparing(Comparator.comparing(StrBean::getId).reversed());
        List<StrBean> list = dataSource.sortingBy(comp).apply(null)
                // The sort here should come e.g from a Component
                .sorted(Comparator.comparing(StrBean::getRandomNumber))
                .collect(Collectors.toList());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);
            // Test specific sort
            Assert.assertTrue(prev.getRandomNumber() <= cur.getRandomNumber());

            if (prev.getRandomNumber() == cur.getRandomNumber()) {
                // Test default sort
                Assert.assertTrue(prev.getValue().compareTo(cur.getValue()) <= 0);
                if (prev.getValue().equals(cur.getValue())) {
                    Assert.assertTrue(prev.getId() > cur.getId());
                }
            }
        }
    }
}
