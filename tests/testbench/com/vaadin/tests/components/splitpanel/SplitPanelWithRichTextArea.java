package com.vaadin.tests.components.splitpanel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalSplitPanel;

public class SplitPanelWithRichTextArea extends TestBase {

    @Override
    protected void setup() {
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setSizeFull();
        RichTextArea rta = new RichTextArea();
        rta.setSizeFull();
        Label label = new Label("One side of the panel");

        sp.setFirstComponent(label);
        sp.setSecondComponent(rta);

        addComponent(sp);
        sp.setSizeFull();
        getLayout().setSizeFull();

    }

    @Override
    protected String getDescription() {
        return "Dragging the splitter should work even if the cursor happens to move over the RichTextArea because of slow updates.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3792;
    }

}
