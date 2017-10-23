package com.vaadin.test.osgi.myapplication1;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import org.osgi.service.component.annotations.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("karaftesttheme")
@Widgetset("com.vaadin.test.osgi.widgetset.CustomWidgetSet")
@Push
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(e -> layout.addComponent(
                new Label("Thanks " + name.getValue() + ", it works!")));

        layout.addComponents(name, button);

        setContent(layout);
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                button.setCaption(button.getCaption()+".");
                push();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Component(service = VaadinServlet.class)
    @WebServlet(urlPatterns = "/myapp1/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

}
