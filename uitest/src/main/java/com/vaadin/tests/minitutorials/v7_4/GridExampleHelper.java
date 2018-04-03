package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.v7.data.util.BeanItemContainer;

public class GridExampleHelper {
    public static BeanItemContainer<GridExampleBean> createContainer() {
        BeanItemContainer<GridExampleBean> container = new BeanItemContainer<>(
                GridExampleBean.class);
        for (int i = 0; i < 1000; i++) {
            container.addItem(new GridExampleBean("Bean " + i, i * i, i / 10d));
        }
        return container;
    }
}
