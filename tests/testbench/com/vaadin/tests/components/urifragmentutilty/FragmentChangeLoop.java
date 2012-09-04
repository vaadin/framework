package com.vaadin.tests.components.urifragmentutilty;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.VerticalLayout;

public class FragmentChangeLoop extends TestBase {
    VerticalLayout layout1 = new VerticalLayout();
    VerticalLayout layout2 = new VerticalLayout();
    VerticalLayout layoutCurrent = null;
    UriFragmentUtility uriFragmentUtility = new UriFragmentUtility();
    Integer i = 0;
    private Label status;
    private Button setToF;
    private Button addOne;

    @Override
    protected void setup() {
        uriFragmentUtility.addListener(new FragmentChangedListener() {

            public void fragmentChanged(FragmentChangedEvent source) {
                status.setValue(++i + " fragment events");
                VerticalLayout layoutNew = (layoutCurrent == layout1 ? layout2
                        : layout1);
                replaceComponent(layoutCurrent, layoutNew);
                layoutCurrent = layoutNew;

            }
        });

        layout1.setCaption("Layout 1");
        layout2.setCaption("Layout 2");
        addComponent(layout1);
        layoutCurrent = layout1;
        status = new Label("0 fragment events");
        setToF = new Button("Set fragment to F", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                uriFragmentUtility.setFragment("F");
            }
        });
        addOne = new Button("Append '1' to fragment",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        uriFragmentUtility.setFragment(uriFragmentUtility
                                .getFragment() + "1");
                    }
                });
        addComponent(status);
        addComponent(setToF);
        addComponent(addOne);

        addComponent(uriFragmentUtility);

    }

    @Override
    protected String getDescription() {
        return "Click the button to set the fragment to F. "
                + "This should cause the counter on the button to increase by one (unless the fragment is already F in case it should do nothing).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8916;
    }

}
