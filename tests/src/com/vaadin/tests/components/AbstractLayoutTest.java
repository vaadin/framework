package com.vaadin.tests.components;

import java.util.LinkedHashMap;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Layout.MarginInfo;

public abstract class AbstractLayoutTest<T extends AbstractLayout> extends
        AbstractComponentContainerTest<T> {

    private static final String CATEGORY_LAYOUT_FEATURES = "Layout features";
    private Command<T, MarginInfo> marginCommand = new Command<T, MarginInfo>() {

        public void execute(T c, MarginInfo value, Object data) {
            c.setMargin(value);

        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        createMarginsSelect(CATEGORY_LAYOUT_FEATURES);
    }

    private void createMarginsSelect(String category) {
        LinkedHashMap<String, MarginInfo> options = new LinkedHashMap<String, MarginInfo>();
        options.put("off", new MarginInfo(false));
        options.put("all", new MarginInfo(true));
        options.put("left", new MarginInfo(false, false, false, true));
        options.put("right", new MarginInfo(false, true, false, false));
        options.put("top", new MarginInfo(true, false, false, false));
        options.put("bottom", new MarginInfo(false, false, true, false));
        options.put("left-right", new MarginInfo(false, true, false, true));
        options.put("top-bottom", new MarginInfo(true, false, true, false));

        createSelectAction("Margins", category, options, "off", marginCommand);
    }
}
