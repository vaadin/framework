package com.vaadin.test.widgetset;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractTestWidgetSetUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        final Label widgetsetInfo = new Label();
        widgetsetInfo.setId("widgetsetinfo");
        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                layout.addComponent(
                        new Label("Thanks " + name.getValue() + ", it works!"));
            }
        });

        getPage().getJavaScript().execute(
                "widgetsetinfo.innerText=document.querySelector('iframe').id;");
        layout.addComponents(widgetsetInfo, name, button);
        layout.setMargin(true);
        layout.setSpacing(true);

        setContent(layout);
    }

}
