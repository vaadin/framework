package com.vaadin.tests.components.nativeselect;

import com.vaadin.tests.components.abstractlisting.AbstractSingleSelectTestUI;
import com.vaadin.ui.NativeSelect;

public class NativeSelects
        extends AbstractSingleSelectTestUI<NativeSelect<Object>> {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Class<NativeSelect<Object>> getTestClass() {
        return (Class) NativeSelect.class;
    }
}
