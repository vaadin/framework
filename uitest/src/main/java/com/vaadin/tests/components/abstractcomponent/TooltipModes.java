package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;

/**
 * @author Vaadin Ltd
 *
 */
public class TooltipModes extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button label = new Button("Label. Hover to see tooltip");
        label.setDescription("Several\n lines\n tooltip");
        addComponent(label);

        Button useHtml = new Button("Use Html in the tooltip",
                event -> label.setDescription(
                        "<div>Html <b><span>tooltip</span></b></div>",
                        ContentMode.HTML));
        addComponent(useHtml);

        Button usePreformatted = new Button("Use plain text in the tooltip",
                event -> label.setDescription("<b>tooltip</b>",
                        ContentMode.TEXT));
        addComponent(usePreformatted);
    }

}
