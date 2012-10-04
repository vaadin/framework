package com.vaadin.tests.components.urifragmentutilty;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.VerticalLayout;

public class BrowserBackButton extends TestBase {
    private String value = "Hello";
    private Page1 p1;
    private Page2 p2;
    private UriFragmentUtility urifu;

    @Override
    public void setup() {
        urifu = new UriFragmentUtility();
        addComponent(urifu);
        p1 = new Page1();
        addComponent(p1);
        p2 = new Page2();

        urifu.addListener(new FragmentChangedListener() {
            public void fragmentChanged(FragmentChangedEvent event) {
                String f = event.getUriFragmentUtility().getFragment();
                if ("page2".equals(f)) {
                    gotoPage2();
                }

                if ("page1".equals(f)) {
                    gotoPage1();
                }
            }
        });
        urifu.setFragment("page1");
    }

    class Page1 extends VerticalLayout {
        Label l = new Label();

        public Page1() {
            setSizeFull();
            l.setCaption("Data from Page 1 : " + value);
            this.addComponent(l);

            Button b = new Button("Go to Page 2");
            b.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    l.setCaption("Data from Page 1 : " + value);
                    gotoPage2();
                }
            });
            this.addComponent(b);
        }
    }

    private void gotoPage2() {
        urifu.setFragment("page2");
        removeComponent(p1);
        p2.f.setValue("");
        addComponent(p2);
    }

    private void gotoPage1() {
        urifu.setFragment("page1");
        removeComponent(p2);
        addComponent(p1);
    }

    class Page2 extends VerticalLayout {
        private final TextField f = new TextField();

        public Page2() {
            setSizeFull();

            f.setImmediate(true);
            this.addComponent(f);
            f.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    value = (String) f.getValue();
                    p1.l.setCaption("Data from Page 2 : " + value);
                }
            });

            Button b = new Button("Go Back");
            b.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    gotoPage1();
                }
            });
            this.addComponent(b);
        }

    }

    @Override
    protected String getDescription() {
        return "Browser Back Button does not trigger ValueChangeListener/Blur";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9818;
    }
}
