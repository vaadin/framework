package com.vaadin.tests.components.grid;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Grid;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridOverrideDataProviderGetterTest
{
    private ListDataProvider<MyBean> listDataProvider;

    @Before
    public void setUp()
    {
        MyBean myBean1 = new MyBean("name1");
        MyBean myBean2 = new MyBean("name2");
        listDataProvider = new ListDataProvider<>(Arrays.asList(myBean1,myBean2));
    }

    /**
     * Originally calling the constructor of MyGrid will throw:
     * <pre>
     * java.lang.ClassCastException: class com.vaadin.data.provider.CallbackDataProvider cannot be cast to class com.vaadin.data.provider.ListDataProvider (com.vaadin.data.provider.CallbackDataProvider and com.vaadin.data.provider.ListDataProvider are in unnamed module of loader 'app')
     * </pre>
     * Don't call an overridable method in your constructor is the moral of the story
     */
    @Test
    public void testCalling()
    {
        try {
            Grid<MyBean> grid = new MyGrid<>(listDataProvider);
            assertTrue(grid.getDataProvider() instanceof ListDataProvider);
        } catch(ClassCastException e) {
            fail("Should not throw a ClassCastException error:" + e);
        }
    }

    private static class MyGrid<T> extends Grid<T>
    {
        public MyGrid(ListDataProvider<T> myDataProvider)
        {
            super(myDataProvider);
        }

        /**
         * I'm overriding this because I can
         */
        @Override
        public ListDataProvider<T> getDataProvider()
        {
            return (ListDataProvider<T>) super.getDataProvider();
        }
    }

    private static class MyBean
    {
        private final String name;

        MyBean(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }
}
