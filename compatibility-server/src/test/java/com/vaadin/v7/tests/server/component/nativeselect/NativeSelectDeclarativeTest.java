package com.vaadin.v7.tests.server.component.nativeselect;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.NativeSelect;

/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class NativeSelectDeclarativeTest
        extends DeclarativeTestBase<NativeSelect> {

    public String getBasicDesign() {
        return "<vaadin7-native-select><option>foo</option><option>bar</option></vaadin7-native-select>";

    }

    public NativeSelect getBasicExpected() {
        NativeSelect ns = new NativeSelect();
        ns.addItem("foo");
        ns.addItem("bar");
        return ns;
    }

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(stripOptionTags(getBasicDesign()), getBasicExpected());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-native-select readonly><option selected>foo</option><option>bar</option></vaadin7-native-select>";

        NativeSelect ns = new NativeSelect();
        ns.addItems("foo", "bar");
        ns.setValue("foo");
        ns.setReadOnly(true);

        testRead(design, ns);

        // Selects items are not written out by default
        String design2 = "<vaadin7-native-select readonly></vaadin7-native-select>";
        testWrite(design2, ns);
    }

}
