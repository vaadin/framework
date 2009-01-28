package com.itmill.toolkit.tests.components.splitpanel;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.Notification;

public class SplitPanelSplitterWidth extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2510;
    }

    @Override
    protected String getDescription() {
        return "SplitPanel splitter is effectively a 1px wide target after unlocking previously locked splitter.";
    }

    @Override
    protected void setup() {
        final SplitPanel split = new SplitPanel(
                SplitPanel.ORIENTATION_HORIZONTAL);
        split.setWidth("200px");
        split.setHeight("200px");
        split.setLocked(true);
        Panel p = new Panel("Left");
        p.setSizeFull();
        split.addComponent(p);
        p = new Panel("Right");
        p.setSizeFull();
        split.addComponent(p);

        final SplitPanel split2 = new SplitPanel();
        split2.setWidth("200px");
        split2.setHeight("200px");
        split2.setLocked(true);
        p = new Panel("Top");
        p.setSizeFull();
        split2.addComponent(p);
        p = new Panel("Bottom");
        p.setSizeFull();
        split2.addComponent(p);

        getLayout().addComponent(
                new Button("Unlock", new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        split.setLocked(false);
                        split2.setLocked(false);
                        getMainWindow().showNotification(
                                "Try moving split. Then reload page.",
                                Notification.TYPE_WARNING_MESSAGE);
                        getLayout().removeComponent(event.getButton());
                    }

                }));
        getLayout().addComponent(split);
        getLayout().addComponent(split2);

    }
}
