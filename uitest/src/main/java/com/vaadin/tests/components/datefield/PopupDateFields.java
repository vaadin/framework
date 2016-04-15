package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
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

    private PopupDateField createPopupDateField(String caption, String width,
            Locale locale) {
        PopupDateField pd = new PopupDateField(caption + "("
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
                new Command<PopupDateField, Resolution>() {

                    @Override
                    public void execute(PopupDateField c, Resolution value,
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

                    @Override
                    public void execute(PopupDateField c, String value,
                            Object data) {
                        c.setInputPrompt(value);

                    }
                });
    }

}
