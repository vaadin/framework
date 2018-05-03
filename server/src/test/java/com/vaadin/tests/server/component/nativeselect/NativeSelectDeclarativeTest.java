package com.vaadin.tests.server.component.nativeselect;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.NativeSelect;

/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class NativeSelectDeclarativeTest
        extends DeclarativeTestBase<NativeSelect> {

    public String getBasicDesign() {
        return "<vaadin-native-select><option>foo</option><option>bar</option></vaadin-native-select>";

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
        String design = "<vaadin-native-select readonly><option selected>foo</option><option>bar</option></vaadin-native-select>";

        NativeSelect ns = new NativeSelect();
        ns.addItems("foo", "bar");
        ns.setValue("foo");
        ns.setReadOnly(true);

        testRead(design, ns);

        // Selects items are not written out by default
        String design2 = "<vaadin-native-select readonly></vaadin-native-select>";
        testWrite(design2, ns);
    }

}
