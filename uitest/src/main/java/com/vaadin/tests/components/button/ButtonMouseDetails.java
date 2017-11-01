package com.vaadin.tests.components.button;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class ButtonMouseDetails extends TestBase {

    private Label out = new Label("", ContentMode.PREFORMATTED);

    private int clickCounter = 1;

    private Button.ClickListener clickListener = event -> {
        StringBuilder str = new StringBuilder(out.getValue());
        str.append(clickCounter).append(":\t");

        // Modifier keys
        str.append("ctrl=").append(event.isCtrlKey()).append(",\t");
        str.append("alt=").append(event.isAltKey()).append(",\t");
        str.append("meta=").append(event.isMetaKey()).append(",\t");
        str.append("shift=").append(event.isShiftKey()).append(",\t");

        // Coordinates
        str.append("X=").append(event.getRelativeX()).append(",\t");
        str.append("Y=").append(event.getRelativeY()).append(",\t");
        str.append("clientX=").append(event.getClientX()).append(",\t");
        str.append("clientY=").append(event.getClientY());

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
