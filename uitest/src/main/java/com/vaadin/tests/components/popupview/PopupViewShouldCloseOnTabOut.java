package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.v7.ui.LegacyTextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PopupViewShouldCloseOnTabOut extends TestBase {

    @Override
    protected String getDescription() {
        return "The PopupView should close when the user moves focus away from it using the TAB key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5059;
    }

    @Override
    protected void setup() {
        PopupView pv = new PopupView(new Content() {

            @Override
            public String getMinimizedValueAsHTML() {
                return "<b>click me</b>";
            }

            @Override
            public Component getPopupComponent() {
                VerticalLayout vl = new VerticalLayout();
                LegacyTextField field1 = new LegacyTextField();
                field1.setValue("one");
                field1.focus();
                vl.addComponent(field1);
                LegacyTextField field2 = new LegacyTextField();
                field2.setValue("two");
                vl.addComponent(field2);
                vl.setWidth("600px");
                return vl;
            }
        });
        addComponent(pv);
        LegacyTextField main = new LegacyTextField();
        main.setValue("main");
        addComponent(main);
        LegacyTextField main2 = new LegacyTextField();
        main2.setValue("main2");
        addComponent(main2);
    }

}
