package com.vaadin.tests.server.component.twincolselect;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractmultiselect.AbstractMultiSelectDeclarativeTest;
import com.vaadin.ui.TwinColSelect;

/**
 * TwinColSelectt declarative test.
 * <p>
 * There are only TwinColSelect specific properties explicit tests. All other
 * tests are in the super class ( {@link AbstractMultiSelectDeclarativeTest}).
 *
 * @see AbstractMultiSelectDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 */
public class TwinColSelectDeclarativeTest
        extends AbstractMultiSelectDeclarativeTest<TwinColSelect> {

    @Test
    public void rowsPropertySerialization() {
        int rows = 7;
        String design = String.format("<%s rows='%s'/>", getComponentTag(),
                rows);

        TwinColSelect<String> select = new TwinColSelect<>();
        select.setRows(rows);

        testRead(design, select);
        testWrite(design, select);
    }

    @Test
    public void rightColumnCaptionPropertySerialization() {
        String rightColumnCaption = "foo";
        String design = String.format("<%s right-column-caption='%s'/>",
                getComponentTag(), rightColumnCaption);

        TwinColSelect<String> select = new TwinColSelect<>();
        select.setRightColumnCaption(rightColumnCaption);

        testRead(design, select);
        testWrite(design, select);
    }

    @Test
    public void leftColumnCaptionPropertySerialization() {
        String leftColumnCaption = "foo";
        String design = String.format("<%s left-column-caption='%s'/>",
                getComponentTag(), leftColumnCaption);

        TwinColSelect<String> select = new TwinColSelect<>();
        select.setLeftColumnCaption(leftColumnCaption);

        testRead(design, select);
        testWrite(design, select);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-twin-col-select";
    }

    @Override
    protected Class<? extends TwinColSelect> getComponentClass() {
        return TwinColSelect.class;
    }

}
