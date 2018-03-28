package com.vaadin.tests;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class TestIFrames extends CustomComponent {

    VerticalLayout main = new VerticalLayout();

    public TestIFrames() {
        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(createEmbedded("http://demo.vaadin.com/sampler/"));
        main.addComponent(createEmbedded("../colorpicker"));
        // main.addComponent(createEmbedded("../TestForNativeWindowing"));
        main.addComponent(createEmbedded("http://demo.vaadin.com/timeline"));
        main.addComponent(createEmbedded("http://demo.vaadin.com/colorpicker"));
    }

    private Label createEmbedded(String URL) {
        final int width = 600;
        final int height = 250;
        final String iFrame = "<iframe height=\"" + height + "\" width=\""
                + width + "\" src=\"" + URL + "\" />";
        return new Label(iFrame, ContentMode.HTML);
    }

}
