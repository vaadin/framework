package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.data.Result;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.v7.data.util.converter.Converter;

public class DateFieldUnparsableDate extends TestBase {

    public class MyDateField extends AbstractDateField {
        LocalDate oldDate = null;

        public MyDateField(String caption) {
            super(caption);
            addValueChangeListener(event -> oldDate = getValue());
        }

        @Override
        protected Result<LocalDate> handleUnparsableDateString(
                String dateString) throws Converter.ConversionException {
            return Result.ok(oldDate);
        }
    }

    public class MyDateField2 extends AbstractDateField {
        public MyDateField2(String caption) {
            super(caption);
        }

        @Override
        protected Result<LocalDate> handleUnparsableDateString(
                String dateString) throws Converter.ConversionException {
            return Result.ok(null);
        }
    }

    public class MyDateField3 extends AbstractDateField {
        public MyDateField3(String caption) {
            super(caption);
        }

        @Override
        protected Result<LocalDate> handleUnparsableDateString(
                String dateString) throws Converter.ConversionException {
            return Result.error("You should not enter invalid dates!");
        }
    }

    public class MyDateField4 extends AbstractDateField {
        public MyDateField4(String caption) {
            super(caption);
        }

        @Override
        protected Result<LocalDate> handleUnparsableDateString(
                String dateString) throws Converter.ConversionException {
            if (dateString != null && dateString.equals("today")) {
                return Result.ok(LocalDate.now());
            }
            return Result.error("You should not enter invalid dates!");
        }
    }

    @Override
    protected void setup() {
        MyDateField df = new MyDateField(
                "Returns the old value for invalid dates");
        addComponent(df);

        MyDateField2 df2 = new MyDateField2("Returns empty for invalid dates");
        addComponent(df2);

        MyDateField3 df3 = new MyDateField3(
                "Throws an exception for invalid dates");
        addComponent(df3);

        MyDateField4 df4 = new MyDateField4("Can convert 'today'");
        addComponent(df4);

    }

    @Override
    protected String getDescription() {
        return "DateFields in various configurations (according to caption). All handle unparsable dates differently";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4236;
    }
}
