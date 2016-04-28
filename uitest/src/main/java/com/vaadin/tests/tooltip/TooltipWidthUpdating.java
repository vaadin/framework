package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Test to see if the width of the tooltip element is updated if a narrower
 * tooltip is opened to replace a tooltip with wider content.
 * 
 * @author Vaadin Ltd
 */
public class TooltipWidthUpdating extends AbstractTestUI {

    private static final long serialVersionUID = 1L;
    protected static final String SHORT_TOOLTIP_TEXT = "This is a short tooltip";
    protected static final String LONG_TOOLTIP_TEXT = LoremIpsum.get(5000);
    protected static final Integer MAX_WIDTH = 500;

    @Override
    protected void setup(VaadinRequest request) {
        NativeButton componentWithShortTooltip = new NativeButton(
                "Short tooltip");
        componentWithShortTooltip.setDescription(SHORT_TOOLTIP_TEXT);
        componentWithShortTooltip.setId("shortTooltip");

        getTooltipConfiguration().setMaxWidth(MAX_WIDTH);
        getTooltipConfiguration().setCloseTimeout(200);

        NativeButton componentWithLongTooltip = new NativeButton("Long tooltip");
        componentWithLongTooltip.setId("longTooltip");
        componentWithLongTooltip.setDescription(LONG_TOOLTIP_TEXT);

        VerticalLayout vl = new VerticalLayout();

        TextField component1 = new TextField("TextField");
        component1.setId("component1");
        TextField component2 = new TextField("TextField");
        TextField component3 = new TextField("TextField");
        TextField component4 = new TextField("TextField");
        TextField component5 = new TextField("TextField");
        TextField component6 = new TextField("TextField");
        TextField component7 = new TextField("TextField");
        TextField component8 = new TextField("TextField");

        // some count of any components should be added before (between) buttons
        // to make defect reproducible
        vl.addComponents(component1, component2, component2, component3,
                component4, component5, component5, component6, component7,
                component8);

        getLayout().addComponents(componentWithShortTooltip, vl,
                componentWithLongTooltip);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that tooltip element width is updated if a narrower tooltip is opened to replace a tooltip with wider content";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11871;
    }

}
