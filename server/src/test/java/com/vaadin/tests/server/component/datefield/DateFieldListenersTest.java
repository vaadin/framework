package com.vaadin.tests.server.component.datefield;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.vaadin.data.validator.RangeValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.AbstractDateField;

public class DateFieldListenersTest extends AbstractListenerMethodsTestBase {

    public static class TestDateField<T extends Temporal & TemporalAdjuster & Serializable & Comparable<? super T>, R extends Enum<R>>
            extends AbstractDateField<T, R> {

        public TestDateField() {
            super(null);
        }

        @Override
        protected int getDatePart(T date, R resolution) {
            return 0;
        }

        @Override
        protected T buildDate(Map<R, Integer> resolutionValues) {
            return null;
        }

        @Override
        protected RangeValidator<T> getRangeValidator() {
            return null;
        }

        @Override
        protected T convertFromDate(Date date) {
            return null;
        }

        @Override
        protected Date convertToDate(T date) {
            return null;
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
