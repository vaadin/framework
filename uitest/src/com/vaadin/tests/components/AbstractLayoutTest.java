package com.vaadin.tests.components;

import java.util.LinkedHashMap;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout.AlignmentHandler;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.Layout.SpacingHandler;

public abstract class AbstractLayoutTest<T extends AbstractLayout> extends
        AbstractComponentContainerTest<T> {

    protected static final String CATEGORY_LAYOUT_FEATURES = "Layout features";
    private Command<T, MarginInfo> marginCommand = new Command<T, MarginInfo>() {

        @Override
        public void execute(T c, MarginInfo value, Object data) {
            ((MarginHandler) c).setMargin(value);

        }
    };

    protected Command<T, Boolean> spacingCommand = new Command<T, Boolean>() {
        @Override
        public void execute(T c, Boolean value, Object data) {
            ((SpacingHandler) c).setSpacing(value);
        }
    };

    private Command<T, Integer> setComponentAlignment = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object alignment) {
            Component child = getComponentAtIndex(c, value);
            ((AlignmentHandler) c).setComponentAlignment(child,
                    (Alignment) alignment);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        if (MarginHandler.class.isAssignableFrom(getTestClass())) {
            createMarginsSelect(CATEGORY_LAYOUT_FEATURES);
        }
        if (SpacingHandler.class.isAssignableFrom(getTestClass())) {
            createSpacingSelect(CATEGORY_LAYOUT_FEATURES);
        }
        if (AlignmentHandler.class.isAssignableFrom(getTestClass())) {
            createChangeComponentAlignmentAction(CATEGORY_LAYOUT_FEATURES);
        }

    }

    private void createMarginsSelect(String category) {
        LinkedHashMap<String, MarginInfo> options = new LinkedHashMap<String, MarginInfo>();
        options.put("off", new MarginInfo(false));
        options.put("all", new MarginInfo(true));
        options.put("left", new MarginInfo(false, false, false, true));
        options.put("right", new MarginInfo(false, true, false, false));
        options.put("top", new MarginInfo(true, false, false, false));
        options.put("bottom", new MarginInfo(false, false, true, false));
        options.put("left-right", new MarginInfo(false, true));
        options.put("top-bottom", new MarginInfo(true, false));

        createSelectAction("Margins", category, options, "off", marginCommand);
    }

    private void createSpacingSelect(String category) {
        createBooleanAction("Spacing", category, false, spacingCommand);
    }

    private void createChangeComponentAlignmentAction(String category) {
        String alignmentCategory = "Component alignment";
        createCategory(alignmentCategory, category);

        LinkedHashMap<String, Alignment> options = new LinkedHashMap<String, Alignment>();
        options.put("Top left", Alignment.TOP_LEFT);
        options.put("Top center", Alignment.TOP_CENTER);
        options.put("Top right", Alignment.TOP_RIGHT);

        options.put("Middle left", Alignment.MIDDLE_LEFT);
        options.put("Middle center", Alignment.MIDDLE_CENTER);
        options.put("Middle right", Alignment.MIDDLE_RIGHT);

        options.put("Bottom left", Alignment.BOTTOM_LEFT);
        options.put("Bottom center", Alignment.BOTTOM_CENTER);
        options.put("Bottom right", Alignment.BOTTOM_RIGHT);

        for (int i = 0; i < 20; i++) {
            String componentAlignmentCategory = "Component " + i + " alignment";
            createCategory(componentAlignmentCategory, alignmentCategory);

            for (String option : options.keySet()) {
                createClickAction(option, componentAlignmentCategory,
                        setComponentAlignment, Integer.valueOf(i),
                        options.get(option));
            }

        }

    }
}
