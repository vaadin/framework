package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class PopupViewWithRTE extends TestBase {

    @Override
    protected String getDescription() {
        return "Rich text editor should work properly in popupview. Try to edit text below.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3043;
    }

    @Override
    protected void setup() {
        PopupView pv = new PopupView(new Content() {

            RichTextArea rte = new RichTextArea();

            VerticalLayout vl = new VerticalLayout();

            @Override
            public String getMinimizedValueAsHTML() {
                String value = rte.getValue();
                if (value == null || "".equals(value)) {
                    value = "Initial <b>content</b> for <h3>rte</h3>.";
                    rte.setValue(value);
                    rte.setHeight("150px");
                    rte.setWidth("100%");
                    vl.addComponent(rte);
                    vl.setWidth("600px");
                }
                return value.toString();
            }

            @Override
            public Component getPopupComponent() {
                return vl;
            }
        });
        getLayout().addComponent(pv);
    }

}
