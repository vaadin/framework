package com.vaadin.v7.tests.server.component.twincolselect;

import java.util.Arrays;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.TwinColSelect;

/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class TwinColSelectDeclarativeTest
        extends DeclarativeTestBase<TwinColSelect> {

    public String getBasicDesign() {
        return "<vaadin7-twin-col-select rows=5 right-column-caption='Selected values' left-column-caption='Unselected values'>\n"
                + "        <option>First item</option>\n"
                + "        <option selected>Second item</option>\n"
                + "        <option selected>Third item</option>\n"
                + "</vaadin7-twin-col-select>";

    }

    public TwinColSelect getBasicExpected() {
        TwinColSelect s = new TwinColSelect();
        s.setRightColumnCaption("Selected values");
        s.setLeftColumnCaption("Unselected values");
        s.addItem("First item");
        s.addItem("Second item");
        s.addItem("Third item");
        s.setValue(Arrays.asList(new Object[] { "Second item", "Third item" }));
        s.setRows(5);
        return s;
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
    public void testReadEmpty() {
        testRead("<vaadin7-twin-col-select />", new TwinColSelect());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin7-twin-col-select />", new TwinColSelect());
    }

}