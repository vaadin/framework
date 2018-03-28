package com.vaadin.v7.tests.components.grid;

import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;

public class PersonTestGrid extends Grid {

    public PersonTestGrid(int size) {
        BeanItemContainer<ComplexPerson> container = ComplexPerson
                .createContainer(size);
        container.addNestedContainerBean("address");
        setContainerDataSource(container);
    }
}
