package com.vaadin.tests.components.button;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

/*
 * NOTE This class is arbitrarily picked to represent a legacy application in
 * MultipleServletConfigurationTest and the corresponding "Embed App 1" servlet
 * configuration. The test will break if this class is refactored to extend UI
 * instead of LegacyApplication. Just a friendly warning.
 */
public class ButtonHtml extends TestBase {

    @Override
    protected void setup() {
        Button b = new Button("<b>Plain text button</b>");
        addComponent(b);

        b = new Button(
                "<span style=\"color: red; font-weight: bold;\">HTML</span> button");
        b.setCaptionAsHtml(true);
        addComponent(b);

        final Button swapButton = new Button("<i>Swap button<i>");
        swapButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                swapButton.setCaptionAsHtml(!swapButton.isCaptionAsHtml());
            }
        });
        addComponent(swapButton);
    }

    @Override
    protected String getDescription() {
        return "Verify that Button HTML rendering works";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8663;
    }
}
