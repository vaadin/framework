package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.UserError;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;

public class PopupDateFields extends ComponentTestCase<PopupDateField> {

    private static final Locale[] LOCALES = new Locale[] { Locale.US,
            Locale.TAIWAN, new Locale("fi", "FI") };

    @Override
    protected void setup() {
        super.setup();

        for (Locale locale : LOCALES) {
            PopupDateField pd = createPopupDateField("Undefined width", "-1",
                    locale);
            addTestComponent(pd);
            pd = createPopupDateField("500px width", "500px", locale);
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
        ArrayList<Component> actions = new ArrayList<Component>();
        actions.add(createErrorIndicatorAction(false));
        actions.add(createEnabledAction(true));
        actions.add(createRequiredAction(false));
        actions.add(createReadonlyAction(false));
        actions.add(createResolutionSelectAction());
        actions.add(createInputPromptSelectAction());

        return actions;
    }

    public interface Command<T extends Component, VALUETYPE extends Object> {
        public void execute(T c, VALUETYPE value);

    }

    protected Component createErrorIndicatorAction(boolean initialState) {
        return createCheckboxAction("Error indicators", initialState,
                new Command<PopupDateField, Boolean>() {
                    public void execute(PopupDateField c, Boolean enabled) {
                        if (enabled) {
                            c.setComponentError(new UserError("It failed!"));
                        } else {
                            c.setComponentError(null);

                        }
                    }

                });
    }

    protected Component createEnabledAction(boolean initialState) {
        return createCheckboxAction("Enabled", initialState,
                new Command<PopupDateField, Boolean>() {
                    public void execute(PopupDateField c, Boolean enabled) {
                        c.setEnabled(enabled);
                    }
                });
    }

    protected Component createReadonlyAction(boolean initialState) {
        return createCheckboxAction("Readonly", initialState,
                new Command<PopupDateField, Boolean>() {
                    public void execute(PopupDateField c, Boolean enabled) {
                        c.setReadOnly(enabled);
                    }
                });
    }

    protected Component createRequiredAction(boolean initialState) {
        return createCheckboxAction("Required", initialState,
                new Command<PopupDateField, Boolean>() {
                    public void execute(PopupDateField c, Boolean enabled) {
                        c.setRequired(enabled);
                    }
                });
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

                    public void execute(PopupDateField c, Integer value) {
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

                    public void execute(PopupDateField c, String value) {
                        c.setInputPrompt(value);

                    }
                });
    }

    private <T> Component createSelectAction(String caption,
            LinkedHashMap<String, T> options, String initialValue,
            final Command<PopupDateField, T> command) {
        final String CAPTION = "caption";
        final String VALUE = "value";

        final NativeSelect select = new NativeSelect(caption);
        select.addContainerProperty(CAPTION, String.class, "");
        select.addContainerProperty(VALUE, Object.class, "");
        select.setItemCaptionPropertyId(CAPTION);
        select.setNullSelectionAllowed(false);
        for (String itemCaption : options.keySet()) {
            Object itemId = new Object();
            Item i = select.addItem(itemId);
            i.getItemProperty(CAPTION).setValue(itemCaption);
            i.getItemProperty(VALUE).setValue(options.get(itemCaption));
            if (itemCaption.equals(initialValue)) {
                select.setValue(itemId);
            }

        }
        select.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                Object itemId = event.getProperty().getValue();
                Item item = select.getItem(itemId);
                T value = (T) item.getItemProperty(VALUE).getValue();
                doCommand(command, value);

            }
        });

        select.setValue(initialValue);

        select.setImmediate(true);

        return select;
    }

    protected Component createCheckboxAction(String caption,
            boolean initialState, final Command<PopupDateField, Boolean> command) {
        CheckBox errorIndicators = new CheckBox(caption,
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        boolean enabled = (Boolean) event.getButton()
                                .getValue();
                        doCommand(command, enabled);
                    }
                });

        errorIndicators.setValue(initialState);
        errorIndicators.setImmediate(true);

        return errorIndicators;
    }

    protected <VALUET> void doCommand(Command<PopupDateField, VALUET> command,
            VALUET value) {
        for (PopupDateField c : getTestComponents()) {
            if (c == null) {
                continue;
            }

            command.execute(c, value);

        }

    }
}
