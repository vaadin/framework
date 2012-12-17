package com.vaadin.tests.components.uitest;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BackButtonTest extends AbstractTestCase {

    private LegacyWindow window;
    private VerticalLayout layout;

    private String value = "Hello";
    private Page1 p1;
    private Page2 p2;

    // private UriFragmentUtility urifu;
    @Override
    public final void init() {
        window = new LegacyWindow(getClass().getName()) {
            @Override
            protected void init(VaadinRequest request) {
                super.init(request);
                getPage().setUriFragment("page1");
            }
        };
        setMainWindow(window);

        Label label = new Label(getDescription(), ContentMode.HTML);
        label.setWidth("100%");
        window.addComponent(label);

        layout = new VerticalLayout();
        window.addComponent(layout);

        setup();
    }

    protected void setup() {
        UI ui = UI.getCurrent();
        p1 = new Page1();
        window.addComponent(p1);
        p2 = new Page2();
        ui.getPage().addUriFragmentChangedListener(
                new UriFragmentChangedListener() {

                    @Override
                    public void uriFragmentChanged(UriFragmentChangedEvent event) {
                        String f = event.getUriFragment();
                        if ("page2".equals(f)) {
                            gotoPage2();
                        }

                        if ("page1".equals(f)) {
                            gotoPage1();
                        }
                    }
                });
    }

    class Page1 extends VerticalLayout {
        Label l = new Label();

        public Page1() {
            setSizeFull();
            l.setCaption("Data from Page 1 : " + value);
            addComponent(l);

            Button b = new Button("Go to Page 2", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    l.setCaption("Data from Page 1 : " + value);
                    gotoPage2();
                }
            });
            addComponent(b);
        }
    }

    private void gotoPage2() {
        UI.getCurrent().getPage().setUriFragment("page2");
        window.removeComponent(p1);
        p2.f.setValue("");
        window.addComponent(p2);
    }

    private void gotoPage1() {
        UI.getCurrent().getPage().setUriFragment("page1");
        window.removeComponent(p2);
        window.addComponent(p1);
    }

    class Page2 extends VerticalLayout {
        private final TextField f = new TextField();

        public Page2() {
            setSizeFull();

            addComponent(f);
            f.addValueChangeListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    value = (String) f.getValue();
                    p1.l.setCaption("Data from Page 2 : " + value);
                }
            });

//            Button b = new Button("Go Back", new Button.ClickListener() {
//                public void buttonClick(ClickEvent event) {
//                    gotoPage1();
//                }
//            });
//            addComponent(b);
            addComponent(new Label(
                    "Go back with the back button without creating a blur event on the text field. Text should transfer to page1 label."));
        }

    }

    @Override
    protected String getDescription() {
        return "Browser Back Button should trigger valueChange for TextField";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9949;
    }

}
