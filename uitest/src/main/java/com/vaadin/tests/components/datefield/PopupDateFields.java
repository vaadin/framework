package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class PopupDateFields extends ComponentTestCase<LegacyPopupDateField> {

    private static final Locale[] LOCALES = new Locale[] { Locale.US,
            Locale.TAIWAN, new Locale("fi", "FI") };

    @Override
    protected Class<LegacyPopupDateField> getTestClass() {
        return LegacyPopupDateField.class;
    }

    @Override
    protected void initializeComponents() {

        for (Locale locale : LOCALES) {
            LegacyPopupDateField pd = createPopupDateField("Undefined width", "-1",
                    locale);
            pd.setId("Locale-" + locale.toString() + "-undefined-wide");
            addTestComponent(pd);
            pd = createPopupDateField("500px width", "500px", locale);
            pd.setId("Locale-" + locale.toString() + "-500px-wide");
            addTestComponent(pd);
            pd = createPopupDateField("Initially empty", "", locale);
            pd.setValue(null);
            pd.setId("Locale-" + locale.toString() + "-initially-empty");
            addTestComponent(pd);
        }

    }

    private LegacyPopupDateField createPopupDateField(String caption, String width,
            Locale locale) {
        LegacyPopupDateField pd = new LegacyPopupDateField(caption + "("
                + locale.toString() + ")");
        pd.setWidth(width);
        pd.setValue(new Date(12312312313L));
        pd.setLocale(locale);
        pd.setResolution(Resolution.YEAR);

        return pd;
    }

    @Override
    protected String getDescription() {
        return "A generic test for PopupDateFields in different configurations";
    }

    @Override
    protected List<Component> createActions() {
        List<Component> actions = super.createActions();
        actions.add(createResolutionSelectAction());
        actions.add(createInputPromptSelectAction());
        return actions;
    }

    private Component createResolutionSelectAction() {
        LinkedHashMap<String, Resolution> options = new LinkedHashMap<String, Resolution>();
        options.put("Year", Resolution.YEAR);
        options.put("Month", Resolution.MONTH);
        options.put("Day", Resolution.DAY);
        options.put("Hour", Resolution.HOUR);
        options.put("Min", Resolution.MINUTE);
        options.put("Sec", Resolution.SECOND);
        return createSelectAction("Resolution", options, "Year",
                new Command<LegacyPopupDateField, Resolution>() {

                    @Override
                    public void execute(LegacyPopupDateField c, Resolution value,
                            Object data) {
                        c.setResolution(value);

                    }
                });
    }

    private Component createInputPromptSelectAction() {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("<none>", null);
        options.put("Please enter date", "Please enter date");
        options.put("åäöÅÄÖ", "åäöÅÄÖ");

        return createSelectAction("Input prompt", options, "<none>",
                new Command<LegacyPopupDateField, String>() {

                    @Override
                    public void execute(LegacyPopupDateField c, String value,
                            Object data) {
                        c.setInputPrompt(value);

                    }
                });
    }

}
