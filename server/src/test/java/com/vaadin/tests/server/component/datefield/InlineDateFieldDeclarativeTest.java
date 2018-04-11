package com.vaadin.tests.server.component.datefield;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateFieldDeclarativeTest;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.declarative.Design;

/**
 * Tests the declarative support for implementations of
 * {@link AbstractDateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class InlineDateFieldDeclarativeTest
        extends AbstractLocalDateFieldDeclarativeTest<InlineDateField> {

    @Test
    public void testInlineDateFieldToFromDesign() throws Exception {
        InlineDateField field = new InlineDateField("Day is",
                LocalDate.of(2003, 2, 27));
        field.setShowISOWeekNumbers(true);
        field.setRangeStart(LocalDate.of(2001, 2, 27));
        field.setRangeEnd(LocalDate.of(20011, 2, 27));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(field, bos);

        InlineDateField result = (InlineDateField) Design
                .read(new ByteArrayInputStream(bos.toByteArray()));
        assertEquals(field.getResolution(), result.getResolution());
        assertEquals(field.getCaption(), result.getCaption());
        assertEquals(field.getValue(), result.getValue());
        assertEquals(field.getRangeStart(), result.getRangeStart());
        assertEquals(field.getRangeEnd(), result.getRangeEnd());
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-inline-date-field";
    }

    @Override
    protected Class<? extends InlineDateField> getComponentClass() {
        return InlineDateField.class;
    }

}
