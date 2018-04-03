package com.vaadin.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.BrowserWindowOpenerState;

/**
 *
 * @author Vaadin Ltd
 */
public class BrowserWindowOpenerTest {

    @Test
    public void setResource_urlBasedOpener_resourceIsSetAndUrlIsNull() {
        BrowserWindowOpener opener = new BrowserWindowOpener("url");

        StreamResource resource = EasyMock.createMock(StreamResource.class);
        opener.setResource(resource);

        assertEquals("Unexpected resource is got on getResource() method",
                resource, opener.getResource());
        assertNull("Unexpected resource is got on getUrl() method",
                opener.getUrl());

        URLReference ref = opener.getState(false).resources
                .get(BrowserWindowOpenerState.locationResource);
        assertTrue("Url reference in the state is not ResourceReference",
                ref instanceof ResourceReference);
        assertEquals("Unexpected resource saved in state", resource,
                ((ResourceReference) ref).getResource());
    }

    @Test
    public void setUrl_urlBasedOpener_urlIsSet() {
        BrowserWindowOpener opener = new BrowserWindowOpener("url");

        String url = "newUrl";
        opener.setUrl(url);

        assertEquals("Unexpected URL is got on getURL() method", url,
                opener.getUrl());
        assertNotNull("Unexpected resource is got on getResource() method",
                opener.getResource());

        URLReference ref = opener.getState(false).resources
                .get(BrowserWindowOpenerState.locationResource);
        assertTrue("Url reference in the state is not ResourceReference",
                ref instanceof ResourceReference);
        Resource resource = ((ResourceReference) ref).getResource();
        assertTrue("Resource reference is not ExternalResource",
                resource instanceof ExternalResource);
        assertEquals("Unexpected URL in resource saved in state", url,
                ((ExternalResource) resource).getURL());
    }

}
