package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

@SuppressWarnings("serial")
public class InlineDateFields extends ComponentTestCase<InlineDateField> {

    private static final Locale[] LOCALES = new Locale[] { Locale.US,
            Locale.TAIWAN, new Locale("fi", "FI") };

    @Override
    protected Class<InlineDateField> getTestClass() {
        return InlineDateField.class;
    }

    @Override
    protected void initializeComponents() {

        Locale locale = LOCALES[0];

        InlineDateField pd = createInlineDateField("Undefined width", "-1",
                locale);
        pd.setDebugId("Locale-" + locale.toString() + "-undefined-wide");
        addTestComponent(pd);
        pd = createInlineDateField("300px width", "300px", locale);
        pd.setDebugId("Locale-" + locale.toString() + "-300px-wide");
        addTestComponent(pd);
        pd = createInlineDateField("Initially empty", "", locale);
        pd.setValue(null);
        pd.setDebugId("Locale-" + locale.toString() + "-initially-empty");
        addTestComponent(pd);

    }

    private InlineDateField createInlineDateField(String caption, String width,
            Locale locale) {
        InlineDateField pd = new InlineDateField(caption + "("
                + locale.toString() + ")");
        pd.setWidth(width);
        pd.setValue(new Date(12312312313L));
        pd.setLocale(locale);
        pd.setResolution(DateField.RESOLUTION_YEAR);

        return pd;
    }

    @Override
    protected String getDescription() {
        return "A generic test for InlineDateFields in different configurations";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = super.createActions();
        actions.add(createResolutionSelectAction());
        actions.add(createLocaleSelectAction());
        return actions;
    }

    private Component createResolutionSelectAction() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Year", DateField.RESOLUTION_YEAR);
        options.put("Month", DateField.RESOLUTION_MONTH);
        options.put("Day", DateField.RESOLUTION_DAY);
        options.put("Hour", DateField.RESOLUTION_HOUR);
        options.put("Min", DateField.RESOLUTION_MIN);
        options.put("Sec", DateField.RESOLUTION_SEC);
        options.put("Msec", DateField.RESOLUTION_MSEC);
        return createSelectAction("Resolution", options, "Year",
                new Command<InlineDateField, Integer>() {

                    public void execute(InlineDateField c, Integer value,
                            Object data) {
                        c.setResolution(value);

                    }
                });
    }

    private Component createLocaleSelectAction() {
        LinkedHashMap<String, Locale> options = new LinkedHashMap<String, Locale>();
        for (Locale locale : LOCALES) {
            options.put(locale.toString(), locale);
        }
        return createSelectAction("Locale", options, LOCALES[0].toString(),
                new Command<InlineDateField, Locale>() {

                    public void execute(InlineDateField c, Locale value,
                            Object data) {
                        c.setCaption(c.getCaption().replaceAll(
                                c.getLocale().toString(), value.toString()));
                        c.setLocale(value);

                    }
                });
    }

}
