package com.vaadin.tests.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout.SpacingHandler;
import com.vaadin.ui.NativeSelect;

public abstract class ComponentTestCase<T extends AbstractComponent> extends
        TestBase {

    private List<T> testComponents = new ArrayList<T>();
    private Class<T> componentClass;

    abstract protected Class<T> getTestClass();

    abstract protected void initializeComponents();

    @Override
    protected final void setup() {
        ((SpacingHandler) getLayout()).setSpacing(true);

        // Create Components
        componentClass = getTestClass();
        initializeComponents();

        // Create actions and add to layout
        addComponent(createActionLayout());

    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    /**
     * Override to provide custom actions for the test case.
     * 
     * @param actions
     *            Array with default actions. Add custom actions to this. Never
     *            null.
     */
    protected void createCustomActions(List<Component> actions) {

    }

    /**
     * Method that creates the "actions" shown in the upper part of the screen.
     * Override this only if you do not want the default actions. Custom actions
     * can be added through #createCustomActions();
     * 
     * @return A List with actions to which more actions can be added.
     */
    protected List<Component> createActions() {
        ArrayList<Component> actions = new ArrayList<Component>();

        actions.add(createEnabledAction(true));
        actions.add(createReadonlyAction(false));

        actions.add(createErrorIndicatorAction(false));
        if (Field.class.isAssignableFrom(componentClass)) {
            actions.add(createRequiredAction(false));
        }

        return actions;
    }

    private Component createActionLayout() {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSpacing(true);
        actionLayout.setMargin(true);
        for (Component c : createActions()) {
            actionLayout.addComponent(c);
            actionLayout.setComponentAlignment(c, Alignment.BOTTOM_LEFT);
        }
        addComponent(actionLayout);
        return actionLayout;
    }

    protected void addTestComponent(T c) {
        testComponents.add(c);
        addComponent(c);
    }

    protected List<T> getTestComponents() {
        return testComponents;
    }

    public interface Command<T, VALUETYPE extends Object> {
        public void execute(T c, VALUETYPE value);

    }

    protected Component createErrorIndicatorAction(boolean initialState) {
        return createCheckboxAction("Error indicators", initialState,
                new Command<T, Boolean>() {
                    public void execute(T c, Boolean enabled) {
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
                new Command<T, Boolean>() {
                    public void execute(T c, Boolean enabled) {
                        c.setEnabled(enabled);
                    }
                });
    }

    protected Component createReadonlyAction(boolean initialState) {
        return createCheckboxAction("Readonly", initialState,
                new Command<T, Boolean>() {
                    public void execute(T c, Boolean enabled) {
                        c.setReadOnly(enabled);
                    }
                });
    }

    protected Component createRequiredAction(boolean initialState) {
        return createCheckboxAction("Required", initialState,
                new Command<T, Boolean>() {
                    public void execute(T c, Boolean enabled) {
                        if (c instanceof Field) {
                            ((Field) c).setRequired(enabled);
                        } else {
                            throw new IllegalArgumentException(
                                    c.getClass().getName()
                                            + " is not a field and cannot be set to required");
                        }
                    }
                });
    }

    protected Component createCheckboxAction(String caption,
            boolean initialState, final Command<T, Boolean> command) {

        CheckBox checkBox = new CheckBox(caption);
        checkBox.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean enabled = (Boolean) event.getButton().getValue();
                doCommand(command, enabled);
            }
        });

        checkBox.setValue(initialState);
        checkBox.setImmediate(true);

        // Set default value for all components
        doCommand(command, initialState);

        return checkBox;
    }

    protected Component createButtonAction(String caption,
            final Command<T, Boolean> command) {

        Button button = new Button(caption);
        button.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean enabled = (Boolean) event.getButton().getValue();
                doCommand(command, enabled);
            }
        });

        button.setImmediate(true);

        return button;
    }

    protected <VALUET> void doCommand(Command<T, VALUET> command, VALUET value) {
        for (T c : getTestComponents()) {
            if (c == null) {
                continue;
            }

            command.execute(c, value);
        }
    }

    protected <TYPE> Component createSelectAction(String caption,
            LinkedHashMap<String, TYPE> options, String initialValue,
            final Command<T, TYPE> command) {
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
                @SuppressWarnings("unchecked")
                TYPE value = (TYPE) item.getItemProperty(VALUE).getValue();
                doCommand(command, value);

            }
        });

        select.setValue(initialValue);

        select.setImmediate(true);

        return select;
    }

    @Override
    protected String getDescription() {
        return "Generic test case for " + componentClass.getSimpleName();
    }
}
