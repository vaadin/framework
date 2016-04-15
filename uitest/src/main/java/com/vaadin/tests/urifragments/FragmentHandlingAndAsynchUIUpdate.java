package com.vaadin.tests.urifragments;

import java.util.Iterator;

import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class FragmentHandlingAndAsynchUIUpdate extends AbstractTestUIWithLog {
    protected static final int START_FRAG_ID = 1000;
    protected static final String FRAG_NAME_TPL = "FRAG%s";
    protected static final String BUTTON_ID = "SetNextFragmentButton";

    private int fragmentId = START_FRAG_ID;

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button(
                "Click Me 10 times, wait for ui to settle (10 seconds) then click the back button 10 times.");

        button.setId(BUTTON_ID);

        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Label c = new Label("Thank you for clicking");
                c.setId(String.valueOf(fragmentId));
                getLayout().addComponent(c);

                log(String.format("Button was clicked for fragmentId %s",
                        fragmentId));
                Page.getCurrent().setUriFragment(
                        String.format(FRAG_NAME_TPL, fragmentId++));
            }
        });

        getLayout().addComponent(button);

        Page.getCurrent().addUriFragmentChangedListener(
                createUriFragmentChangedListener());
    }

    private UriFragmentChangedListener createUriFragmentChangedListener() {
        return new UriFragmentChangedListener() {

            @Override
            public void uriFragmentChanged(final UriFragmentChangedEvent event) {

                log(String.format("uriFragmentChanged %s",
                        event.getUriFragment()));

                if (!event.getUriFragment().startsWith("FRAG")) {
                    return;
                }

                Iterator<Component> it = getLayout().iterator();
                final String frag = event.getUriFragment().substring(4);

                Component fragComp = null;
                while (it.hasNext()) {
                    Component comp = it.next();
                    if (comp.getId() != null && comp instanceof Label
                            && comp.getId().equals(frag)) {
                        fragComp = comp;
                        break;
                    }
                }

                if (fragComp == null) {
                    return;
                }
                final Label fragLabel = (Label) fragComp;

                createThread(frag, fragLabel).start();

                fragLabel.setCaption(String.format("Thread running for %s!",
                        frag));
                UI.getCurrent().setPollInterval(1000);
            }
        };
    }

    private Thread createThread(final String frag, final Label fragLabel) {
        return new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                UI.getCurrent().access(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        log(String.format(
                                "setCaption in synch mode for fragment %s",
                                frag));
                        java.util.Random rand = new java.util.Random();
                        fragLabel.setCaption(String.format(
                                "Thread finished on %s (%s)", frag,
                                rand.nextInt()));
                    }
                });

            };
        };
    }

    @Override
    protected String getTestDescription() {
        return "Back and Forward buttons in browser should work correctly during asynchronous ui update";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13997;
    }
}
