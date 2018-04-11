package com.vaadin.v7.tests.server.component.listselect;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.ListSelect;

public class ListSelectDeclarativeTest extends DeclarativeTestBase<ListSelect> {

    private ListSelect getWithOptionsExpected() {
        ListSelect ls = new ListSelect();
        ls.setRows(10);
        ls.addItem("Male");
        ls.addItem("Female");
        return ls;
    }

    private String getWithOptionsDesign() {
        return "<vaadin7-list-select rows=10>\n"
                + "        <option>Male</option>\n"
                + "        <option>Female</option>\n"
                + "</vaadin7-list-select>\n" + "";
    }

    @Test
    public void testReadWithOptions() {
        testRead(getWithOptionsDesign(), getWithOptionsExpected());
    }

    @Test
    public void testWriteWithOptions() {
        testWrite(stripOptionTags(getWithOptionsDesign()),
                getWithOptionsExpected());
    }

    private ListSelect getBasicExpected() {
        ListSelect ls = new ListSelect();
        ls.setCaption("Hello");
        return ls;
    }

    private String getBasicDesign() {
        return "<vaadin7-list-select caption='Hello' />";
    }

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

}
