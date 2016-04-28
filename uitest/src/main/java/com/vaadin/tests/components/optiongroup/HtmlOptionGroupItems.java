package com.vaadin.tests.components.optiongroup;

import java.util.Arrays;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.OptionGroup;

public class HtmlOptionGroupItems extends ComponentTestCase<OptionGroup> {

    private static final List<String> cities = Arrays.asList(new String[] {
            "<i>Berlin</i>", "<b>Brussels</b>", "<u>H</u>elsinki",
            "<span style='font-size: 20px'>Madrid</span>",
            "<pre><i>Oslo</i>\nNorway</pre>", "<button>Paris</button>",
            "<input type='text' value='Stockholm' />" });

    private static final String NULL_SELECTION_ID = cities.get(0);

    @Override
    protected Class<OptionGroup> getTestClass() {
        return OptionGroup.class;
    }

    @Override
    protected void initializeComponents() {

        OptionGroup og = createOptionGroup("");
        og.setItemEnabled(cities.get(2), false);
        og.setItemEnabled(cities.get(5), false);
        og.setValue(Arrays.asList(cities.get(2)));
        og.setNullSelectionAllowed(true);
        og.setNullSelectionItemId(NULL_SELECTION_ID);
        addTestComponent(og);

        og = createOptionGroup("");
        og.setMultiSelect(true);
        og.setHtmlContentAllowed(true);
        og.setValue(Arrays.asList(cities.get(2)));
        og.setNullSelectionAllowed(true);
        og.setItemEnabled(cities.get(2), false);
        og.setItemEnabled(cities.get(5), false);
        addTestComponent(og);

    }

    @Override
    protected void createCustomActions(List<Component> actions) {
        actions.add(createInvertDisabledItemsAction());
        actions.add(createToggleSelectionModeAction());
        actions.add(createInvertHtmlItemsAction());
    }

    private Component createInvertHtmlItemsAction() {
        return createButtonAction("Toggle html mode",
                new Command<OptionGroup, Boolean>() {
                    @Override
                    public void execute(OptionGroup og, Boolean value,
                            Object data) {
                        og.setHtmlContentAllowed(!og.isHtmlContentAllowed());
                    }
                });
    }

    private Component createToggleSelectionModeAction() {
        return createButtonAction("Toggle selection mode",
                new Command<OptionGroup, Boolean>() {

                    @Override
                    public void execute(OptionGroup og, Boolean value,
                            Object data) {
                        if (og.isMultiSelect()) {
                            og.setMultiSelect(false);
                            og.setNullSelectionItemId(NULL_SELECTION_ID);
                        } else {
                            og.setNullSelectionItemId(null);
                            og.setMultiSelect(true);
                        }
                    }
                });
    }

    private Component createInvertDisabledItemsAction() {
        return createButtonAction("Invert disabled items",
                new Command<OptionGroup, Boolean>() {

                    @Override
                    public void execute(OptionGroup c, Boolean value,
                            Object data) {
                        for (Object itemId : c.getItemIds()) {
                            c.setItemEnabled(itemId, !c.isItemEnabled(itemId));
                        }
                    }
                });
    }

    private OptionGroup createOptionGroup(String caption) {
        OptionGroup og = new OptionGroup(caption, cities);
        og.setImmediate(true);
        return og;
    }

    @Override
    protected String getDescription() {
        return "Test case for html items in an OptionGroup";
    }
}
