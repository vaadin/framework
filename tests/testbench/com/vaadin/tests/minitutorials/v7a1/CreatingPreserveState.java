/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Creating%20an%20application
 * %20that%20preserves%20state%20on%20refresh
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class CreatingPreserveState extends Root {
    private static int windowCounter = 0;

    @Override
    public void init(WrappedRequest request) {
        TextField tf = new TextField("Window #" + (++windowCounter));
        tf.setImmediate(true);
        getContent().addComponent(tf);
        getApplication().setRootPreserved(true);
    }

}
