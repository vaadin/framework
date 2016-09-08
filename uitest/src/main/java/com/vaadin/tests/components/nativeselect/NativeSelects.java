package com.vaadin.tests.components.nativeselect;

import com.vaadin.tests.components.abstractlisting.AbstractListingTestUI;
import com.vaadin.ui.NativeSelect;

public class NativeSelects extends
        AbstractListingTestUI<NativeSelect<Object>> {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Class<NativeSelect<Object>> getTestClass() {
        return (Class) NativeSelect.class;
    }
}
