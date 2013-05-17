package com.vaadin.tests.components.uitest;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class BackButtonTest extends AbstractTestUI {

    private VerticalLayout layout;

    private String value = "Hello";
    private Page1 p1;
    private Page2 p2;

    @Override
    public void setup(VaadinRequest request) {
        getPage().setUriFragment("page1");

        layout = new VerticalLayout();
        addComponent(layout);

        p1 = new Page1();
        addComponent(p1);

        p2 = new Page2();
        getPage().addUriFragmentChangedListener(
                new UriFragmentChangedListener() {

                    @Override
                    public void uriFragmentChanged(UriFragmentChangedEvent event) {
                        String f = event.getUriFragment();
                        if ("page2".equals(f)) {
                            showPage2();
                        }

                        if ("page1".equals(f)) {
                            showPage1();
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
                @Override
                public void buttonClick(ClickEvent event) {
                    l.setCaption("Data from Page 1 : " + value);
                    getPage().setUriFragment("page2");
                }
            });
            addComponent(b);
        }
    }

    private void showPage2() {
        removeComponent(p1);
        p2.f.setValue("");
        addComponent(p2);
    }

    private void showPage1() {
        removeComponent(p2);
        addComponent(p1);
    }

    class Page2 extends VerticalLayout {
        private final TextField f = new TextField();

        public Page2() {
            setSizeFull();

            addComponent(f);
            f.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    value = f.getValue();
                    p1.l.setCaption("Data from Page 2 : " + value);
                }
            });

            Button b = new Button("Go Back", new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    getPage().setUriFragment("page1");
                }
            });
            addComponent(b);
            addComponent(new Label(
                    "Go back with the back button without creating a blur event on the text field. Text should transfer to page1 label."));
        }

    }

    @Override
    protected Integer getTicketNumber() {
        return 9949;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

}
