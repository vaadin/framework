package com.vaadin.tests.components.select;

import com.vaadin.ui.Select;

public class SelectTest<T extends Select> extends AbstractSelectTestCase<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) Select.class;
    }

}
