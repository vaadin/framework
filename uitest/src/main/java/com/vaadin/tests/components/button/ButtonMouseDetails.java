package com.vaadin.tests.components.button;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class ButtonMouseDetails extends TestBase {

    private Label out = new Label("", ContentMode.PREFORMATTED);

    private int clickCounter = 1;

    private Button.ClickListener clickListener = e -> {
        StringBuilder str = new StringBuilder(out.getValue());
        str.append(clickCounter + ":\t");

        // Modifier keys
        str.append("ctrl=" + e.isCtrlKey() + ",\t");
        str.append("alt=" + e.isAltKey() + ",\t");
        str.append("meta=" + e.isMetaKey() + ",\t");
        str.append("shift=" + e.isShiftKey() + ",\t");

        // Coordinates
        str.append("X=" + e.getRelativeX() + ",\t");
        str.append("Y=" + e.getRelativeY() + ",\t");
        str.append("clientX=" + e.getClientX() + ",\t");
        str.append("clientY=" + e.getClientY());

        str.append("\n");

        out.setValue(str.toString());
        clickCounter++;
    };

    @Override
    protected void setup() {

        getLayout().setSpacing(true);

        Button button = new Button("CLICK ME!", clickListener);
        addComponent(button);

        addComponent(out);
    }

    @Override
    protected String getDescription() {
        return "Clicking a button should returns some additional information about the click";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6605;
    }

}
