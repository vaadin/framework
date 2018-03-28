package com.vaadin.tests.server.component.nativeselect;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractsingleselect.AbstractSingleSelectDeclarativeTest;
import com.vaadin.ui.NativeSelect;

/**
 * Declarative support tests for {@link NativeSelect}. All tests are in the
 * super class ({@link AbstractSingleSelectDeclarativeTest}). This class
 * declares only tag name and native select class (test parameters).
 *
 * @author Vaadin Ltd
 *
 */
public class NativeSelectDeclarativeTest
        extends AbstractSingleSelectDeclarativeTest<NativeSelect> {

    @Test
    public void nativeSelectSpecificPropertiesSerialize() {
        boolean emptySelectionAllowed = false;
        String emptySelectionCaption = "foo";

        String design = String.format(
                "<%s empty-selection-allowed='%s' "
                        + "empty-selection-caption='%s'/>",
                getComponentTag(), emptySelectionAllowed,
                emptySelectionCaption);

        NativeSelect<String> select = new NativeSelect<>();
        select.setEmptySelectionAllowed(emptySelectionAllowed);
        select.setEmptySelectionCaption(emptySelectionCaption);

        testRead(design, select);
        testWrite(design, select);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-native-select";
    }

    @Override
    protected Class<NativeSelect> getComponentClass() {
        return NativeSelect.class;
    }

}
