package com.vaadin.server;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Assert;
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
        Assert.assertNull("Unexpected resource is got on getUrl() method",
                opener.getUrl());

        URLReference ref = opener.getState(false).resources
                .get(BrowserWindowOpenerState.locationResource);
        Assert.assertTrue("Url reference in the state is not ResourceReference",
                ref instanceof ResourceReference);
        Assert.assertEquals("Unexpected resource saved in state", resource,
                ((ResourceReference) ref).getResource());
    }

    @Test
    public void setUrl_urlBasedOpener_urlIsSet() {
        BrowserWindowOpener opener = new BrowserWindowOpener("url");

        String url = "newUrl";
        opener.setUrl(url);

        assertEquals("Unexpected URL is got on getURL() method", url,
                opener.getUrl());
        Assert.assertNotNull(
                "Unexpected resource is got on getResource() method",
                opener.getResource());

        URLReference ref = opener.getState(false).resources
                .get(BrowserWindowOpenerState.locationResource);
        Assert.assertTrue("Url reference in the state is not ResourceReference",
                ref instanceof ResourceReference);
        Resource resource = ((ResourceReference) ref).getResource();
        Assert.assertTrue("Resource reference is not ExternalResource",
                resource instanceof ExternalResource);
        Assert.assertEquals("Unexpected URL in resource saved in state", url,
                ((ExternalResource) resource).getURL());
    }

}
