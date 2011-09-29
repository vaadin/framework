package com.vaadin.tests.components.splitpanel;

import java.util.LinkedHashMap;

import com.vaadin.ui.SplitPanel;

@SuppressWarnings("deprecation")
public class SplitPanels extends AbstractSplitPanelTest<SplitPanel> {

    private Command<SplitPanel, Integer> orientationCommand = new Command<SplitPanel, Integer>() {

        public void execute(SplitPanel c, Integer value, Object data) {
            c.setOrientation(value);
        }
    };

    @Override
    protected Class<SplitPanel> getTestClass() {
        return SplitPanel.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createOrientationSelect(CATEGORY_FEATURES);

    }

    private void createOrientationSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Horizontal", SplitPanel.ORIENTATION_HORIZONTAL);
        options.put("Vertical", SplitPanel.ORIENTATION_VERTICAL);
        createSelectAction("Orientation", category, options, "Horizontal",
                orientationCommand);

    }
}
