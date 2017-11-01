package com.vaadin.tests.components.nativebutton;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.NativeButton;

public class NativeButtonHtml extends TestBase {

    @Override
    protected void setup() {
        NativeButton b = new NativeButton("<b>Plain text button</b>");
        addComponent(b);

        b = new NativeButton(
                "<span style=\"color: red; font-weight: bold;\">HTML</span> button");
        b.setCaptionAsHtml(true);
        addComponent(b);

        final NativeButton swapButton = new NativeButton("<i>Swap button<i>");
        swapButton.addClickListener(event -> swapButton
                .setCaptionAsHtml(!swapButton.isCaptionAsHtml()));
        addComponent(swapButton);
    }

    @Override
    protected String getDescription() {
        return "Verify that NativeButton HTML rendering works";
    }

    @Override
    protected Integer getTicketNumber() {
        // 8663 was for normal button (see ButtonHtml test)
        return null;
    }
}
