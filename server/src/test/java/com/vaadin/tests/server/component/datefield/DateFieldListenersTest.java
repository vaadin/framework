package com.vaadin.tests.server.component.datefield;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.vaadin.data.validator.DateTimeRangeValidator;
import com.vaadin.data.validator.RangeValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.AbstractDateField;

public class DateFieldListenersTest extends AbstractListenerMethodsTestBase {

    public static class TestDateField
            extends AbstractDateField<LocalDateTime, DateTimeResolution> {

        public TestDateField() {
            super(DateTimeResolution.DAY);
        }

        @Override
        protected int getDatePart(LocalDateTime date,
                DateTimeResolution resolution) {
            return 0;
        }

        @Override
        protected LocalDateTime buildDate(
                Map<DateTimeResolution, Integer> resolutionValues) {
            return null;
        }

        @Override
        protected RangeValidator<LocalDateTime> getRangeValidator() {
            return new DateTimeRangeValidator(getDateOutOfRangeMessage(),
                    adjustToResolution(getRangeStart(), getResolution()),
                    adjustToResolution(getRangeEnd(), getResolution()));
        }

        @Override
        protected LocalDateTime convertFromDate(Date date) {
            return null;
        }

        @Override
        protected Date convertToDate(LocalDateTime date) {
            return null;
        }

        @Override
        protected String formatDate(LocalDateTime value) {
            return null;
        }

        @Override
        protected LocalDateTime toType(TemporalAccessor temporalAccessor) {
            return LocalDateTime.from(temporalAccessor);
        }

        @Override
        protected LocalDateTime adjustToResolution(LocalDateTime date,
                DateTimeResolution forResolution) {
            if (date == null) {
                return null;
            }
            switch (forResolution) {
            case YEAR:
                return date.withDayOfYear(1).toLocalDate().atStartOfDay();
            case MONTH:
                return date.withDayOfMonth(1).toLocalDate().atStartOfDay();
            case DAY:
                return date.toLocalDate().atStartOfDay();
            case HOUR:
                return date.truncatedTo(ChronoUnit.HOURS);
            case MINUTE:
                return date.truncatedTo(ChronoUnit.MINUTES);
            case SECOND:
                return date.truncatedTo(ChronoUnit.SECONDS);
            default:
                assert false : "Unexpected resolution argument "
                        + forResolution;
                return null;
            }
        }
    }

    @Test
    public void testFocusListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TestDateField.class, FocusEvent.class,
                FocusListener.class);
    }

    @Test
    public void testBlurListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TestDateField.class, BlurEvent.class,
                BlurListener.class);
    }
}
