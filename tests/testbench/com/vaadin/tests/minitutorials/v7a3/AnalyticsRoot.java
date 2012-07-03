/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

public class AnalyticsRoot extends Root {

    @Override
    protected void init(WrappedRequest request) {
        final Analytics analytics = new Analytics("UA-33036133-12");
        analytics.extend(this);

        addComponent(new Button("Track pageview", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                analytics.trackPageview("/fake/url");
            }
        }));
    }

}
