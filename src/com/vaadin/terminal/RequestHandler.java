/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.IOException;
import java.io.Serializable;

import com.vaadin.Application;

public interface RequestHandler extends Serializable {

    boolean handleRequest(Application application, WrappedRequest request, WrappedResponse response)
            throws IOException;

}
