package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.IdTestLabel;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
@Theme("tests-tickets")
public class IdOverrideTest extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Id shouldn't get overridden unless specifically re-set.<br>"
                + "First two are custom labels with a default id, third is an ordinary label for comparison.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10179;
    }

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);
        getLayout().setMargin(new MarginInfo(true, false, false, false));

        final IdTestLabel idTestLabel = new IdTestLabel("default id");
        idTestLabel.setSizeUndefined();
        addComponent(idTestLabel);

        final IdTestLabel idTestLabelWithId = new IdTestLabel("set id");
        idTestLabelWithId.setSizeUndefined();
        idTestLabelWithId.setId("set10179");
        idTestLabelWithId.setImmediate(true);
        addComponent(idTestLabelWithId);

        final Label label = new Label("no id");
        label.setSizeUndefined();
        addComponent(label);

        Button button = new Button();
        button.setCaption("Toggle");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (idTestLabelWithId.getId() == null) {
                    idTestLabelWithId.setId("set10179");
                    idTestLabelWithId.setValue("set id");
                    idTestLabel.setValue("default id");
                    label.setValue("no id");
                } else {
                    idTestLabelWithId.setId(null);
                    idTestLabelWithId.setValue("removed id");
                    idTestLabel.setValue("still default id");
                    label.setValue("still no id");
                }
            }
        });
        button.setId("toggle");
        button.setImmediate(true);
        addComponent(button);
    }

}
