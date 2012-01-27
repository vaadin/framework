package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class PopupDateFields extends ComponentTestCase<PopupDateField> {

    private static final Locale[] LOCALES = new Locale[] { Locale.US,
            Locale.TAIWAN, new Locale("fi", "FI") };

    @Override
    protected Class<PopupDateField> getTestClass() {
        return PopupDateField.class;
    }

    @Override
    protected void initializeComponents() {

        for (Locale locale : LOCALES) {
            PopupDateField pd = createPopupDateField("Undefined width", "-1",
                    locale);
            pd.setDebugId("Locale-" + locale.toString() + "-undefined-wide");
            addTestComponent(pd);
            pd = createPopupDateField("500px width", "500px", locale);
            pd.setDebugId("Locale-" + locale.toString() + "-500px-wide");
            addTestComponent(pd);
            pd = createPopupDateField("Initially empty", "", locale);
            pd.setValue(null);
            pd.setDebugId("Locale-" + locale.toString() + "-initially-empty");
            addTestComponent(pd);
        }

    }

    private PopupDateField createPopupDateField(String caption, String width,
            Locale locale) {
        PopupDateField pd = new PopupDateField(caption + "("
                + locale.toString() + ")");
        pd.setWidth(width);
        pd.setValue(new Date(12312312313L));
        pd.setLocale(locale);
        pd.setResolution(DateField.RESOLUTION_YEAR);

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
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Year", DateField.RESOLUTION_YEAR);
        options.put("Month", DateField.RESOLUTION_MONTH);
        options.put("Day", DateField.RESOLUTION_DAY);
        options.put("Hour", DateField.RESOLUTION_HOUR);
        options.put("Min", DateField.RESOLUTION_MIN);
        options.put("Sec", DateField.RESOLUTION_SEC);
        options.put("Msec", DateField.RESOLUTION_MSEC);
        return createSelectAction("Resolution", options, "Year",
                new Command<PopupDateField, Integer>() {

                    public void execute(PopupDateField c, Integer value,
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
                new Command<PopupDateField, String>() {

                    public void execute(PopupDateField c, String value,
                            Object data) {
                        c.setInputPrompt(value);

                    }
                });
    }

}
