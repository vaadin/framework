package com.vaadin.tests.components.label;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.themes.Reindeer;

public class LabelStyles extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createPanel(null, 1));
        addComponent(createPanel(null, 2));
        addComponent(createPanel(Reindeer.LABEL_SMALL, 1));
        addComponent(createPanel(Reindeer.LABEL_SMALL, 2));
        addComponent(createPanel(Reindeer.LABEL_H1, 1));
        addComponent(createPanel(Reindeer.LABEL_H1, 2));
        addComponent(createPanel(Reindeer.LABEL_H2, 1));
        addComponent(createPanel(Reindeer.LABEL_H2, 2));

    }

    private Panel createPanel(String styleName, int labels) {
        Panel p = new Panel("");
        if (styleName != null) {
            p.setCaption(styleName);
        }
        GridLayout layout = new GridLayout();
        p.setContent(layout);
        // ((VerticalLayout) p.getContent()).setMargin(false);
        p.setSizeUndefined();
        layout.setSizeUndefined();

        for (int i = 0; i < labels; i++) {
            Label l = new Label("Label " + i);
            if (styleName != null) {
                l.setStyleName(styleName);
            }
            layout.addComponent(l);
        }

        return p;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
