package com.vaadin.tests.components.button;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class ButtonMouseDetails extends TestBase {

    private Label out = new Label("", ContentMode.PREFORMATTED);

    private int clickCounter = 1;

    private Button.ClickListener clickListener = event -> {
        String str = out.getValue();
        str += clickCounter + ":\t";

        // Modifier keys
        str += "ctrl=" + event.isCtrlKey() + ",\t";
        str += "alt=" + event.isAltKey() + ",\t";
        str += "meta=" + event.isMetaKey() + ",\t";
        str += "shift=" + event.isShiftKey() + ",\t";

        // Coordinates
        str += "X=" + event.getRelativeX() + ",\t";
        str += "Y=" + event.getRelativeY() + ",\t";
        str += "clientX=" + event.getClientX() + ",\t";
        str += "clientY=" + event.getClientY();

        str += '\n';

        out.setValue(str);
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
