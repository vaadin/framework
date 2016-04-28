package com.vaadin.tests.components.uitest.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.Reindeer;

public class LabelsCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public LabelsCssTest(TestSampler parent) {
        this.parent = parent;
        setSpacing(true);
        setWidth("100%");
        setColumns(5);

        createLabelWith(null, "Default empty label", null, null);
        createLabelWith(null, "Label with icon", null, parent.ICON_URL);
        Label l = createLabelWith("The caption", "With caption and tooltip",
                null, null);
        l.setDescription("The tooltip");

        createLabelWith("H1", ChameleonTheme.LABEL_H1);
        createLabelWith("H2", ChameleonTheme.LABEL_H2);
        createLabelWith("H3", ChameleonTheme.LABEL_H3);
        createLabelWith("H4", ChameleonTheme.LABEL_H4);
        createLabelWith("Big", ChameleonTheme.LABEL_BIG);
        createLabelWith("Small", ChameleonTheme.LABEL_SMALL);
        createLabelWith("Tiny", ChameleonTheme.LABEL_TINY);
        createLabelWith("Color", ChameleonTheme.LABEL_COLOR);
        createLabelWith("Warning", ChameleonTheme.LABEL_WARNING);
        createLabelWith("Error", ChameleonTheme.LABEL_ERROR);
        // Will break test bench as the spinner spins and it's not identical in
        // all screen shots
        // createLabelWith("Loading", ChameleonTheme.LABEL_LOADING);
        createLabelWith("Big", ChameleonTheme.LABEL_BIG);
        createLabelWith("Big", ChameleonTheme.LABEL_BIG);

        createLabelWith("Light", Reindeer.LABEL_SMALL);

    }

    private Label createLabelWith(String content, String primaryStyleName) {
        return createLabelWith(null, content, primaryStyleName, null);
    }

    private Label createLabelWith(String caption, String content,
            String primaryStyleName, String iconUrl) {

        Label l = new Label();
        l.setId("label" + debugIdCounter++);
        if (caption != null) {
            l.setCaption(caption);
        }

        if (content != null) {
            l.setValue(content);
        }

        if (primaryStyleName != null) {
            l.addStyleName(primaryStyleName);
        }

        if (iconUrl != null) {
            l.setIcon(new ThemeResource(iconUrl));
        }

        addComponent(l);
        return l;

    }

    @Override
    public void addComponent(Component component) {
        parent.registerComponent(component);
        super.addComponent(component);
    }

}
