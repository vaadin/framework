package com.vaadin.tests.components.nativebutton;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Button;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class NativeButtonDisableOnClick extends AbstractTestUI {
    public static String UPDATED_CAPTION = "Updated caption";

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new NativeButton("Click Me");
        button.setId("buttonId");
        button.setDisableOnClick(true);
        button.addClickListener(e -> {
            if (UPDATED_CAPTION.equals(button.getCaption())) {
                button.setCaption("Failed");
            } else {
                button.setCaption(UPDATED_CAPTION);
            }
        });
        addComponent(button);
    }
}
