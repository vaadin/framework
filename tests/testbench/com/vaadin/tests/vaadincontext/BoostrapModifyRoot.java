/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.vaadincontext;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;

public class BoostrapModifyRoot extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getTestDescription() {
        return "There should be a static h1 in the HTML of the bootstrap page for this Root";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9274);
    }

}
