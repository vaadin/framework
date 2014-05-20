package com.vaadin.tests.components.datefield;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class CustomDateFormats extends TestBase {

    private static final Object CAPTION = "C";

    private Component customFormats = null;

    @Override
    protected void setup() {
        final NativeSelect s = new NativeSelect("Locale");
        s.setImmediate(true);
        s.setNullSelectionAllowed(false);
        s.addContainerProperty(CAPTION, String.class, "");
        addLocale(Locale.FRANCE, s);
        addLocale(Locale.CHINESE, s);
        addLocale(Locale.US, s);
        addLocale(Locale.UK, s);
        addLocale(new Locale("fi", "FI"), s);

        s.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                setDateFieldLocale((Locale) s.getValue());
            }
        });
        addComponent(s);
        s.setValue(Locale.FRANCE);
    }

    private void addLocale(Locale locale, NativeSelect s) {
        Item i = s.addItem(locale);
        i.getItemProperty(CAPTION).setValue(locale.toString());

    }

    protected void setDateFieldLocale(Locale value) {
        Component n = getCustomFormats(value);
        if (customFormats == null) {
            addComponent(n);
        } else {
            replaceComponent(customFormats, n);
        }
        customFormats = n;

    }

    private GridLayout getCustomFormats(Locale locale) {
        GridLayout gridLayout = createGridLayout();
        usedDebugIds.clear();
        addDateFields(gridLayout, locale);

        return gridLayout;
    }

    private GridLayout createGridLayout() {
        GridLayout gridLayout = new GridLayout(4, 4);
        gridLayout.setMargin(true);
        gridLayout.addComponent(new Label("FORMAT"));
        gridLayout.addComponent(new Label("DATEFIELD"));
        gridLayout.addComponent(new Label("SERVER SIDE VALUE"));
        gridLayout.addComponent(new Label("EXPECTED"));

        return gridLayout;
    }

    private void addDateFields(GridLayout gridLayout, Locale locale) {
        addDateField(gridLayout, "d M yyyy", locale);
        addDateField(gridLayout, "d MM yyyy", locale);
        addDateField(gridLayout, "d MMM yyyy", locale);
        addDateField(gridLayout, "d MMMM yyyy", locale);

        addDateField(gridLayout, "dd M yyyy", locale);
        addDateField(gridLayout, "ddd M yyyy", locale);

        addDateField(gridLayout, "d M y", locale, "d M yyyy");
        addDateField(gridLayout, "d M yy", locale);
        addDateField(gridLayout, "d M yyy", locale, "d M yyyy");
        addDateField(gridLayout, "d M yyyy", locale);

        addDateField(gridLayout, getDatePattern(locale, DateFormat.FULL),
                locale);
        addDateField(gridLayout, getDatePattern(locale, DateFormat.LONG),
                locale);
        addDateField(gridLayout, getDatePattern(locale, DateFormat.MEDIUM),
                locale);
        addDateField(gridLayout, getDatePattern(locale, DateFormat.SHORT),
                locale);
        addDateField(gridLayout, "EEE d MMMM yyyy", locale);

    }

    private String getDatePattern(Locale locale, int dateStyle) {
        DateFormat dateFormat = DateFormat.getDateInstance(dateStyle, locale);

        if (dateFormat instanceof SimpleDateFormat) {
            String pattern = ((SimpleDateFormat) dateFormat).toPattern();
            return pattern;
        }
        return null;

    }

    public class Data {

        private Label label;
        private String pattern;

        public Data(Label label, String pattern) {
            this.label = label;
            this.pattern = pattern;
        }

    }

    private Set<String> usedDebugIds = new HashSet<String>();

    private void addDateField(GridLayout gridLayout, String pattern,
            Locale locale, String expectedDateFormat) {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 1, 1);

        Label serversideValueLabel = new Label();

        DateField df = new DateField();
        df.setResolution(DateField.RESOLUTION_DAY);
        df.setLocale(locale);
        df.setWidth("300px");
        df.setDateFormat(pattern);
        df.setImmediate(true);
        String debugId = pattern.replace('/', 'X');
        // only certain characters are allowed in debug IDs
        debugId = debugId.replaceAll("[^-a-zA-Z .'_]", "X");
        while (usedDebugIds.contains(debugId)) {
            debugId = debugId + "-";
        }
        df.setId(debugId);
        usedDebugIds.add(debugId);

        df.setData(new Data(serversideValueLabel, pattern));
        df.setValue(cal.getTime());
        df.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateServerSideLabel((DateField) event.getProperty());
            }
        });

        Label patternLabel = new Label(pattern);
        patternLabel.setWidth(null);
        SimpleDateFormat expDateFormat = new SimpleDateFormat(
                expectedDateFormat, locale);

        Label expectedLabel = new Label(expDateFormat.format(cal.getTime()));
        if (!pattern.equals(expectedDateFormat)) {
            expectedLabel.setValue(expectedLabel.getValue()
                    + " (differs from JDK)");
        }
        expectedLabel.setWidth(null);

        gridLayout.addComponent(patternLabel);
        gridLayout.addComponent(df);
        gridLayout.addComponent(serversideValueLabel);
        gridLayout.addComponent(expectedLabel);

        updateServerSideLabel(df);
    }

    private void updateServerSideLabel(DateField df) {
        Data data = (Data) df.getData();
        String pattern = data.pattern;
        Locale locale = df.getLocale();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);

        String newValue = formatter.format(df.getValue());
        data.label.setValue(newValue);
    }

    private void addDateField(GridLayout gridLayout, String pattern,
            Locale locale) {
        addDateField(gridLayout, pattern, locale, pattern);
    }

    @Override
    protected String getDescription() {
        return "Test that DateField renders custom date formats the same way as SimpleDateFormat formats them";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5465;
    }
}
