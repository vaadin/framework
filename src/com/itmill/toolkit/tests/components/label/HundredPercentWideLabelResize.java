package com.itmill.toolkit.tests.components.label;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class HundredPercentWideLabelResize extends TestBase {

    @Override
    protected String getDescription() {
        return "100% wide label re-wrap should cause re-layout; forceLayout fixes this.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2514;
    }

    @Override
    protected void setup() {
        getLayout().setWidth("500px");
        Label text = new Label(
                "This is a fairly long text that will wrap if the width of the layout is narrow enough. Directly below the text is a Button - however, when the layout changes size, the Label re-wraps w/o moving the button, causing eiter clipping or a big space.");
        text.setWidth("100%");
        getLayout().addComponent(text);

        getLayout().addComponent(
                new Button("toggle width", new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (getLayout().getWidth() == 500) {
                            getLayout().setWidth("100px");
                        } else {
                            getLayout().setWidth("500px");
                        }

                    }

                }));
    }

}
