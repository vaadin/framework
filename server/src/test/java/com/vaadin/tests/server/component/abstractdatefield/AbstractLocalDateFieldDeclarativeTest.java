package com.vaadin.tests.server.component.abstractdatefield;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.AbstractLocalDateField;

/**
 * Abstract test class which contains tests for declarative format for
 * properties that are common for AbstractDateField.
 * <p>
 * It's an abstract so it's not supposed to be run as is. Instead each
 * declarative test for a real component should extend it and implement abstract
 * methods to be able to test the common properties. Components specific
 * properties should be tested additionally in the subclasses implementations.
 *
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractLocalDateFieldDeclarativeTest<T extends AbstractLocalDateField>
        extends AbstractFieldDeclarativeTest<T, LocalDate> {

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        LocalDate value = LocalDate.of(2003, 02, 27);
        String design = String.format("<%s value='%s'/>", getComponentTag(),
                value);

        T component = getComponentClass().newInstance();
        component.setValue(value);

        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void abstractDateFieldAttributesDeserialization()
            throws InstantiationException, IllegalAccessException {
        boolean showIsoWeeks = true;
        LocalDate end = LocalDate.of(2019, 01, 15);
        LocalDate start = LocalDate.of(2001, 02, 11);
        String dateOutOfRange = "test date out of range";
        DateResolution resolution = DateResolution.MONTH;
        String dateFormat = "test format";
        boolean lenient = true;
        String parseErrorMsg = "test parse error";
        String design = String.format(
                "<%s show-iso-week-numbers range-end='%s' range-start='%s' "
                        + "date-out-of-range-message='%s' resolution='%s' "
                        + "date-format='%s' lenient parse-error-message='%s'/>",
                getComponentTag(), end, start, dateOutOfRange,
                resolution.name().toLowerCase(Locale.ROOT), dateFormat,
                parseErrorMsg);

        T component = getComponentClass().newInstance();

        component.setShowISOWeekNumbers(showIsoWeeks);
        component.setRangeEnd(end);
        component.setRangeStart(start);
        component.setDateOutOfRangeMessage(dateOutOfRange);
        component.setResolution(resolution);
        component.setDateFormat(dateFormat);
        component.setLenient(lenient);
        component.setParseErrorMessage(parseErrorMsg);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        LocalDate value = LocalDate.of(2003, 02, 27);
        String design = String.format("<%s value='%s' readonly/>",
                getComponentTag(), value);

        T component = getComponentClass().newInstance();
        component.setValue(value);
        component.setReadOnly(true);

        testRead(design, component);
        testWrite(design, component);
    }

}
