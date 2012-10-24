package com.vaadin.tests.extensions;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.BrowserPopupOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.popupview.ReopenPopupView;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;

public class BrowserPopupExtensionTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
        components.add(Button.class);
        components.add(NativeButton.class);
        components.add(Link.class);
        components.add(CssLayout.class);
        components.add(Label.class);
        addComponents(components, "http://vaadin.com/download/nightly/");

        Button uiClassButton = new Button("Open UI class");
        new BrowserPopupOpener(ReopenPopupView.class).extend(uiClassButton);
        addComponent(uiClassButton);

        Button uiWithPath = new Button("Open UI class with path");
        new BrowserPopupOpener(ReopenPopupView.class, "foobar")
                .extend(uiWithPath);
        addComponent(uiWithPath);

        Button withPopupFeaturesButton = new Button("Open with features");
        BrowserPopupOpener featuresPopup = new BrowserPopupOpener(
                "http://vaadin.com/download/nightly/");
        featuresPopup.setFeatures("width=400,height=400");
        featuresPopup.extend(withPopupFeaturesButton);
        addComponent(withPopupFeaturesButton);
    }

    public void addComponents(List<Class<? extends Component>> components,
            String URL) {
        final HorizontalLayout hl = new HorizontalLayout();
        for (Class<? extends Component> cls : components) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                c.setId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(URL);
                c.setWidth("100px");
                c.setHeight("100px");
                hl.addComponent(c);

                new BrowserPopupOpener(URL).extend(c);

                if (c instanceof Button) {
                    ((Button) c).addClickListener(new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Could not instatiate " + cls.getName());
            }
        }
        addComponent(hl);
    }

    @Override
    protected String getTestDescription() {
        return "Test for " + BrowserPopupOpener.class.getSimpleName()
                + " features";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
