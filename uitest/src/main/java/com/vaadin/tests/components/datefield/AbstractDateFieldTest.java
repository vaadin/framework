package com.vaadin.tests.components.datefield;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.AbstractLocalDateField;

public abstract class AbstractDateFieldTest<T extends AbstractLocalDateField>
        extends AbstractFieldTest<T, LocalDate> {

    private Command<T, LocalDate> setValue = new Command<T, LocalDate>() {

        @Override
        public void execute(T c, LocalDate value, Object data) {
            c.setValue(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        createResolutionSelectAction(CATEGORY_FEATURES);
        createBooleanAction("Lenient", CATEGORY_FEATURES, false,
                lenientCommand);
        createBooleanAction("Show week numbers", CATEGORY_FEATURES, false,
                weekNumberCommand);
        createDateFormatSelectAction(CATEGORY_FEATURES);
        createSetValueAction(CATEGORY_FEATURES);

    }

    private void createSetValueAction(String category) {
        LinkedHashMap<String, LocalDate> options = new LinkedHashMap<>();
        options.put("(null)", null);
        options.put("(current time)", LocalDate.now());
        options.put("2010-12-12", LocalDate.of(2010, 12, 12));
        createMultiClickAction("Set value", category, options, setValue, null);
    }

    private void createDateFormatSelectAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();

        options.put("-", null);
        options.put("d M yyyy", "d M yyyy");
        options.put("d MM yyyy", "d MM yyyy");
        options.put("d MMM yyyy", "d MMM yyyy");
        options.put("d MMMM yyyy", "d MMMM yyyy");
        options.put("dd M yyyy", "dd M yyyy");
        options.put("ddd M yyyy", "ddd M yyyy");
        options.put("d M y", "d M y");
        options.put("d M yy", "d M yy");
        options.put("d M yyy", "d M yyy");
        options.put("d M yyyy", "d M yyyy");
        options.put("d M 'custom text' yyyy", "d M 'custom text' yyyy");
        options.put("'day:'d', month:'M', year: 'yyyy",
                "'day:'d', month:'M', year: 'yyyy");
        options.put(getDatePattern(new Locale("fi", "FI"), DateFormat.LONG),
                getDatePattern(new Locale("fi", "FI"), DateFormat.LONG));
        options.put(getDatePattern(new Locale("fi", "FI"), DateFormat.MEDIUM),
                getDatePattern(new Locale("fi", "FI"), DateFormat.MEDIUM));
        options.put(getDatePattern(new Locale("fi", "FI"), DateFormat.SHORT),
                getDatePattern(new Locale("fi", "FI"), DateFormat.SHORT));

        createSelectAction("Date format", category, options, "-",
                dateFormatCommand);

    }

    private String getDatePattern(Locale locale, int dateStyle) {
        DateFormat dateFormat = DateFormat.getDateInstance(dateStyle, locale);

        if (dateFormat instanceof SimpleDateFormat) {
            String pattern = ((SimpleDateFormat) dateFormat).toPattern();
            return pattern;
        }
        return null;

    }

    private void createResolutionSelectAction(String category) {
        LinkedHashMap<String, DateResolution> options = new LinkedHashMap<>();
        options.put("Year", DateResolution.YEAR);
        options.put("Month", DateResolution.MONTH);
        options.put("Day", DateResolution.DAY);

        createSelectAction("Resolution", category, options, "Year",
                resolutionCommand);
    }

    private Command<T, DateResolution> resolutionCommand = new Command<T, DateResolution>() {

        @Override
        public void execute(T c, DateResolution value, Object data) {
            c.setResolution(value);

        }
    };
    private Command<T, Boolean> lenientCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setLenient(false);

        }
    };
    private Command<T, Boolean> weekNumberCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setShowISOWeekNumbers(value);

        }
    };
    private Command<T, String> dateFormatCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String value, Object data) {
            c.setDateFormat(value);
        }
    };

}
