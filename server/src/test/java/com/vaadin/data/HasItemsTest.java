package com.vaadin.data;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeGrid;

public class HasItemsTest {

    private static ArrayList<Class<?>> whiteList = new ArrayList<>();
    {
        // these create a hierarchical data provider, which is not using
        // ArrayList or Arrays.ArrayList in the end
        whiteList.add(TreeGrid.class);
        whiteList.add(Tree.class);
    }

    @Test
    public void setItemsVarargsConstructor_createsListDataProvider_itIsEditable()
            throws InstantiationException, IllegalAccessException {
        Set<Class<? extends HasItems>> subTypesOf = new Reflections(
                "com.vaadin.ui").getSubTypesOf(HasItems.class).stream().filter(
                        clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                        .filter(clazz -> Stream.of(clazz.getConstructors())
                                .anyMatch(constuctor -> constuctor
                                        .getParameterCount() == 0))
                        .filter(clazz -> !whiteList.contains(clazz))
                        .collect(Collectors.toSet());

        for (Class<? extends HasItems> hasItemsType : subTypesOf) {
            HasItems hasItems = hasItemsType.newInstance();

            hasItems.setItems("0", "1");

            DataProvider dataProvider = hasItems.getDataProvider();

            Assert.assertTrue(
                    hasItemsType.getSimpleName()
                            + "setItems method with varargs parameters of does not create a list data provider",
                    dataProvider instanceof ListDataProvider);

            ListDataProvider listDataProvider = (ListDataProvider) dataProvider;

            Assert.assertTrue(
                    hasItemsType.getSimpleName()
                            + " does not have setItems method with varargs parameters of does not create an ArrayList backed list data provider",
                    listDataProvider.getItems() instanceof ArrayList);

            List list = (List) listDataProvider.getItems();
            // previously the following would explode since Arrays.ArrayList
            // does not support it
            list.add(0, "2");
        }
    }
}
