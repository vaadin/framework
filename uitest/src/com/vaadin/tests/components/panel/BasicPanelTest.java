package com.vaadin.tests.components.panel;

import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class BasicPanelTest extends TestBase {
    private final Label scrollPosition = new Label();
    private final Panel panel = new Panel("Panel caption") {
        @Override
        public void changeVariables(Object source, Map<String, Object> variables) {
            super.changeVariables(source, variables);
            updateLabelText();
        }
    };

    @Override
    protected void setup() {
        getLayout().setWidth("600px");
        getLayout().setHeight("100%");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        actions.addComponent(scrollPosition);
        actions.addComponent(new Button("Sync"));

        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        panel.setContent(panelLayout);

        final CheckBox heightSelection = new CheckBox("Undefined height");
        heightSelection.setImmediate(true);
        heightSelection.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (heightSelection.getValue() == Boolean.TRUE) {
                    panel.setHeight(null);
                } else {
                    panel.setHeight("100%");
                }
            }
        });
        actions.addComponent(heightSelection);

        panel.setWidth("200px");
        panel.setHeight("100%");
        panel.setStyleName(Reindeer.PANEL_LIGHT);

        panelLayout.setCaption("Content caption");

        TextArea textArea = new TextArea("TextArea caption");
        textArea.setWidth("300px");
        textArea.setHeight("500px");
        panelLayout.addComponent(textArea);

        getLayout().addComponent(actions);
        getLayout().addComponent(panel);
        getLayout().setExpandRatio(panel, 1);

        panel.setScrollTop(50);
        panel.setScrollLeft(50);
        panel.setImmediate(true);

        updateLabelText();
    }

    private void updateLabelText() {
        scrollPosition.setValue("Scrolled to " + panel.getScrollTop());
    }

    @Override
    protected String getDescription() {
        return "Simple test for basic panel functionality";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
