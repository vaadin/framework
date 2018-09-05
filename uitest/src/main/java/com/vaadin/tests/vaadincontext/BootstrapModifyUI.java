package com.vaadin.tests.vaadincontext;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.BootstrapResponse;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

public class BootstrapModifyUI extends AbstractReindeerTestUI {
    private static final String INSTALLED_ATRIBUTE_NAME = BootstrapModifyUI.class
            .getName() + ".installed";

    @Override
    protected void setup(VaadinRequest request) {
        Button c = new Button("Add bootstrap listener", event -> {
            getSession().addBootstrapListener(createBootstrapListener());
            event.getButton().setEnabled(false);
            getSession().setAttribute(INSTALLED_ATRIBUTE_NAME, Boolean.TRUE);
        });
        addComponent(c);
        c.setEnabled(
                getSession().getAttribute(INSTALLED_ATRIBUTE_NAME) == null);
    }

    private static BootstrapListener createBootstrapListener() {
        return new BootstrapListener() {
            @Override
            public void modifyBootstrapFragment(
                    BootstrapFragmentResponse response) {
                if (shouldModify(response)) {
                    Element heading = new Element(Tag.valueOf("div"), "")
                            .text("Added by modifyBootstrapFragment");
                    response.getFragmentNodes().add(0, heading);
                }
            }

            private boolean shouldModify(BootstrapResponse response) {
                Class<? extends UI> uiClass = response.getUiClass();
                boolean shouldModify = uiClass == BootstrapModifyUI.class;
                return shouldModify;
            }

            @Override
            public void modifyBootstrapPage(BootstrapPageResponse response) {
                if (shouldModify(response)) {
                    response.getDocument().body().child(0)
                            .before("<div>Added by modifyBootstrapPage</div>");
                }
            }
        };
    }

    @Override
    protected String getTestDescription() {
        return "There should be two additional divs in the HTML of the bootstrap page for this UI";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9274);
    }

}
