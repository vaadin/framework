package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateField.Resolution;
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

        InlineDateField hidden = new InlineDateField();
        hidden.setVisible(false); // Used to break rest of layout #8693
        addComponent(hidden);

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
        pd.setResolution(DateField.Resolution.YEAR);

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
        LinkedHashMap<String, Resolution> options = new LinkedHashMap<String, Resolution>();
        options.put("Year", DateField.Resolution.YEAR);
        options.put("Month", DateField.Resolution.MONTH);
        options.put("Day", DateField.Resolution.DAY);
        options.put("Hour", DateField.Resolution.HOUR);
        options.put("Min", DateField.Resolution.MINUTE);
        options.put("Sec", DateField.Resolution.SECOND);
        return createSelectAction("Resolution", options, "Year",
                new Command<InlineDateField, Resolution>() {

                    public void execute(InlineDateField c, Resolution value,
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
