package com.vaadin.tests.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.HasValue;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.NativeSelect;

public abstract class ComponentTestCase<T extends AbstractComponent>
        extends AbstractComponentTestCase<T> {

    protected static final Object CAPTION = "caption";

    private HorizontalLayout actionLayout;

    @Override
    protected final void setup(VaadinRequest request) {
        // Create action layout so it appears before the components
        actionLayout = createActionLayout();
        addComponent(actionLayout);

        super.setup(request);

        // Create actions and add to layout
        populateActionLayout();
    }

    protected void populateActionLayout() {
        for (Component c : createActions()) {
            addAction(c);
        }

    }

    private void addAction(Component c) {
        actionLayout.addComponent(c);
        actionLayout.setComponentAlignment(c, Alignment.BOTTOM_LEFT);
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
        List<Component> actions = new ArrayList<>();

        actions.add(createEnabledAction(true));
        actions.add(createReadonlyAction(false));

        actions.add(createErrorIndicatorAction(false));
        if (HasValue.class.isAssignableFrom(getTestClass())
                || Field.class.isAssignableFrom(getTestClass())) {
            actions.add(createRequiredAction(false));
        }

        createCustomActions(actions);

        return actions;
    }

    private HorizontalLayout createActionLayout() {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSpacing(true);
        actionLayout.setMargin(true);

        return actionLayout;
    }

    protected Component createErrorIndicatorAction(boolean initialState) {
        return createBooleanAction("Error indicators", initialState,
                errorIndicatorCommand);
    }

    protected Component createEnabledAction(boolean initialState) {
        return createBooleanAction("Enabled", initialState, enabledCommand);
    }

    protected Component createReadonlyAction(boolean initialState) {
        return createBooleanAction("Readonly", initialState, readonlyCommand);
    }

    protected Component createRequiredAction(boolean initialState) {
        return createBooleanAction("Required", initialState, requiredCommand);
    }

    protected Component createBooleanAction(String caption,
            boolean initialState, final Command<T, Boolean> command) {

        CheckBox checkBox = new CheckBox(caption);
        checkBox.addValueChangeListener(
                event -> doCommand(command, event.getValue()));

        checkBox.setValue(initialState);

        checkBox.setId("checkboxaction-" + caption);
        // Set default value for all components
        doCommand(command, initialState);

        return checkBox;
    }

    protected Component createButtonAction(String caption,
            final Command<T, Boolean> command) {

        Button button = new Button(caption);
        button.setData(Boolean.FALSE);
        button.addClickListener(event -> {
            Button b = event.getButton();
            boolean state = (Boolean) b.getData();
            b.setData(!state);
            doCommand(command, state);
        });

        button.setId("buttonaction-" + caption);

        return button;
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
        select.addValueChangeListener(event -> {
            Object itemId = event.getProperty().getValue();
            Item item = select.getItem(itemId);
            @SuppressWarnings("unchecked")
            TYPE value = (TYPE) item.getItemProperty(VALUE).getValue();
            doCommand(command, value);
        });

        for (String itemCaption : options.keySet()) {
            Object itemId = new Object();
            Item i = select.addItem(itemId);
            i.getItemProperty(CAPTION).setValue(itemCaption);
            i.getItemProperty(VALUE).setValue(options.get(itemCaption));
            if (itemCaption.equals(initialValue)) {
                select.setValue(itemId);
            }
        }

        select.setId("selectaction-" + caption);

        select.setImmediate(true);

        return select;
    }

}
