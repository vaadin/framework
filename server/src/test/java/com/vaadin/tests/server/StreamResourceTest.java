package com.vaadin.tests.server;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

/**
 *
 * @author Vaadin Ltd
 */
public class StreamResourceTest {

    @Test
    public void testEqualsWithNullFields() {
        StreamResource resource1 = new StreamResource(null, null);
        StreamResource resource2 = new StreamResource(null, null);

        Assert.assertEquals(resource1, resource2);
    }

    @Test
    public void testNotEqualsWithNullFields() {
        StreamResource resource1 = new StreamResource(null, null);
        StreamResource resource2 = new StreamResource(
                EasyMock.createMock(StreamSource.class), "");

        Assert.assertNotEquals(resource1, resource2);
    }

    @Test
    public void testHashCodeForNullFields() {
        StreamResource resource = new StreamResource(null, null);
        // No NPE
        resource.hashCode();
    }

}
